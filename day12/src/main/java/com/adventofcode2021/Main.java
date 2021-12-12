package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
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

        System.out.println("Part 1: " + traverse(caves, (visited, next) -> !visited.contains(next) || isBig(next)));
        System.out.println("Part 2: " + traverse(caves, (visited, next) -> {
            var frequencies = visited.stream().filter(Main::isSmall).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            
            return !"start".equals(next) && (isBig(next) || !visited.contains(next) || frequencies.values().stream().noneMatch(frequency -> frequency > 1));
        }));
    }

    static boolean isBig(String cave) {
        return Character.isUpperCase(cave.charAt(0));
    }

    static boolean isSmall(String cave) {
        return Character.isLowerCase(cave.charAt(0)) && !"start".equals(cave) && !"end".equals(cave);
    }
    
    static int traverse(Map<String, Set<String>> caves, BiPredicate<Deque<String>, String> isVisitable) {
        var candidates = new ArrayDeque<Deque<String>>();
        var paths = new ArrayDeque<Deque<String>>();

        candidates.push(new ArrayDeque<>(List.of("start")));
        
        while (!candidates.isEmpty()) {
            var visited = candidates.pop();
            var cave = visited.peek();

            if ("end".equals(cave)) {
                paths.add(visited);
                continue;
            }

            for (var neighbour : caves.get(cave)) {
                if (isVisitable.test(visited, neighbour)) {
                    var candidate = new ArrayDeque<>(visited);
                    candidate.push(neighbour);

                    candidates.push(candidate);
                }
            }
        }

        return paths.size();
    }
}