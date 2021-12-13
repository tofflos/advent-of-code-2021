package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("13.in"));
        var dots = new ArrayList<int[]>();
        var folds = new ArrayList<Object[]>();

        for (var line : lines) {
            if (line.isBlank()) {
                continue;
            }

            if (Character.isDigit(line.charAt(0))) {
                var t = line.split(",");
                dots.add(new int[]{Integer.parseInt(t[0]), Integer.parseInt(t[1])});
            }

            if (line.startsWith("fold along ")) {
                var t = line.substring("fold along ".length()).split("=");
                folds.add(new Object[]{t[0], Integer.parseInt(t[1])});
            }
        }

        var height = dots.stream().mapToInt(t -> t[1]).max().orElseThrow() + 2;
        var width = dots.stream().mapToInt(t -> t[0]).max().orElseThrow() + 1;

        var paper = new String[height][width];

        Arrays.stream(paper).forEach(row -> Arrays.fill(row, "."));
        dots.stream().forEach(t -> paper[t[1]][t[0]] = "#");

        var part1 = foldX(paper, (int) folds.get(0)[1]);
        System.out.println("Part 1: " + Arrays.stream(part1).flatMap(Arrays::stream).filter(s -> "#".equals(s)).count());

        var part2 = paper;

        for (var fold : folds) {
            part2 = "y".equals(fold[0]) ? foldY(part2, (int) fold[1]) : foldX(part2, (int) fold[1]);
        }

        System.out.println("Part 2:");
        System.out.println(toString(part2));
    }

    static String toString(String[][] paper) {
        return Arrays.stream(paper)
                .map(row -> Arrays.stream(row).collect(Collectors.joining()))
                .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    static String[][] foldY(String[][] paper, int position) {
        var upper = Arrays.stream(paper).limit(position).map(row -> row.clone()).toArray(String[][]::new);
        var lower = Arrays.stream(paper).skip(position + 1).map(row -> row.clone()).toArray(String[][]::new);

        var t = Arrays.asList(lower);
        Collections.reverse(t);

        for (int y = 0; y < upper.length; y++) {
            for (int x = 0; x < upper[y].length; x++) {
                upper[y][x] = "#".equals(upper[y][x]) || "#".equals(lower[y][x]) ? "#" : ".";
            }
        }

        return upper;
    }

    static String[][] foldX(String[][] paper, int position) {
        var left = Arrays.stream(paper).map(row -> Arrays.copyOfRange(row, 0, position)).toArray(String[][]::new);
        var right = Arrays.stream(paper).map(row -> Arrays.copyOfRange(row, position + 1, row.length)).toArray(String[][]::new);

        Arrays.stream(right).forEach(row -> {
            var t = Arrays.asList(row);
            Collections.reverse(t);
        });

        for (int y = 0; y < left.length; y++) {
            for (int x = 0; x < left[y].length; x++) {
                left[y][x] = "#".equals(left[y][x]) || "#".equals(right[y][x]) ? "#" : ".";
            }
        }

        return left;
    }
}