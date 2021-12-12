package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        var caves = Files.lines(Paths.get("12.in"))
                .<String[]>mapMulti((connection, consumer) -> {
                    var t = connection.split("-");
                    
                    consumer.accept(new String[]{t[0], t[1]});
                    consumer.accept(new String[]{t[1], t[0]});
                })
                .collect(Collectors.groupingBy(t -> t[0], Collectors.mapping(t -> t[1], Collectors.toSet())));

        System.out.println("Part 1: " + solve(caves, (path, next) -> !path.contains(next) || isBig(next)));
        System.out.println("Part 2: " + solve(caves, (path, next) -> {
            var frequencies = path.stream().filter(Main::isSmall).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            return !"start".equals(next) && (isBig(next) || !path.contains(next) || frequencies.values().stream().noneMatch(frequency -> frequency > 1));
        }));
    }

    static boolean isBig(String name) {
        return Character.isUpperCase(name.charAt(0));
    }

    static boolean isSmall(String name) {
        return Character.isLowerCase(name.charAt(0)) && !"start".equals(name) && !"end".equals(name);
    }
    
    static int solve(Map<String, Set<String>> caves, BiPredicate<Deque<String>, String> predicate) {
        var branches = new ArrayDeque<Deque<String>>();
        var paths = new ArrayDeque<Deque<String>>();

        branches.push(new ArrayDeque<>(List.of("start")));
        
        while (!branches.isEmpty()) {
            var branch = branches.pop();
            var current = branch.peek();

            if ("end".equals(current)) {
                paths.add(branch);
                continue;
            }

            for (var next : caves.get(current)) {
                if (predicate.test(branch, next)) {
                    var b = new ArrayDeque<String>(branch);
                    b.push(next);

                    branches.push(b);
                }
            }
        }

        return paths.size();
    }
}