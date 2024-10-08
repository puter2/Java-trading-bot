package org.example;

import java.util.List;

public class calculator {
    double average(List<Double> Data, int n) {
        double sum = 0.0;
        for (int i = Data.size() - 1; i > Data.size() - n; i--) {
            sum += Data.get(i);
        }
        return sum / n;
    }
}
