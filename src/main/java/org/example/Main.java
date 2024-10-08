package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;

import java.time.LocalDate;
import java.util.*;


public class Main {


    public static void main(String[] args) {
        final var alpacaAPI = new AlpacaAPI();

        final String[] stockCodes = {"AAPL", "TSLA", "NVDA", "PM",
                "PPC", "PRM", "PSA", "PZZA",
                "QUAD", "RBBN", "RELL"};    //array of stock codes I'll be trading

        Dictionary<String, Double> boughtFor = new Hashtable<>();      //how much did i pay a for certain stock
        Dictionary<String, Integer> myStocks = new Hashtable<>();     //how much stocks i possess

        //in the beginning we don't have any stocks and we didn't buy any
        for (String code : stockCodes) {
            boughtFor.put(code, 0.0);
            myStocks.put(code, 0);
        }

        final var strategia2 = new strategia2(stockCodes, alpacaAPI, boughtFor, myStocks);
        final var strategia1 = new strategia1(stockCodes, alpacaAPI, boughtFor, myStocks);

        while (true) {
            try {
                if (LocalDate.now().getDayOfMonth() % 2 == 1) {
                    strategia2.execute();
                } else {
                    strategia1.execute();
                }
            } catch (InterruptedException | RuntimeException ignored) {

            }
        }

    }
}