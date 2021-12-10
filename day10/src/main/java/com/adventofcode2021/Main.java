package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

    static Map<String, String> delimiters = Map.of("(", ")", "[", "]", "{", "}", "<", ">");

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("10.in"));

        System.out.println("Part 1: " + part1(lines));
        System.out.println("Part 2: " + part2(lines));
    }

    static int part1(List<String> lines) {
        var deque = new ArrayDeque<String>();
        var results = new ArrayList<String>();
        
        var iterator = lines.iterator();

        while (iterator.hasNext()) {
            var line = iterator.next();

            for (int i = 0; i < line.length() - 1; i++) {
                var next = line.substring(i, i + 1);

                if (delimiters.keySet().contains(next)) {
                    deque.push(next);
                } else {
                    var previous = deque.pop();
                    var opener = delimiters.entrySet().stream().filter(e -> e.getValue().equals(next)).map(Entry::getKey).findAny().orElseThrow();

                    if (!previous.equals(opener)) {
                        results.add(next);
                        iterator.remove();
                    }
                }
            }

            deque.clear();
        }

        var points = Map.of(")", 3, "]", 57, "}", 1197, ">", 25137);

        return results.stream().mapToInt(points::get).sum();
    }

    static long part2(List<String> lines) {
        var left = new ArrayDeque<String>();
        var right = new ArrayDeque<String>();
        var results = new ArrayList<List<String>>();

        for (var line : lines) {
            for (int i = 0; i < line.length(); i++) {
                left.push(line.substring(i, i + 1));
            }

            var completions = new ArrayList<String>();

            while (!left.isEmpty()) {
                var l = left.pop();

                if (delimiters.values().contains(l)) {
                    right.add(l);
                } else {
                    if (right.isEmpty()) {
                        completions.add(delimiters.get(l));
                        right.push(delimiters.get(l));
                    }

                    var r = right.pop();
                }
            }

            results.add(completions);
        }

        var scores = results.stream().mapToLong(Main::score).sorted().toArray();

        return scores[scores.length / 2];
    }

    static long score(List<String> completion) {
        var score = 0L;
        var points = Map.of(")", 1, "]", 2, "}", 3, ">", 4);

        for (int i = 0; i < completion.size(); i++) {
            score = score * 5 + points.get(completion.get(i));
        }

        return score;
    }
}
