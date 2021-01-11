//Author: Tushar Jaiswal
//Creation Date: 01/10/2021

/*Runtime Complexity:
  * createCurrencyGraph is O(|Conversions|)
  * getConversionRate is O(|Vertices| * |Edges|) as each neighbor of the Currency to be converted can start the traversal of the entire graph
Space Complexity: O(|Vertices| + |Edges|) of the currency graph*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;

public class CurrencyConversion {
    HashMap<String, List<Pair<String, Double>>> currencyGraph;

    public static void main(String[] args) {
        List<Conversion> conversions = new ArrayList<>();
        conversions.add(new Conversion("ETH", "USD", 5, 6));
        conversions.add(new Conversion("ETH", "BTC", 2, 3));
        conversions.add(new Conversion("BTC", "USD", 3, 4));
        CurrencyConversion currencyConversion = new CurrencyConversion(conversions);
        String from = "ETH";
        String to = "USD";
        double amount = currencyConversion.getConversionRate(from, to);
        System.out.println(String.format("1.0 %s = %s %s", from, amount, to));

        conversions.add(new Conversion("A", "B", 2, 3));
        conversions.add(new Conversion("A", "C", 5, 6));
        conversions.add(new Conversion("B", "C", 3, 4));
        conversions.add(new Conversion("C", "D", 2, 3));
        currencyConversion = new CurrencyConversion(conversions);
        from = "A";
        to = "D";
        amount = currencyConversion.getConversionRate(from, to);
        System.out.println(String.format("1.0 %s = %s %s", from, amount, to));

        amount = currencyConversion.getConversionRate("USD", "ETH");
        System.out.println(String.format("1.0 %s = %s %s", "USD", amount, "ETH"));
    }

    public CurrencyConversion(List<Conversion> conversions) {
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
}
