package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("14.in"));
        var template = lines.get(0);
        var rules = lines.stream().skip(2).map(s -> s.split(" -> ")).collect(Collectors.toMap(t -> t[0], t -> t[1]));

        System.out.println("Part 1: " + process1(template, rules, 10));
        System.out.println("Part 2: " + process2(template, rules, 40));

    }

    static long process1(String template, Map<String, String> rules, int steps) {
        var polymer = new StringBuilder(template);

        for (int n = 0; n < steps; n++) {
            for (int i = 0; i < polymer.length() - 1; i++) {
                var pair = polymer.substring(i, i + 2);

                if (rules.containsKey(pair)) {
                    polymer.insert(i + 1, rules.get(pair));
                    i++;
                }
            }
        }

        var frequencies = polymer.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        var max = frequencies.entrySet().stream().max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue())).orElseThrow();
        var min = frequencies.entrySet().stream().min((e1, e2) -> Long.compare(e1.getValue(), e2.getValue())).orElseThrow();

        return max.getValue() - min.getValue();
    }

    static long process2(String template, Map<String, String> rules, int steps) {
        var current = new HashMap<String, Long>();

        for (int i = 0; i < template.length() - 1; i++) {
            var pair = template.substring(i, i + 2);
            current.merge(pair, 1L, Long::sum);
        }

        for (int i = 0; i < steps; i++) {
            var next = new HashMap<>(current);

            for (Map.Entry<String, Long> entry : current.entrySet()) {
                if (entry.getValue() == 0L) {
                    continue;
                }

                var pair1 = entry.getKey().substring(0, 1) + rules.get(entry.getKey());
                var pair2 = rules.get(entry.getKey()) + entry.getKey().substring(1, 2);

                next.merge(pair1, current.get(entry.getKey()), Long::sum);
                next.merge(pair2, current.get(entry.getKey()), Long::sum);
                next.merge(entry.getKey(), current.get(entry.getKey()), (a, b) -> a - b);
            }

            current = next;
        }

        var frequencies = current.entrySet().stream()
                .<Object[]>mapMulti((entry, consumer) -> {
                    consumer.accept(new Object[]{entry.getKey().substring(0, 1), entry.getValue()});
                    consumer.accept(new Object[]{entry.getKey().substring(1, 2), entry.getValue()});
                })
                .collect(Collectors.groupingBy(t -> t[0], Collectors.mapping(t -> t[1], Collectors.summingLong(l -> (long) l))));

        var max = frequencies.entrySet().stream().max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue())).orElseThrow();
        var min = frequencies.entrySet().stream().filter(entry -> entry.getValue() > 0).min((e1, e2) -> Long.compare(e1.getValue(), e2.getValue())).orElseThrow();

        return (max.getValue() / 2) - (min.getValue() / 2);
    }
}
