package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {

        var lines = Files.lines(Paths.get("4.in")).map(String::trim).toList();
        var numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).toList();
        var results = play(numbers, parse(lines));

        System.out.println("Part 1: " + results.get(0).calculateScore());
        System.out.println("Part 2: " + results.get(results.size() - 1).calculateScore());
    }

    static List<Board> parse(List<String> lines) {
        var boards = new ArrayList<Board>();
        var board = new Board();

        for (var line : lines.subList(2, lines.size())) {
            if(line.isBlank()) {
                boards.add(board);
                board = new Board();
            } else {
                board.rows().add(Arrays.stream(line.split("\\s+")).map(Integer::parseInt).toList());
            }
        }

        boards.add(board);

        return boards;
    }

    static List<Board> play(List<Integer> numbers, List<Board> boards) {
        var results = new ArrayList<Board>();

        for (var number : numbers) {
            var iterator = boards.iterator();
            
            while(iterator.hasNext()) {
                var board = iterator.next();
                
                board.mark(number);

                if (board.hasWinningColumn() || board.hasWinningRow()) {
                    results.add(board);
                    iterator.remove();
                }
            }
        }

        return results;
    }
}

record Board(List<Integer> marks, List<List<Integer>> rows) {

    Board() {
        this(new ArrayList<>(), new ArrayList<>());
    }
    
    int calculateScore() {
        return rows.stream()
                .flatMap(row -> row.stream())
                .filter(Predicate.not(marks::contains))
                .mapToInt(Integer::intValue)
                .sum() * marks.get(marks.size() - 1);
    }

    void mark(int number) {
        marks.add(number);
    }

    boolean hasWinningColumn() {
        return IntStream.range(0, rows.get(0).size())
                .mapToObj(i -> rows.stream().map(row -> row.get(i)).toList())
                .anyMatch(column -> marks.containsAll(column));
    }

    boolean hasWinningRow() {
        return rows.stream().anyMatch(row -> marks.containsAll(row));
    }
}