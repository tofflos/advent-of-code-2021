package com.adventofcode2021;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {

        var cubes = Files.lines(Paths.get("22.in")).map(Cube::of).toList();

        System.out.println("Part 1: " + volume(cubes.stream()
                .filter(c -> Arrays.stream(new long[]{c.x1(), c.x2(), c.y1(), c.y2(), c.z1(), c.z2()}).map(Math::abs).allMatch(l -> l <= 50))
                .toList()
        ));

        System.out.println("Part 2: " + volume(cubes));
    }

    static BigInteger volume(List<Cube> cubes) {
        var volume = BigInteger.ZERO;

        for (int i = 0; i < cubes.size(); i++) {
            var cube = cubes.get(i);

            if (cube.type() == Cube.Type.ON) {
                volume = volume.add(BigInteger.valueOf(cube.volume()));
            }

            var intersections = cubes.stream().limit(i).flatMap(c -> Cube.intersection(c, cube).stream()).toList();
            volume = volume.subtract(volume(intersections));
        }

        return volume;
    }

}

record Cube(Type type, long x1, long x2, long y1, long y2, long z1, long z2) {

    static Pattern pattern = Pattern.compile("(-?\\d+)");

    static enum Type {
        ON, OFF
    }

    static Cube of(String line) {
        var bounds = pattern.matcher(line).results().map(MatchResult::group).mapToInt(Integer::parseInt).toArray();

        return new Cube(line.startsWith("on") ? Type.ON : Type.OFF,
                bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
    }

    long volume() {
        return Math.abs(x1 - x2 - 1) * Math.abs(y1 - y2 - 1) * Math.abs(z1 - z2 - 1);
    }

    static Optional<Cube> intersection(Cube c1, Cube c2) {

        if (c1.x2 >= c2.x1 && c1.x1 <= c2.x2
                && c1.y2 >= c2.y1 && c1.y1 <= c2.y2
                && c1.z2 >= c2.z1 && c1.z1 <= c2.z2) {
            return Optional.of(new Cube(c1.type,
                    Math.max(c1.x1, c2.x1), Math.min(c1.x2, c2.x2),
                    Math.max(c1.y1, c2.y1), Math.min(c1.y2, c2.y2),
                    Math.max(c1.z1, c2.z1), Math.min(c1.z2, c2.z2)));
        }

        return Optional.empty();
    }
}
