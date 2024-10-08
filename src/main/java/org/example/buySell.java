package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;

import java.util.Dictionary;

public interface buySell {
    int buy(AlpacaAPI alpacaAPI, String code, double cash, double currentPrice, Dictionary<String, Integer> myStocks);
    int sell(AlpacaAPI alpacaAPI, String code, Integer number);
}
