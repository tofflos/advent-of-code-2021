package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var fish = Arrays.stream(Files.readString(Paths.get("6.in")).split(","))
                .map(Integer::parseInt)
                .toList();

        var frequencies = new long[9];

        fish.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().forEach(entry -> frequencies[entry.getKey()] = entry.getValue());

        System.out.println("Part 1: " + calculate(frequencies, 80));
        System.out.println("Part 2: " + calculate(frequencies, 256));
    }
    
    static long calculate(long [] frequencies, int rounds) {
        var current = Arrays.copyOf(frequencies, frequencies.length);

        for (int i = 0; i < rounds; i++) {
            var zeroes = current[0];

            System.arraycopy(current, 1, current, 0, 8);
            current[6] += zeroes;
            current[8] = zeroes;

        }
        
        return Arrays.stream(current).sum();
    }
}