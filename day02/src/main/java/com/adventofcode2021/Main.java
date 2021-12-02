package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        var commands = Files.lines(Paths.get("2.in")).map(Command::of).toList();

        int x = 0, y = 0;

        for (var command : commands) {
            switch (command.direction()) {
                case FORWARD    -> x += command.amount();
                case DOWN       -> y += command.amount();
                case UP         -> y -= command.amount();
            }
        }

        System.out.println("Part 1: " + x * y);

        x = 0;
        y = 0;
        int dy = 0;

        for (var command : commands) {
            switch (command.direction()) {
                case DOWN       -> dy += command.amount();
                case UP         -> dy -= command.amount();
                case FORWARD -> {
                    x += command.amount();
                    y += command.amount() * dy;
                }
            }
        }

        System.out.println("Part 2: " + x * y);
    }
}

record Command(Direction direction, int amount) {

    enum Direction {
        DOWN, FORWARD, UP
    }

    static Command of(String s) {
        var t = s.split(" ");

        return new Command(Direction.valueOf(t[0].toUpperCase()), Integer.parseInt(t[1]));
    }
}