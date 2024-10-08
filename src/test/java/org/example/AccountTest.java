package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import static org.example.strategia1.getAssetQuantity;

public class AccountTest {
    @Test
    void connect() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var accountEndpoint = alpacaAPI.account();

            final Account account;
            account = accountEndpoint.get();

            final var accountNumber = account.getAccountNumber();
            Assertions.assertEquals("PA33WGVL7OEG", accountNumber);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void bars() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();
            final var stockBarsResponse = stockMarketDataEndpoint.getBars(
                    "AAPL",
                    ZonedDateTime.now().minusWeeks(1),
                    ZonedDateTime.now(),
                    null,
                    null,
                    1,
                    BarTimePeriod.DAY,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            System.out.println(stockBarsResponse.getBars());
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void bars2() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();
            final var stockBarsResponse = stockMarketDataEndpoint.getBars(
                    "AAPL",
                    ZonedDateTime.now().minusHours(6),
                    ZonedDateTime.now(),
                    null,
                    null,
                    10,
                    BarTimePeriod.MINUTE,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            System.out.println(stockBarsResponse.getBars());
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void latestTrade() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();

            final var latestTrade = stockMarketDataEndpoint.getLatestTrade("AAPL").getTrade().getPrice();

            System.out.println(latestTrade);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void latestQuote() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();

            final var latestQuote = stockMarketDataEndpoint.getLatestQuote("AAPL");

            System.out.println(latestQuote);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void place() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();

            final var marketOrder = ordersEndpoint.requestMarketOrder(
                    "AAPL",
                    10,
                    OrderSide.BUY,
                    OrderTimeInForce.DAY);

            System.out.println(marketOrder);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void sell() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();
            final var marketSell = ordersEndpoint.requestMarketOrder(
                    "TSLA",
                    10,
                    OrderSide.SELL,
                    OrderTimeInForce.DAY);
            System.out.println(marketSell);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void clock() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            // Get the market 'Clock' and print it out
            Clock clock = alpacaAPI.clock().get();
            System.out.println(clock);
        } catch (AlpacaClientException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void status() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var buyingPower = alpacaAPI.account().get().getBuyingPower();
            System.out.println(buyingPower);
        } catch (AlpacaClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void stoporder() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();

            final var stopOrder = ordersEndpoint.requestStopOrder(
                    "AAPL",
                    20,
                    OrderSide.BUY,
                    OrderTimeInForce.DAY,
                    210.0,
                    false
            );
        } catch (AlpacaClientException e) {
            System.out.println("blad");
            Assertions.fail();
        }

    }

    @Test
    void stoplimitorder() {

        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();
            final var stopLimitOrder = ordersEndpoint.requestStopLimitOrder(
                    "SNAP",
                    20,
                    OrderSide.BUY,
                    OrderTimeInForce.DAY,
                    31.5,
                    30.0,
                    false
            );
        } catch (AlpacaClientException e) {
            System.out.println("blad");
            throw new RuntimeException(e);
        }
    }


    @Test
    void limitorder() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();
            final var limitOrder = ordersEndpoint.requestLimitOrder(
                    "AAPL",
                    10.0,
                    OrderSide.BUY,
                    OrderTimeInForce.GOOD_UNTIL_CANCELLED,
                    200.0,
                    false
            );
        } catch (AlpacaClientException e) {
            System.out.println("blad");
            Assertions.fail();
        }
    }

    @Test
    void traillingstoporder() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();
            final var traillingstop = ordersEndpoint.requestTrailingStopPriceOrder(
                    "AAPL",
                    20,
                    OrderSide.BUY,
                    OrderTimeInForce.DAY,
                    20.,
                    false
            );

        } catch (AlpacaClientException e) {
            System.out.println("blad");
            Assertions.fail();
        }
    }

    @Test
    void assety() throws AlpacaClientException {
        final var alpacaAPI = new AlpacaAPI();
        final var account = alpacaAPI.account();
        final var a = alpacaAPI.assets().getBySymbol("PRM");
        final var b = alpacaAPI.positions().getBySymbol("A");

        System.out.println(b);

    }
//
    @Test
    void asset() {
        final String[] nazwy = {"AAPL", "TSLA", "NVDA", "PM", "PPC", "PRM", "PSA", "PZZA", "QUAD", "RBBN", "RELL"};
        final var alpacaAPI = new AlpacaAPI();

        for (String nazwa : nazwy) {
            if (getAssetQuantity(alpacaAPI, nazwa) != -1)
                System.out.println(nazwa + getAssetQuantity(alpacaAPI, nazwa));
        }
    }

    @Test
    void get_asset_quantitytest() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final String name = "AAPL";

            System.out.println(alpacaAPI.positions().getBySymbol(name).getQuantity());
            System.out.println(Integer.parseInt(alpacaAPI.positions().getBySymbol(name).getQuantity()));
        } catch (AlpacaClientException e) {
            throw new RuntimeException();
        }
    }

    @Test
    void get_quantity(){
        final String[] nazwy = {"AAPL", "TSLA", "NVDA", "PM", "PPC", "PRM", "PSA", "PZZA", "QUAD", "RBBN", "RELL"};
        Dictionary<String,Integer> myStocks = new Hashtable<>();
        final var alpaca = new AlpacaAPI();
        for( String code : nazwy)
            myStocks.put(code,stockInfo.getAssetQuantity(alpaca,code));

        System.out.println(myStocks);
    }

    @Test
    void optional() {
        Optional<Integer> v;

    }


    @ParameterizedTest
    @ValueSource(strings = {"AAPL", "TSLA", "NVDA", "PM",
            "PPC", "PRM", "PSA", "PZZA",
            "QUAD", "RBBN", "RELL"})
    void strategy1Evaluation(String nazwa) {
        final var alpacaAPI = new AlpacaAPI();

        final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();
        try {
            final var stockBarsResponse = stockMarketDataEndpoint.getBars(
                    nazwa,
                    ZonedDateTime.now().minusDays(4),
                    ZonedDateTime.now(),
                    null,
                    null,
                    10,
                    BarTimePeriod.MINUTE,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            //if there were no trades in last few days, stockBarsResponse is null
            if (stockBarsResponse.getBars() == null) System.out.println("error");

            double srednia1 = (stockBarsResponse.getBars().get(1).getHigh() - stockBarsResponse.getBars().get(1).getLow()) / 2;
            double zmiana_w_cenie = 0;
            //badam czy cena urosła czy zmalała
            for (StockBar info : stockBarsResponse.getBars().stream().skip(1).toList()) {
                double srednia2 = (info.getHigh() - info.getLow()) / 2;
                zmiana_w_cenie += srednia2 - srednia1;
                srednia1 = srednia2;
            }
            //jesli ostatnia zmiana ceny była na plus to dodaj punkt
            if (alpacaAPI.stockMarketData().getLatestTrade(nazwa).getTrade().getPrice() - srednia1 > 0)
                zmiana_w_cenie++;
            else zmiana_w_cenie--;
            //stockBarsResponse.getBars().get(1).;

            //zwracamy defacto czy cena urosła przez 6h czy zmalała przez 6h
            System.out.println(zmiana_w_cenie);

        } catch (AlpacaClientException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void day() {

        System.out.println(LocalDate.now().getDayOfMonth());
    }

    @Test
    void isOpen(){
        final var alpaca = new AlpacaAPI();
        try {
            System.out.println(alpaca.clock().get().getIsOpen());
        } catch (AlpacaClientException e) {
            throw new RuntimeException(e);
        }
    }

}
