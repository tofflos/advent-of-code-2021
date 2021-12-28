package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    static Set<Position> halls = Set.of(new Position(1, 1), new Position(2, 1),
            new Position(4, 1), new Position(6, 1), new Position(8, 1),
            new Position(10, 1), new Position(11, 1)
    );

    static Map<Character, List<Position>> rooms1 = Map.of(
            'A', List.of(new Position(3, 2), new Position(3, 3)),
            'B', List.of(new Position(5, 2), new Position(5, 3)),
            'C', List.of(new Position(7, 2), new Position(7, 3)),
            'D', List.of(new Position(9, 2), new Position(9, 3)));

    static Map<Character, List<Position>> rooms2 = Map.of(
            'A', List.of(new Position(3, 2), new Position(3, 3), new Position(3, 4), new Position(3, 5)),
            'B', List.of(new Position(5, 2), new Position(5, 3), new Position(5, 4), new Position(5, 5)),
            'C', List.of(new Position(7, 2), new Position(7, 3), new Position(7, 4), new Position(7, 5)),
            'D', List.of(new Position(9, 2), new Position(9, 3), new Position(9, 4), new Position(9, 5)));

    record Position(int x, int y) {

    }

    record State(char[][] diagram, int energy) {

    }

    public static void main(String[] args) throws IOException {

        var diagram1 = Files.lines(Paths.get("23.in")).map(String::toCharArray).toArray(char[][]::new);

        var state1 = new State(diagram1, 0);
        var cheapest1 = organize(state1, rooms1);

        System.out.println("Part 1: " + cheapest1.energy);

        var diagram2 = """
                       #############
                       #...........#
                       ###A#D#C#A###
                         #D#C#B#A#
                         #D#B#A#C#
                         #C#D#B#B#
                         #########
                       """.lines().map(String::toCharArray).toArray(char[][]::new);

        var state2 = new State(diagram2, 0);
        var cheapest2 = organize(state2, rooms2);

        System.out.println("Part 2: " + cheapest2.energy);
    }

    static State organize(State state, Map<Character, List<Position>> rooms) {
        var costs = Map.of('A', 1, 'B', 10, 'C', 100, 'D', 1000);
        var deque = new ArrayDeque<State>(List.of(state));
        var history = new HashMap<String, State>();

        var cheapest = new State(new char[0][0], Integer.MAX_VALUE);

        while (!deque.isEmpty()) {
            var current = deque.remove();

            if (isOrganized(current.diagram, rooms) && current.energy < cheapest.energy) {
                cheapest = current;
                continue;
            }

            for (int y = 1; y < current.diagram.length - 1; y++) {
                for (int x = 1; x < current.diagram[y].length - 1; x++) {
                    char c = current.diagram[y][x];

                    if (c == ' ' || c == '#' || c == '.') {
                        continue;
                    }

                    var origin = new Position(x, y);
                    var paths = paths(origin, current.diagram);

                    if (halls.contains(origin)) {
                        paths.removeIf(path -> !isAllowedToLeaveHall(path, current.diagram, rooms));
                    } else {
                        paths.removeIf(path -> !isAllowedToLeaveRoom(path, current.diagram, rooms));
                    }

                    for (var path : paths) {
                        var destination = path.getLast();

                        var diagram = Arrays.stream(current.diagram).map(char[]::clone).toArray(char[][]::new);
                        diagram[destination.y][destination.x] = c;
                        diagram[y][x] = '.';

                        var cost = costs.get(c) * (path.size() - 1);
                        var next = new State(diagram, current.energy + cost);

                        if (isOrganized(next.diagram, rooms) && next.energy < cheapest.energy) {
                            cheapest = next;
                        } else {
                            var key = Arrays.deepToString(next.diagram);

                            if (next.energy < history.getOrDefault(key, cheapest).energy) {
                                history.put(key, next);
                                deque.offer(next);
                            }
                        }
                    }
                }
            }
        }

        return cheapest;
    }

    static boolean isOrganized(char[][] diagram, Map<Character, List<Position>> rooms) {
        return rooms.entrySet().stream().allMatch(entry -> entry.getValue().stream().allMatch(p -> diagram[p.y][p.x] == entry.getKey()));
    }

    static Set<Deque<Position>> paths(Position origin, char[][] diagram) {
        var candidates = new ArrayDeque<Deque<Position>>(List.of(new ArrayDeque<>(List.of(origin))));
        var directions = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        var paths = new HashSet<Deque<Position>>();

        while (!candidates.isEmpty()) {
            var visited = candidates.pop();
            var previous = visited.peekLast();

            for (var direction : directions) {
                var next = new Position(previous.x + direction[0], previous.y + direction[1]);

                if ((0 <= next.x && next.x < diagram[next.y].length && 0 <= next.y && next.y < diagram.length)
                        && diagram[next.y][next.x] == '.' && !visited.contains(next)) {
                    var t = new ArrayDeque<Position>(visited);
                    t.addLast(next);
                    candidates.push(t);
                } else {
                    paths.add(visited);
                }
            }
        }

        return paths;
    }

    static boolean isAllowedToLeaveHall(Deque<Position> path, char[][] diagram, Map<Character, List<Position>> rooms) {
        var origin = path.getFirst();
        var destination = path.getLast();
        var crab = diagram[origin.y][origin.x];

        if (origin.equals(destination)) {
            return false;
        }

        if (!rooms.get(crab).contains(destination)) {
            return false;
        }

        if (diagram[destination.y + 1][destination.x] == '.') {
            return false;
        }

        if (!rooms.get(crab).stream().map(room -> diagram[room.y][room.x]).allMatch(c -> c == crab || c == '.')) {
            return false;
        }

        return true;
    }

    static boolean isAllowedToLeaveRoom(Deque<Position> path, char[][] diagram, Map<Character, List<Position>> rooms) {
        var origin = path.getFirst();
        var destination = path.getLast();
        var crab = diagram[origin.y][origin.x];

        if (origin.equals(destination)) {
            return false;
        }

        if (!halls.contains(destination)) {
            return false;
        }

        if (rooms.get(crab).contains(origin) && rooms.get(crab).stream().allMatch(room -> diagram[room.y][room.x] == crab || diagram[room.y][room.x] == '.')) {
            return false;
        }

        return true;
    }
}
