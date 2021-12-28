package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var initial = Files.lines(Paths.get("25.in")).map(String::toCharArray).toArray(char[][]::new);

        System.out.println("Part 1: " + part1(initial));
    }

    static int part1(char[][] initial) {

        var current = Arrays.stream(initial).map(char[]::clone).toArray(char[][]::new);
        var n = 1;

        while (true) {
            var east = Arrays.stream(current).map(char[]::clone).toArray(char[][]::new);

            for (int y = 0; y < current.length; y++) {
                for (int x = 0; x < current[y].length; x++) {
                    char c = current[y][x];

                    if (c == '>' && current[y][(x + 1) % current[y].length] == '.') {
                        east[y][(x + 1) % current[y].length] = c;
                        east[y][x] = '.';
                    }
                }
            }

            var south = Arrays.stream(east).map(char[]::clone).toArray(char[][]::new);

            for (int y = 0; y < east.length; y++) {
                for (int x = 0; x < east[y].length; x++) {
                    char c = east[y][x];

                    if (c == 'v' && east[(y + 1) % east.length][x] == '.') {
                        south[(y + 1) % current.length][x] = c;
                        south[y][x] = '.';
                    }
                }
            }

            if(Arrays.deepEquals(current, south)) {
                break;
            }
            
            current = Arrays.stream(south).map(char[]::clone).toArray(char[][]::new);
            n++;
        }

        return n;
    }

    static String toString(char[][] arr) {
        return Arrays.stream(arr).map(String::copyValueOf).collect(Collectors.joining(System.getProperty("line.separator")));
    }
}
