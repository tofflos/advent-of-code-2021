package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        var report = Files.readAllLines(Paths.get("3.in"));
        var width = report.get(0).length();
        var gamma = new StringBuffer();
        var epsilon = new StringBuffer();

        for (int w = 0; w < width; w++) {
            var ones = ones(report, w);
            var zeroes = report.size() - ones;

            gamma.append(ones > zeroes ? 1 : 0);
            epsilon.append(zeroes > ones ? 1 : 0);
        }

        System.out.println("Part 1: " + Integer.parseInt(gamma.toString(), 2) * Integer.parseInt(epsilon.toString(), 2));

        var current = report;

        for (int i = 0; i < width; i++) {
            var ones = ones(current, i);
            var zeroes = current.size() - ones;
            var next = new ArrayList<String>();

            if(current.size() == 1) {
                break;
            }

            for (var row : current) {
                if (ones > zeroes && '1' == row.charAt(i)) {
                    next.add(row);
                }
                if (zeroes > ones && '0' == row.charAt(i)) {
                    next.add(row);
                }
                if (ones == zeroes && '1' == row.charAt(i)) {
                    next.add(row);
                }
            }

            current = next;
        }

        var oxygen = Integer.parseInt(current.get(0), 2);
        
        current = report;

        for (int i = 0; i < width; i++) {
            var ones = ones(current, i);
            var zeroes = current.size() - ones;
            var next = new ArrayList<String>();
            
            if(current.size() == 1) {
                break;
            }

            for (var row : current) {
                if (zeroes < ones && '0' == row.charAt(i)) {
                    next.add(row);
                }
                if (ones < zeroes && '1' == row.charAt(i)) {
                    next.add(row);
                }
                if (ones == zeroes && '0' == row.charAt(i)) {
                    next.add(row);
                }
            }

            current = next;
        }

        var co2 = Integer.parseInt(current.get(0), 2);

        System.out.println("Part 2: " + oxygen * co2);
    }

    static long ones(List<String> report, int index) {
        return report.stream().filter(s -> '1' == s.charAt(index)).count();
    }
}
