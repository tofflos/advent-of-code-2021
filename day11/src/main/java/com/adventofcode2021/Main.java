package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        var energies = Files.lines(Paths.get("11.in"))
                .map(s -> Arrays.stream(s.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        System.out.println("Part 1: " + part1(energies));
        System.out.println("Part 2: " + part2(energies));
        
    }

    static int part1(int[][] energies) {
        var current = new Result(0, energies);
        var flashes = 0;
        
        for (int n = 0; n < 100; n++) {
            var next = process(current.energies());
            
            flashes += next.flashes();
            current = next;
        }
        
        return flashes;
    }
    
    static int part2(int[][] energies) {
        var current = new Result(0, energies);
        var rounds = 0;

        while(!Arrays.stream(current.energies()).flatMapToInt(Arrays::stream).allMatch(energy -> energy == 0)) {
            var next = process(current.energies());

            rounds++;
            current = next;
        }        
        
        return rounds;
    }

    static Result process(int[][] energies) {
        var arr = Arrays.stream(energies).map(int[]::clone).toArray(int[][]::new);
        var directions = new int[][]{{1, -1}, {1, 0}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};
        var flashed = new boolean[energies.length][energies[0].length];
        var flashes = 0;

        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[y].length; x++) {
                arr[y][x]++;
            }
        }

        while (Arrays.stream(arr).flatMapToInt(Arrays::stream).anyMatch(energy -> energy > 9)) {
            for (int y = 0; y < arr.length; y++) {
                for (int x = 0; x < arr[y].length; x++) {

                    if (!flashed[y][x] && arr[y][x] > 9) {
                        flashed[y][x] = true;
                        arr[y][x] = 0;

                        for (var direction : directions) {
                            var px = x + direction[0];
                            var py = y + direction[1];

                            if (0 <= px && px < arr[y].length && 0 <= py && py < arr.length && !flashed[py][px]) {
                                flashed[y][x] = true;
                                arr[y][x] = 0;
                                arr[py][px]++;
                            }
                        }
                    }
                }
            }
        }

        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[y].length; x++) {
                if (flashed[y][x]) {
                    arr[y][x] = 0;
                    flashed[y][x] = false;
                    flashes++;
                }
            }
        }

        return new Result(flashes, arr);
    }
}

record Result(int flashes, int[][] energies) {
    
}
