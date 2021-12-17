package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {

        var input = Files.readString(Paths.get("17.in"));
        var bounds = Pattern.compile("(-?\\d+)").matcher(input).results().map(MatchResult::group).mapToInt(Integer::parseInt).toArray();

        var results = new ArrayList<Probe>();
        
        for (int dx = -250; dx < 250; dx++) {
            for (int dy = -250; dy < 250; dy++) {
                launch(dx, dy, bounds[0], bounds[1], bounds[2], bounds[3]).ifPresent(results::add);
            }
        }

        System.out.println("Part 1: " + results.stream().mapToInt(probe -> probe.maxY).max().orElseThrow());
        System.out.println("Part 2: " + results.stream().mapToInt(probe -> probe.maxY).count());
    }

    static Optional<Probe> launch(int dx, int dy, int minX, int maxX, int minY, int maxY) {
        var hit = false;
        var current = new Probe(dx, dy);

        while ((current.dx > 0 && current.x <= maxX) || current.y >= minY) {
            if (minX <= current.x && current.x <= maxX && minY <= current.y && current.y <= maxY) {
                hit = true;
                break;
            }

            current.move();
        }

        return Optional.ofNullable(hit ? current : null);
    }
}

class Probe {

    int x, y, dx, dy, maxY;

    public Probe(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    void move() {
        x = x + dx;
        y = y + dy;

        if (y > maxY) {
            maxY = y;
        }

        dx = dx - Integer.compare(dx, 0);
        dy = dy - 1;
    }
}
