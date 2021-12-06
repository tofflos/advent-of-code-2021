package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        var counts = new long[9];

        Arrays.stream(Files.readString(Paths.get("6.in")).split(","))
                .map(Integer::parseInt)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .forEach((k, v) -> counts[k] = v);

        System.out.println("Part 1: " + calculate(counts, 80));
        System.out.println("Part 2: " + calculate(counts, 256));
    }
    
    static long calculate(long [] counts, int days) {
        var arr = Arrays.copyOf(counts, counts.length);

        for (int i = 0; i < days; i++) {
            var zeroes = arr[0];

            System.arraycopy(arr, 1, arr, 0, 8);
            arr[6] += zeroes;
            arr[8]  = zeroes;

        }
        
        return Arrays.stream(arr).sum();
    }
}