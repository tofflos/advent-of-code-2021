package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

// Analysis tooling
// split -l 18 24.in
// vim -d xaa  xab  xac  xad  xae  xaf
// vim -d xag  xah  xai  xaj  xak  xal  xam  xan
// :qa

public class Main {

    public static void main(String[] args) throws IOException {

        var chunks = split(Files.lines(Paths.get("24.in")).map(Statement::of).toList(), 18);
        var initial = Map.of("w", 0L, "x", 0L, "y", 0L, "z", 0L);

        System.out.println("Part 1: " + process1("", initial, chunks, 0));
        System.out.println("Part 2: " + process2("", initial, chunks, 0));
    }

    static Optional<Result> process1(String input, Map<String, Long> variables, List<List<Statement>> chunks, int depth) {
        var chunk = chunks.get(depth);
        var current = new HashMap<>(variables);

        var statement = chunk.get(4);

        if (26 == Integer.parseInt(statement.b())) {
            var remainder = (int) (current.get("z") % 26);
            var i = remainder + Integer.parseInt(chunk.get(5).b());

            if (i < 1 || i > 9) {
                return Optional.empty();
            }

            var next = new ALU(String.valueOf(i), chunk, current).call();
            return depth == chunks.size() - 1 ? Optional.of(new Result(depth, Long.parseLong(input + i), next)) : process1(input + i, next, chunks, depth + 1);
        }

        for (int i = 9; i > 0; i--) {
            var next = new ALU(String.valueOf(i), chunk, current).call();
            var result = process1(input + i, next, chunks, depth + 1);

            if (result.isPresent()) {
                var r = result.get();

                if (r.variables().get("z") == 0) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    static Optional<Result> process2(String input, Map<String, Long> variables, List<List<Statement>> chunks, int depth) {
        var chunk = chunks.get(depth);
        var current = new HashMap<>(variables);

        var statement = chunk.get(4);

        if (26 == Integer.parseInt(statement.b())) {
            var remainder = (int) (current.get("z") % 26);
            var i = remainder + Integer.parseInt(chunk.get(5).b());

            if (i < 1 || i > 9) {
                return Optional.empty();
            }

            var next = new ALU(String.valueOf(i), chunk, current).call();
            return depth == chunks.size() - 1 ? Optional.of(new Result(depth, Long.parseLong(input + i), next)) : process2(input + i, next, chunks, depth + 1);
        }

        for (int i = 1; i <= 9; i++) {
            var next = new ALU(String.valueOf(i), chunk, current).call();
            var result = process2(input + i, next, chunks, depth + 1);

            if (result.isPresent()) {
                var r = result.get();

                if (r.variables().get("z") == 0) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    static List<List<Statement>> split(List<Statement> statements, int size) {
        var chunks = new ArrayList<List<Statement>>();

        for (int i = 0; i < statements.size(); i += size) {
            chunks.add(statements.subList(i, i + size));
        }

        return chunks;
    }
}

class ALU implements Callable<Map<String, Long>> {

    Iterator<Long> input;
    List<Statement> statements;
    Map<String, Long> variables;

    public ALU(String input, List<Statement> statements, Map<String, Long> variables) {
        this.input = Arrays.stream(input.split("")).map(Long::parseLong).toList().iterator();
        this.statements = statements;
        this.variables = new HashMap<>(variables);
    }

    public Map<String, Long> call() {

        Map<String, BiConsumer<String, Long>> instructions = Map.of(
                "inp", (a, b) -> variables.put(a, input.next()),
                "add", (a, b) -> variables.merge(a, b, (n1, n2) -> n1 + n2),
                "mul", (a, b) -> variables.merge(a, b, (n1, n2) -> n1 * n2),
                "div", (a, b) -> variables.merge(a, b, (n1, n2) -> n1 / n2),
                "mod", (a, b) -> variables.merge(a, b, (n1, n2) -> n1 % n2),
                "eql", (a, b) -> variables.merge(a, b, (n1, n2) -> n1.equals(n2) ? 1L : 0L)
        );

        for (var statement : statements) {
            var instruction = instructions.get(statement.name());
            var a = statement.a();
            var b = Character.isLetter(statement.b().charAt(0)) ? variables.get(statement.b()) : Integer.parseInt(statement.b());

            instruction.accept(a, b);
        }

        return variables;
    }
}

record Result(int depth, long input, Map<String, Long> variables) {

}

record Statement(String name, String a, String b) {

    static Statement of(String s) {
        var t = s.split(" ");

        return new Statement(t[0], t[1], "inp".equals(t[0]) ? "0" : t[2]);
    }
}
