package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    record Universe(int position1, int roll1, int score1, int position2, int roll2, int score2, int stage, long occurrences) implements Comparable<Universe> {

        @Override
        public int compareTo(Universe other) {
            var sum1 = this.score1 + this.score2;
            var sum2 = other.score1 + other.score2;

            return Integer.compare(sum2, sum1);
        }
    }

    public static void main(String[] args) throws IOException {

        var lines = Files.readAllLines(Paths.get("21.in"));

        var position1 = Integer.parseInt(lines.get(0).split(" ")[4]);
        var position2 = Integer.parseInt(lines.get(1).split(" ")[4]);

        System.out.println("Part 1: " + part1(position1, position2));

        var universe = new Universe(position1, 0, 0, position2, 0, 0, 1, 1);

        System.out.println("Part 2: " + part2(universe));
    }

    static long part2(Universe initial) {
        var queue = new PriorityQueue<Universe>(List.of(initial));
        var wins1 = 0L;
        var wins2 = 0L;

        var rolls = new ArrayList<Integer>();

        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                for (int k = 1; k <= 3; k++) {
                    rolls.add(i + j + k);
                }
            }
        }

        var frequencies = rolls.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        while (!queue.isEmpty()) {
            var current = queue.remove();

            switch (current.stage) {
                case 1,2,3:
                    frequencies.forEach((roll, occurrences) -> {
                        queue.offer(new Universe(current.position1, roll, current.score1, current.position2, 0, current.score2, 4, current.occurrences * occurrences));
                    });
                    break;
                case 4:
                    var position1 = ((current.position1 - 1 + current.roll1) % 10) + 1;
                    var score1 = current.score1 + position1;

                    if (score1 >= 21) {
                        wins1 += current.occurrences;
                    } else {
                        queue.offer(new Universe(position1, current.roll1, score1, current.position2, 0, current.score2, current.stage + 1, current.occurrences));
                    }
                    break;
                case 5,6,7:
                    frequencies.forEach((roll, occurrences) -> {
                        queue.offer(new Universe(current.position1, current.roll1, current.score1, current.position2, roll, current.score2, 8, current.occurrences * occurrences));
                    });
                    break;
                case 8:
                    var position2 = ((current.position2 - 1 + current.roll2) % 10) + 1;
                    var score2 = current.score2 + position2;

                    if (score2 >= 21) {
                        wins2 += current.occurrences;
                    } else {
                        queue.offer(new Universe(current.position1, current.roll1, current.score1, position2, current.roll2, score2, 1, current.occurrences));
                    }
                    break;
                default:
                    throw new IllegalStateException("Illegal stage: " + current.stage);
            }
        }

        return Math.max(wins1, wins2);
    }

    static int part1(int position1, int position2) {
        var die = new DeterministicDie();
        var pawn1 = new Pawn(position1);
        var pawn2 = new Pawn(position2);

        while (true) {
            var roll1 = die.roll() + die.roll() + die.roll();
            pawn1.move(roll1);
            pawn1.score += pawn1.position;

            if (pawn1.score >= 1000) {
                break;
            }

            var roll2 = die.roll() + die.roll() + die.roll();
            pawn2.move(roll2);
            pawn2.score += pawn2.position;

            if (pawn2.score >= 1000) {
                break;
            }
        }

        return pawn2.score * die.count;
    }
}

class DeterministicDie {

    int count;
    int value = 0;

    int roll() {
        count++;
        return (value++ % 100) + 1;
    }
}

class Pawn {

    int score;
    int position;

    public Pawn(int position) {
        this.position = position;
    }

    void move(int steps) {
        position = ((position - 1 + steps) % 10) + 1;
    }
}
