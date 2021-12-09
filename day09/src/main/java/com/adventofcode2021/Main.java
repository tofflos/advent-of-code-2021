package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) throws IOException {

        var heights = Files.lines(Paths.get("9.in")).map(s -> Arrays.stream(s.split("")).mapToInt(Integer::parseInt).toArray()).toArray(int[][]::new);
        var lows = lows(heights);

        System.out.println("Part 1: " + lows.stream().mapToInt(p -> p.z() + 1).sum());
        System.out.println("Part 2: " + lows.stream()
                .map(p -> basin(heights, p).size())
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .limit(3)
                .reduce(1, (a, b) -> a * b)
        );
    }

    static Set<Point> basin(int[][] heights, Point point) {
        var members = new HashSet<Point>();
        var deque = new ArrayDeque<Point>();

        members.add(point);
        deque.push(point);

        while (!deque.isEmpty()) {
            var current = deque.pop();

            neighbours(heights, current.x(), current.y()).stream()
                    .filter(p -> p.z() < 9)
                    .filter(p -> p.z() > current.z())
                    .filter(Predicate.not(members::contains))
                    .forEach(p -> {
                        members.add(p);
                        deque.push(p);
                    });
        }

        return members;
    }

    static List<Point> lows(int[][] heights) {
        var lows = new ArrayList<Point>();

        for (int y = 0; y < heights.length; y++) {
            for (int x = 0; x < heights[y].length; x++) {
                if (isLow(heights, x, y)) {
                    lows.add(new Point(x, y, heights[y][x]));
                }
            }
        }

        return lows;
    }

    static List<Point> neighbours(int[][] heights, int x, int y) {
        var neighbours = new ArrayList<Point>();
        var directions = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (var direction : directions) {
            var px = x + direction[0];
            var py = y + direction[1];

            if (0 <= px && px < heights[0].length && 0 <= py && py < heights.length) {
                neighbours.add(new Point(px, py, heights[py][px]));
            }
        }

        return neighbours;
    }

    static boolean isLow(int[][] heights, int x, int y) {
        return heights[y][x] < neighbours(heights, x, y).stream().mapToInt(Point::z).min().orElseThrow();
    }
}

record Point(int x, int y, int z) {

}
