package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        var measurements = Files.lines(Paths.get("1.in"))
                .mapToInt(Integer::parseInt)
                .toArray();

        var increments = 0;

        for (int i = 0; i < measurements.length - 1; i++) {
            if (measurements[i] < measurements[i + 1]) {
                increments++;
            }
        }

        System.out.println("Day 1a: " + increments);

        increments = 0;

        for (int i = 0; i < measurements.length - 3; i++) {
            var sum1 = measurements[i + 0] + measurements[i + 1] + measurements[i + 2];
            var sum2 = measurements[i + 1] + measurements[i + 2] + measurements[i + 3];

            if (sum1 < sum2) {
                increments++;
            }
        }

        System.out.println("Day 1b: " + increments);
    }
}