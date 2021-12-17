package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) throws IOException {
        var caves = Files.lines(Paths.get("15.in"))
                .map(line -> Arrays.stream(line.split("")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);

        var height = caves.length;
        var width = caves[0].length;
        var risks1 = risks(caves);

        System.out.println("Part 1: " + risks1[height - 1][width - 1]);

        var parts = new ArrayList<int[][]>();
        parts.add(caves);

        var caves5x = new int[height][width * 5];

        for (int i = 1; i < 5; i++) {
            parts.add(next(parts.get(i - 1)));
        }

        for (int i = 0; i < parts.size(); i++) {
            for (int j = 0; j < parts.get(i).length; j++) {
                System.arraycopy(parts.get(i)[j], 0, caves5x[j], i * width, width);
            }
        }

        parts.clear();
        parts.add(caves5x);

        for (int i = 1; i < 5; i++) {
            parts.add(next(parts.get(i - 1)));
        }

        var caves25x = new int[height * 5][width * 5];

        for (int i = 0; i < parts.size(); i++) {
            for (int j = 0; j < parts.get(i).length; j++) {
                System.arraycopy(parts.get(i)[j], 0, caves25x[i * height + j], 0, width * 5);
            }
        }

        var risks2 = risks(caves25x);

        System.out.println("Part 2: " + risks2[height * 5 - 1][width * 5 - 1]);
    }

    static int[][] next(int[][] caves) {
        var arr = new int[caves.length][caves[0].length];

        for (int y = 0; y < caves.length; y++) {
            for (int x = 0; x < caves[y].length; x++) {
                arr[y][x] = caves[y][x] == 9 ? 1 : caves[y][x] + 1;
            }
        }

        return arr;
    }

    static int[][] risks(int[][] caves) {
        var risks = new int[caves.length][caves[0].length];
        var candidates = new ArrayDeque<PriorityQueue<Point>>();
        var comparator = Comparator.comparing(Point::x).thenComparing(Point::y);
        var directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        Arrays.stream(risks).forEach(row -> Arrays.fill(row, Integer.MAX_VALUE));
        risks[0][0] = 0;
        candidates.add(new PriorityQueue<>(comparator));
        candidates.peek().add(new Point(0, 0));

        while (!candidates.isEmpty()) {
            var visited = candidates.removeFirst();
            var cave = visited.poll();

            if (cave.x() == caves[0].length && cave.y() == caves.length) {
                continue;
            }

            for (var direction : directions) {
                var px = cave.x() + direction[0];
                var py = cave.y() + direction[1];

                if (0 <= px && px < caves[cave.y()].length && 0 <= py && py < caves.length) {
                    var risk1 = risks[cave.y()][cave.x()];
                    var risk2 = risks[py][px];

                    if (risk1 + caves[py][px] < risk2) {
                        risks[py][px] = risk1 + caves[py][px];

                        var candidate = new PriorityQueue<Point>(comparator);
                        candidate.addAll(visited);
                        candidate.add(new Point(px, py));
                        candidates.addFirst(candidate);
                    }
                }
            }
        }

        return risks;
    }
}

record Point(int x, int y) {

}
