package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.time.Duration;
import java.util.Dictionary;

import java.util.Hashtable;

import static java.lang.Double.parseDouble;
import static java.lang.Thread.sleep;

public class strategia2 extends stockInfo implements buySell {
    String[] codes;
    AlpacaAPI alpacaAPI;

    Dictionary<String, Double> boughtFor;
    Dictionary<String, Integer> myStocks;

    public strategia2(String[] codes, AlpacaAPI alpacaAPI, Dictionary<String, Double> boughtFor, Dictionary<String, Integer> myStocks) {
        this.codes = codes;
        this.alpacaAPI = alpacaAPI;
        this.boughtFor = boughtFor;
        this.myStocks = myStocks;
    }


    public void execute() throws InterruptedException {

        double cash;

        for (String code : codes) {
            if (stockInfo.getAssetQuantity(alpacaAPI, code) != -1) {
                myStocks.put(code, stockInfo.getAssetQuantity(alpacaAPI, code));
            }
        }


        Dictionary<String, Double> isItWorth = new Hashtable<>();

        Dictionary<String, Double> currentPrices = stockInfo.getPrices(alpacaAPI,codes);

        for (String code : codes) {
            isItWorth.put(code, stockInfo.averages(alpacaAPI, code)
            );
        }


        //check if we successfully logged in
        if (login(alpacaAPI).isPresent()) {
            final var account = login(alpacaAPI).get();
            cash = parseDouble(account.getCash());
            if (stockInfo.isOpen(alpacaAPI)) {
                //we buy and sell only if stock exchange is open


                //first we consider selling
                for (String code : codes) {
                    if (myStocks.get(code) > 0 && boughtFor.get(code) < currentPrices.get(code)) {
                        if (sell(alpacaAPI, code, myStocks.get(code)) == 1) {
                            myStocks.put(code, 0);
                        }
                    }
                    System.out.println();

                }

                sleep(1000);
                //after selling, now we consider buying some stocks
                for (String code : codes) {
                    if (isItWorth.get(code) > 1) {
                        if (buy(alpacaAPI, code, cash, currentPrices.get(code), myStocks) == 1 && cash > 0) {
                            boughtFor.put(code, currentPrices.get(code));
                            cash = cash -  (cash / currentPrices.get(code) / 10);
                        }
                    }
                }


            }
        }

        sleep(Duration.ofMinutes(10));

    }

    @Override
    public int buy(AlpacaAPI alpacaAPI, String code, double cash, double currentPrice, Dictionary<String, Integer> myStocks) {
        final var ordersEndpoint = alpacaAPI.orders();

        try {
            System.out.println((int) (cash / currentPrice / 20));
            int howMuch = (int) (cash / currentPrice / 20);
            if (howMuch > 0) {
                ordersEndpoint.requestMarketOrder(
                        code,
                        howMuch,
                        OrderSide.BUY,
                        OrderTimeInForce.GOOD_UNTIL_CANCELLED);
                myStocks.put(code, howMuch);
                return 1;
            } else return -1;
        } catch (AlpacaClientException e) {
            return -1;
        }
    }

    @Override
    public int sell(AlpacaAPI alpacaAPI, String code, Integer number) {
        final var ordersEndpoint = alpacaAPI.orders();

        try {
            ordersEndpoint.requestMarketOrder(
                    code,
                    number,
                    OrderSide.SELL,
                    OrderTimeInForce.GOOD_UNTIL_CANCELLED);
            return 1;
        } catch (AlpacaClientException e) {
            return -1;
        }
    }
}
