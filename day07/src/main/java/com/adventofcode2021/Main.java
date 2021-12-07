package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.BiFunction;

public class Main {

    public static void main(String[] args) throws IOException {
        var crabs = Arrays.stream(Files.readString(Paths.get("7.in")).split(","))
                .mapToInt(Integer::parseInt).toArray();

        System.out.println("Part 1: " + calculate(crabs, (p1, p2) -> Math.abs(p1 - p2)));
        System.out.println("Part 2: " + calculate(crabs, (p1, p2) -> {
            var delta = Math.abs(p1 - p2);
            var cost = 0;

            for (int i = 0; i <= delta; i++) {
                cost += i;
            }

            return cost;
        }));
    }

    static long calculate(int[] crabs, BiFunction<Integer, Integer, Integer> grr) {
        var min = 0;
        var max = Arrays.stream(crabs).max().orElseThrow();
        var result = Integer.MAX_VALUE;

        for (int i = min; i < max; i++) {
            var cost = 0;

            for (int j = 0; j < crabs.length; j++) {
                cost += grr.apply(crabs[j], i);
            }

            if (cost < result) {
                result = cost;
            }
        }

        return result;
    }
}
