//Author: Tushar Jaiswal
//Creation Date: 01/10/2021

public class Conversion {
    String currencyA;
    String currencyB;

    /**
     * If you sell 1 unit of currencyA, the amount of currencyB that you will get.
     */
    double bid;

    /**
     * Amount of currencyB needed to buy 1 unit of currencyA.
     */
    double ask;

    public Conversion(String currencyA, String currencyB, double bid, double ask) {
        this.currencyA = currencyA;
        this.currencyB = currencyB;
        this.bid = bid;
        this.ask = ask;
    }
}
