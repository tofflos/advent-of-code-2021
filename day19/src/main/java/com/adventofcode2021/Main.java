package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var lines = Files.readAllLines(Paths.get("19.in"));
        var scanners = new ArrayList<Scanner>();
        var name = "";
        var points = new HashSet<Point>();

        for (var line : lines) {
            if (line.startsWith("---")) {
                name = line;
                points = new HashSet<>();
                continue;
            } else if (line.isBlank()) {
                var orientations = orientations(points).stream()
                        .map(orientation -> new Orientation(orientation, relations(orientation))).collect(Collectors.toSet());
                scanners.add(new Scanner(name, orientations));
                continue;
            }

            var t = line.split(",");
            points.add(new Point(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
        }

        var result = process(new ArrayList<>(scanners));

        System.out.println("Part 1: " + result.beacons().size());

        var max = Integer.MIN_VALUE;

        for (var p1 : result.scanners()) {
            for (var p2 : result.scanners()) {
                if (p1 != p2) {
                    var manhattan = Point.manhattan(p1, p2);

                    if (manhattan > max) {
                        max = manhattan;
                    }
                }
            }
        }

        System.out.println("Part 2: " + max);
    }

    static Result process(List<Scanner> unaligned) {
        var aligned = new ArrayList<Scanner>();
        var beacons = new HashSet<Point>();
        var scanners = new HashSet<Point>();

        outer:
        for (var s1 : unaligned) {
            for (var s2 : unaligned) {
                if (s1 != s2) {

                    System.out.println("Processing: " + s1.name() + " " + s2.name());

                    var translation = translation(s1, s2);

                    if (translation.isEmpty()) {
                        continue;
                    }

                    var t = translation.get();
                    var pt = Point.transpose(t.p2(), t.p1());
                    var transposed = t.s2().stream().map(p -> Point.transpose(p, pt)).collect(Collectors.toSet());

                    var s1t = new Scanner(s1.name(), Set.of(new Orientation(t.s1(), relations(t.s1()))));
                    var s2t = new Scanner(s2.name(), Set.of(new Orientation(transposed, relations(transposed))));

                    aligned.add(s1t);
                    aligned.add(s2t);

                    beacons.addAll(t.s1());
                    beacons.addAll(transposed);

                    scanners.add(new Point(0, 0, 0));
                    scanners.add(pt);
                    break outer;
                }
            }
        }

        System.out.println("Beacons: " + beacons.size());

        aligned.stream().map(Scanner::name).forEach(name -> unaligned.removeIf(scanner -> scanner.name().equals(name)));

        while (!unaligned.isEmpty()) {
            var intermediate = new ArrayList<Scanner>();

            outer:
            for (var s1 : aligned) {
                for (var s2 : unaligned) {
                    var translation = translation(s1, s2);

                    if (translation.isEmpty()) {
                        continue;
                    }

                    var t = translation.get();
                    var pt = Point.transpose(t.p2(), t.p1());
                    var transposed = t.s2().stream().map(p -> Point.transpose(p, pt)).collect(Collectors.toSet());

                    var s2t = new Scanner(s2.name(), Set.of(new Orientation(transposed, relations(transposed))));

                    intermediate.add(s2t);
                    beacons.addAll(transposed);
                    scanners.add(pt);

                    System.out.println("Processing: " + s1.name() + " " + s2.name());
                    System.out.println("Beacons: " + beacons.size());
                    System.out.println("Remaining: " + unaligned.size());

                    break outer;
                }
            }

            aligned.addAll(intermediate);
            aligned.stream().map(Scanner::name).forEach(name -> unaligned.removeIf(scanner -> scanner.name().equals(name)));
        }

        return new Result(beacons, scanners);
    }

    static Optional<Translation> translation(Scanner s1, Scanner s2) {
        for (var o1 : s1.orientations()) {
            for (var o2 : s2.orientations()) {
                for (var e1 : o1.relations().entrySet()) {
                    for (var e2 : o2.relations().entrySet()) {
                        var intersection = intersection(e1.getValue(), e2.getValue());

                        if (intersection.size() == 11) {
                            return Optional.of(new Translation(e1.getKey(), e2.getKey(), o1.orientation(), o2.orientation()));
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    static Map<Point, Set<Point>> relations(Set<Point> points) {
        var relations = new HashMap<Point, Set<Point>>();

        for (var p1 : points) {
            for (var p2 : points) {
                if (p1 != p2) {
                    relations.merge(p1, Set.of(Point.transpose(p1, p2)), (a, b) -> {
                        var r = new HashSet<Point>();
                        r.addAll(a);
                        r.addAll(b);
                        return r;
                    });
                }
            }
        }

        return relations;
    }

    static Set<Set<Point>> orientations(Set<Point> points) {
        var orientations = new HashSet<Set<Point>>();

        var xy1 = points;
        var xy2 = flipXY(points);
        var xyr1 = rotations(xy1);
        var xyr2 = rotations(xy2);

        orientations.addAll(xyr1);
        orientations.addAll(xyr2);

        var xz1 = points;
        var xz2 = flipXZ(points);
        var xzr1 = rotations(xz1);
        var xzr2 = rotations(xz2);

        orientations.addAll(xzr1);
        orientations.addAll(xzr2);

        var yz1 = points;
        var yz2 = flipXY(points);
        var yzr1 = rotations(yz1);
        var yzr2 = rotations(yz2);

        orientations.addAll(yzr1);
        orientations.addAll(yzr2);

        return orientations;
    }

    static Set<Set<Point>> rotations(Set<Point> points) {
        var rotations = new HashSet<Set<Point>>();
        var current = points;

        for (int i = 0; i < 4; i++) {
            current = rotateXY(current);
            rotations.add(current);
            rotations.add(rotateXZ(current));
            rotations.add(rotateYZ(current));
        }

        return rotations;
    }

    static Set<Point> rotateXY(Set<Point> points) {
        return points.stream().map(point -> new Point(point.y(), -point.x(), point.z())).collect(Collectors.toSet());
    }

    static Set<Point> rotateXZ(Set<Point> points) {
        return points.stream().map(point -> new Point(point.z(), point.y(), -point.x())).collect(Collectors.toSet());
    }

    static Set<Point> rotateYZ(Set<Point> points) {
        return points.stream().map(point -> new Point(point.x(), point.z(), -point.y())).collect(Collectors.toSet());
    }

    static Set<Point> flipXY(Set<Point> points) {
        return points.stream().map(point -> new Point(-point.x(), -point.y(), point.z())).collect(Collectors.toSet());
    }

    static Set<Point> flipXZ(Set<Point> points) {
        return points.stream().map(point -> new Point(-point.x(), point.y(), -point.z())).collect(Collectors.toSet());
    }

    static Set<Point> flipYZ(Set<Point> points) {
        return points.stream().map(point -> new Point(point.x(), -point.y(), -point.z())).collect(Collectors.toSet());
    }

    static <T> Set<T> intersection(Set<T> s1, Set<T> s2) {
        var intersection = new HashSet<T>(s1);

        intersection.retainAll(s2);

        return intersection;
    }
}

record Result(Set<Point> beacons, Set<Point> scanners) {

}

record Translation(Point p1, Point p2, Set<Point> s1, Set<Point> s2) {

}

record Orientation(Set<Point> orientation, Map<Point, Set<Point>> relations) {

}

record Scanner(String name, Set<Orientation> orientations) {

}

record Point(int x, int y, int z) {

    static int manhattan(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + Math.abs(p1.z - p2.z);
    }

    static Point transpose(Point p1, Point p2) {
        return new Point(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
    }
}
