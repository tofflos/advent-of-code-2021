package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        var frequencies = new long[9];

        Arrays.stream(Files.readString(Paths.get("6.in")).split(","))
                .map(Integer::parseInt)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .forEach((k, v) -> frequencies[k] = v);

        System.out.println("Part 1: " + calculate(frequencies, 80));
        System.out.println("Part 2: " + calculate(frequencies, 256));
    }
    
    static long calculate(long [] frequencies, int rounds) {
        var copy = Arrays.copyOf(frequencies, frequencies.length);

        for (int i = 0; i < rounds; i++) {
            var zeroes = copy[0];

            System.arraycopy(copy, 1, copy, 0, 8);
            copy[6] += zeroes;
            copy[8]  = zeroes;

        }
        
        return Arrays.stream(copy).sum();
    }
}