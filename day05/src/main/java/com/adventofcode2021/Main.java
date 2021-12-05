package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {

        var ranges = Files.lines(Paths.get("5.in")).map(Range::of).toList();
        var height = ranges.stream().mapToInt(range -> Math.max(range.y1(), range.y2())).max().orElseThrow() + 1;
        var width = ranges.stream().mapToInt(range -> Math.max(range.x1(), range.x2())).max().orElseThrow() + 1;

        var diagram1 = new int[height][width];

        ranges.stream()
                .filter(range -> range.x1() == range.x2() || range.y1() == range.y2())
                .flatMap(range -> Point.fromRange(range).stream())
                .forEach(p -> diagram1[p.y()][p.x()]++);

        var count1 = Arrays.stream(diagram1).flatMapToInt(Arrays::stream).filter(i -> i >= 2).count();

        System.out.println("Part 1: " + count1);

        var diagram2 = new int[height][width];

        ranges.stream()
                .flatMap(range -> Point.fromRange(range).stream())
                .forEach(p -> diagram2[p.y()][p.x()]++);

        var count2 = Arrays.stream(diagram2).flatMapToInt(Arrays::stream).filter(i -> i >= 2).count();

        System.out.println("Part 2: " + count2);
    }
}

record Range(int x1, int y1, int x2, int y2) {

    static Pattern pattern = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");

    static Range of(String s) {
        var matcher = pattern.matcher(s);

        if (matcher.matches()) {
            return new Range(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)));
        } else {
            throw new IllegalArgumentException();
        }
    }
}

record Point(int x, int y) {

    static List<Point> fromRange(Range range) {
        var points = new ArrayList<Point>();

        var dx = Integer.compare(range.x2(), range.x1());
        var dy = Integer.compare(range.y2(), range.y1());

        var maxY = Math.max(range.y1(), range.y2());
        var minY = Math.min(range.y1(), range.y2());
        var maxX = Math.max(range.x1(), range.x2());
        var minX = Math.min(range.x1(), range.x2());

        var x = range.x1();
        var y = range.y1();

        while (minX <= x && x <= maxX && minY <= y && y <= maxY) {
            points.add(new Point(x, y));

            x += dx;
            y += dy;
        }

        return points;
    }
}