package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        var measurements = Files.lines(Paths.get("1.in"))
                .mapToInt(Integer::parseInt)
                .toArray();

        System.out.println("Day 1a: " + calculate(measurements, 1));
        System.out.println("Day 1b: " + calculate(measurements, 3));
    }

    static int calculate(int[] measurements, int offset) {
        var count = 0;

        for (int i = 0; i < measurements.length - offset; i++) {
            if (measurements[i] < measurements[i + offset]) {
                count++;
            }
        }

        return count;
    }
}