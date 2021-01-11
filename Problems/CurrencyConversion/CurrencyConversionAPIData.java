//Author: Tushar Jaiswal
//Creation Date: 01/10/2021

/*Runtime Complexity:
  * createCurrencyGraph is O(|Conversions|)
  * getConversionRate is O(|Vertices| * |Edges|) as each neighbor of the Currency to be converted can start the traversal of the entire graph
Space Complexity: O(|Vertices| + |Edges|) of the currency graph*/

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrencyConversionAPIData {
    private HashMap<String, List<Pair<String, Double>>> currencyGraph;

    private static final String CONVERSIONS_LIST_ENDPOINT = "https://api.pro.coinbase.com/products";
    private static final String CONVERSION_ENDPOINT_SUFFIX = "book";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final static Logger LOGGER = LoggerFactory.getLogger(CurrencyConversionAPIData.class.getName());

    public static void main(String[] args) {
        CurrencyConversionAPIData currencyConversion = new CurrencyConversionAPIData();
        String from = "ETH";
        String to = "USD";
        double amount = currencyConversion.getConversionRate(from, to);
        System.out.println(String.format("1.0 %s = %s %s", from, amount, to));

        amount = currencyConversion.getConversionRate(to, from);
        System.out.println(String.format("1.0 %s = %s %s", to, amount, from));
    }

    public CurrencyConversionAPIData() {
        List<Conversion> conversions = getConversionsList();
        currencyGraph = createCurrencyGraph(conversions);
    }

    HashMap<String, List<Pair<String, Double>>> createCurrencyGraph(List<Conversion> conversions) {
        HashMap<String, List<Pair<String, Double>>> currencyGraph = new HashMap<>();

        for (Conversion conversion : conversions) {
            if (!currencyGraph.containsKey(conversion.currencyA)) {
                currencyGraph.put(conversion.currencyA, new ArrayList<>());
            }
            if (!currencyGraph.containsKey(conversion.currencyB)) {
                currencyGraph.put(conversion.currencyB, new ArrayList<>());
            }
            currencyGraph.get(conversion.currencyA).add(new Pair<>(conversion.currencyB, conversion.bid));
            currencyGraph.get(conversion.currencyB).add(new Pair<>(conversion.currencyA, 1/conversion.ask));
        }

        return currencyGraph;
    }

    private double getConversionRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }

        double bestRate = -1.0;
        HashSet<String> visitedNodes = new HashSet<>();
        visitedNodes.add(fromCurrency);

        for (Pair<String, Double> neighbor : currencyGraph.get(fromCurrency)) {
            double rate = dfsTraversal(toCurrency, neighbor.key, neighbor.value, visitedNodes);
            bestRate = Math.max(rate, bestRate);
        }

        if (bestRate == -1) {
            throw new IllegalArgumentException(String.format("There is no conversion from %s to %s.", fromCurrency, toCurrency));
        }

        return bestRate;
    }

    private double dfsTraversal(String targetNode, String currNode, double amount, HashSet<String> visitedNodes) {
        if (currNode.equals(targetNode)) {
            return amount;
        }

        double maxAmount = -1;

        for (Pair<String, Double> neighbor : currencyGraph.get(currNode)) {
            if (!visitedNodes.contains(neighbor.key)) {
                visitedNodes.add(currNode);
                double currAmount = dfsTraversal(targetNode, neighbor.key, amount * neighbor.value, visitedNodes);
                visitedNodes.remove(currNode);

                maxAmount = Math.max(maxAmount, currAmount);
            }
        }

        return maxAmount;
    }

    private List<Conversion> getConversionsList() {
        List<String> conversionIDs;
        try {
            conversionIDs = getConversionIDs();
        } catch (IOException e) {
            throw new RuntimeException("Could not get Currency Conversion Data.");
        }

        List<Conversion> conversions = new ArrayList<>();
        for (String conversionID : conversionIDs) {
            conversions.add(getConversion(conversionID));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return conversions;
    }

    private List<String> getConversionIDs() throws IOException {
        List<String> conversionIDs = new ArrayList<>();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(CONVERSIONS_LIST_ENDPOINT);
            CloseableHttpResponse response = client.execute(request);
            JsonNode jsonNode = MAPPER.readTree(response.getEntity().getContent());

            for (JsonNode conversion : jsonNode) {
                conversionIDs.add(conversion.get("id").asText());
            }
        }

        return conversionIDs;
    }

    private Conversion getConversion(String conversionID) {
        Conversion conversion = null;

        try {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                String conversionEndpoint = String.format("%s/%s/%s", CONVERSIONS_LIST_ENDPOINT, conversionID, CONVERSION_ENDPOINT_SUFFIX);
                HttpGet request = new HttpGet(conversionEndpoint);
                CloseableHttpResponse response = client.execute(request);
                JsonNode jsonNode = MAPPER.readTree(response.getEntity().getContent());

                System.out.println(conversionID);

                String[] currencies = conversionID.split("-");
                String currencyA = currencies[0];
                String currencyB = currencies[1];
                double bid = jsonNode.get("bids").get(0).get(0).asDouble();
                double ask = jsonNode.get("asks").get(0).get(0).asDouble();

                conversion = new Conversion(currencyA, currencyB, bid, ask);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("API call to get Bid Ask spread for %s failed.", conversionID));
        }

        return conversion;
    }
}
