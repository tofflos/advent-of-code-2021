package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        var lines = Files.lines(Paths.get("4.in")).map(String::trim).toList();
        var numbers = Arrays.stream(lines.get(0).split(",")).mapToInt(Integer::parseInt).toArray();
        var boards = new ArrayList<List<List<Tile>>>();
        var board = new ArrayList<List<Tile>>();

        for (var line : lines.subList(2, lines.size())) {
            if ("".equals(line)) {
                boards.add(board);
                board = new ArrayList<>();
            } else {
                board.add(Arrays.stream(line.split("\\s+")).map(Integer::parseInt).map(Tile::new).toList());
            }
        }

        boards.add(board);

        System.out.println("Part 1: " + part1(numbers, boards));

        boards.stream().forEach(Main::clear);

        System.out.println("Part 2: " + part2(numbers, boards));
    }

    static int part1(int[] numbers, List<List<List<Tile>>> boards) {
        var score = Integer.MIN_VALUE;

        outer:
        for (var number : numbers) {
            for (var board : boards) {
                mark(number, board);

                if (rows(board) || columns(board)) {
                    score = score(number, board);
                    break outer;
                }
            }
        }

        return score;
    }

    static int part2(int[] numbers, List<List<List<Tile>>> boards) {
        var score = Integer.MIN_VALUE;

        for (var number : numbers) {

            boards.stream().forEach(board -> mark(number, board));

            if (boards.size() == 1 && (rows(boards.get(0)) || columns(boards.get(0)))) {
                score = score(number, boards.get(0));
                break;
            }

            boards.removeIf(b -> rows(b) || columns(b));
        }

        return score;
    }

    static int score(int number, List<List<Tile>> board) {
        return board.stream().flatMap(List::stream).filter(t -> !t.marked).mapToInt(t -> t.number).sum() * number;
    }

    static void clear(List<List<Tile>> board) {
        board.stream().flatMap(List::stream).forEach(t -> t.marked = false);
    }

    static void mark(int number, List<List<Tile>> board) {
        board.stream().flatMap(List::stream).filter(t -> t.number == number).forEach(t -> t.marked = true);
    }

    static boolean rows(List<List<Tile>> board) {
        return board.stream().anyMatch(row -> row.stream().allMatch(t -> t.marked));
    }

    static boolean columns(List<List<Tile>> board) {
        var columns = false;

        for (int x = 0; x < board.get(0).size(); x++) {
            var b = true;

            for (int y = 0; y < board.size(); y++) {
                if (!board.get(y).get(x).marked) {
                    b = false;
                    break;
                }
            }

            if (b) {
                columns = true;
                break;
            }
        }

        return columns;
    }
}

class Tile {

    int number;
    boolean marked;

    public Tile(int number) {
        this.number = number;
    }
}
