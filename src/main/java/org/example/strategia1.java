package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.time.Duration;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Thread.sleep;

public class strategia1 extends stockInfo implements buySell {

    String[] stockCodes;
    AlpacaAPI alpacaAPI;
    Dictionary<String, Double> boughtFor;
    Dictionary<String, Integer> myStocks;

    public strategia1(String[] stockCodes, AlpacaAPI alpacaAPI, Dictionary<String, Double> boughtFor, Dictionary<String, Integer> myStocks) {
        this.stockCodes = stockCodes;
        this.alpacaAPI = alpacaAPI;
        this.boughtFor = boughtFor;
        this.myStocks = myStocks;
    }

    public Dictionary<String, Double> getBoughtFor() {
        return boughtFor;
    }

    public void execute() throws InterruptedException, RuntimeException {

        double cash;

        //updating how much stocks i have
        for (String code : stockCodes) {
            if (stockInfo.getAssetQuantity(alpacaAPI, code) != -1) {
                myStocks.put(code, stockInfo.getAssetQuantity(alpacaAPI, code));
            }
        }

        Dictionary<String, Double> isItWorth = new Hashtable<>();
        Dictionary<String, Double> currentPrices = stockInfo.getPrices(alpacaAPI,stockCodes);

        //calculating stock score
        for (String code : stockCodes) {
            isItWorth.put(code, stockInfo.
                    strategy1Evaluation(alpacaAPI,
                            currentPrices.get(code),
                            code)
            );
        }


        //check if we succesfully logged in
        if (stockInfo.login(alpacaAPI).isPresent()) {
            final var konto = stockInfo.login(alpacaAPI).get();
            cash = parseDouble(konto.getCash());
        } else {
            throw new RuntimeException();
        }

        if (stockInfo.isOpen(alpacaAPI)) {
            //we buy and sell only if stock exchange is open

            //first we consider selling
            for (String code : stockCodes) {
                if (myStocks.get(code) > 0 && boughtFor.get(code) < currentPrices.get(code)) {
                    if (sell(alpacaAPI, code, myStocks.get(code)) == 1) {
                        myStocks.put(code, 0);
                    }
                }
            }

            sleep(1000);
            //after selling, now we consider buying some stocks
            for (String code : stockCodes) {
                if (isItWorth.get(code) > 1) {
                    if (buy(alpacaAPI, code, cash, currentPrices.get(code), myStocks) == 1 && cash > 0) {
                        boughtFor.put(code, currentPrices.get(code));
                        cash = cash - ((cash / currentPrices.get(code)) / 10);
                    }
                }
            }

        }


        sleep(Duration.ofMinutes(5));


    }

    @Override
    public int buy(AlpacaAPI alpacaAPI, String code, double cash, double currentPrice, Dictionary<String, Integer> myStocks) {
        final var ordersEndpoint = alpacaAPI.orders();

        try {
            int howMuch = (int) (cash / currentPrice / 10);
            if (howMuch > 0) {
                ordersEndpoint.requestMarketOrder(
                        code,
                        howMuch,
                        OrderSide.BUY,
                        OrderTimeInForce.GOOD_UNTIL_CANCELLED);
                myStocks.put(code, howMuch);
                return 1;
            }
            else return -1;
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
