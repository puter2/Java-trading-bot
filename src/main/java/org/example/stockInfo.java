package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.time.ZonedDateTime;
import java.util.*;

public class stockInfo {

    //this class contains every method that returns some information about stock exchange or my portfolio

    public static int getAssetQuantity(AlpacaAPI alpacaAPI, String name) {
        try {
            return Integer.parseInt(alpacaAPI.positions().getBySymbol(name).getQuantity());
        } catch (AlpacaClientException e) {
            return 0;
        }
    }

    public static Boolean isOpen(AlpacaAPI alpacaAPI) {
        try {
            return alpacaAPI.clock().get().getIsOpen();
        } catch (AlpacaClientException e) {
            return false;
        }
    }

    public static Dictionary<String,Double> getPrices(AlpacaAPI alpacaAPI, String... stocks) {
        Dictionary<String,Double> prices = new Hashtable<>();
        try {
            for (String stock : stocks) {
                prices.put(
                        stock,
                        alpacaAPI.stockMarketData().getLatestTrade(stock).getTrade().getPrice()
                );
            }
            return prices;
        } catch (AlpacaClientException e) {
            throw new RuntimeException();
        }
    }

    public static Double strategy1Evaluation(AlpacaAPI alpacaAPI, Double price, String code) {
        final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();
        try {
            final var stockBarsResponse = stockMarketDataEndpoint.getBars(
                    code,
                    ZonedDateTime.now().minusDays(4),
                    ZonedDateTime.now(),
                    null,
                    null,
                    10,
                    BarTimePeriod.MINUTE,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            //if there were no trades in last few days, stockBarsResponse is null
            if (stockBarsResponse.getBars() == null) return -100.;

            double average1 = (stockBarsResponse.getBars().get(1).getHigh() - stockBarsResponse.getBars().get(1).getLow()) / 2;
            double priceChange = 0;
            //priceChange is the score of a stock

            //we calculate score based on if the average price is ascending or descending
            for (StockBar info : stockBarsResponse.getBars().stream().skip(1).toList()) {
                double average2 = (info.getHigh() - info.getLow()) / 2;
                priceChange += average2 - average1;
                average1 = average2;
            }

            //if price is higher in comparison with last 10 minutes we add a point to score
            if (price - average1 > 0) priceChange++;
            else priceChange--;


            return priceChange;
        } catch (AlpacaClientException e) {
            throw new RuntimeException(e);
        }

    }

    public static double averages(AlpacaAPI alpacaAPI, String code) {
        final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();
        try {
            final var bars = stockMarketDataEndpoint.getBars(code,
                    ZonedDateTime.now().minusDays(4),
                    ZonedDateTime.now(),
                    null,
                    null,
                    10,
                    BarTimePeriod.MINUTE,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            List<Double> prices = new ArrayList<>();

            for (StockBar stockBar : bars.getBars())
                prices.add(stockBar.getHigh());

            final var calculator = new calculator();
            //we calculate two averages over two periods of time
            final var averageBig = calculator.average(prices, bars.getBars().size());
            final var averageSmall = calculator.average(prices, 10);
            //if the result is less than zero we assume that the stock is going down
            return averageSmall - averageBig;
        } catch (AlpacaClientException e) {
            return -1.;
        }
    }

    public static Optional<Account> login(AlpacaAPI alpacaAPI) {
        try {
            Account account = alpacaAPI.account().get();
            return Optional.of(account);
        } catch (AlpacaClientException e) {
            //if signing in didn't work we return empty
            return Optional.empty();
        }
    }
}
