package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {

        var lines = Files.readAllLines(Paths.get("18.in"));
        var magnitude = lines.stream().map(Node::of)
                .reduce((left, right) -> Node.reduce(Node.add(left, right)))
                .map(Node::magnitude).orElseThrow();

        System.out.println("Part 1: " + magnitude);
        
        var max = lines.stream()
                .flatMap(left -> lines.stream().map(right -> Node.reduce(Node.add(Node.of(left), Node.of(right)))))
                .mapToInt(Node::magnitude).max().orElseThrow();

        System.out.println("Part 2: " + max);
    }
}

class Node {

    Pair parent;

    static Pair add(Node left, Node right) {
        var pair = new Pair();

        pair.left = left;
        pair.right = right;

        left.parent = pair;
        right.parent = pair;

        return pair;
    }

    static int magnitude(Node root) {
        int magnitude = Integer.MIN_VALUE;

        if (root instanceof Regular regular) {
            magnitude = regular.value;
        } else if (root instanceof Pair pair) {
            magnitude = 3 * magnitude(pair.left) + 2 * magnitude(pair.right);
        }

        return magnitude;
    }

    static Node reduce(Node root) {
        var finished = false;

        while (!finished) {
            finished = !explode(root) && !split(root);
        }

        return root;
    }

    static boolean explode(Node root) {
        var pair = Node.stream(root).filter(n -> n instanceof Pair && n.depth() == 4).map(n -> (Pair) n).findFirst();
        var regulars = Node.stream(root).filter(n -> n instanceof Regular).map(n -> (Regular) n).toList();

        if (pair.isEmpty()) {
            return false;
        }

        pair.ifPresent(p -> {
            var zero = new Regular();
            var parent = p.parent;

            if (p == parent.left) {
                parent.left = zero;
            } else {
                parent.right = zero;
            }

            zero.parent = parent;

            var left = regulars.stream().takeWhile(r -> r != p.left).reduce((a, b) -> b);
            left.ifPresent(regular -> regular.value = regular.value + ((Regular) p.left).value);

            var right = regulars.stream().dropWhile(r -> r != p.right).skip(1).findFirst();
            right.ifPresent(regular -> regular.value = regular.value + ((Regular) p.right).value);
        });

        return true;
    }

    static boolean split(Node root) {
        var regular = Node.stream(root)
                .filter(node -> node instanceof Regular r && r.value >= 10)
                .map(node -> (Regular) node)
                .findFirst();

        if (regular.isEmpty()) {
            return false;
        }

        regular.ifPresent(r -> {
            var pair = new Pair();
            var left = new Regular();
            var right = new Regular();

            pair.left = left;
            left.parent = pair;
            pair.right = right;
            right.parent = pair;

            left.value = r.value / 2;
            right.value = (int) Math.ceil(r.value / 2.0);

            if (r.parent.left == r) {
                r.parent.left = pair;
            } else {
                r.parent.right = pair;
            }

            pair.parent = r.parent;
        });

        return true;
    }

    static void traverse(Node root, Consumer<Node> consumer) {

        if (root instanceof Pair p) {
            consumer.accept(p);
            traverse(p.left, consumer);
        }

        if (root instanceof Regular r) {
            consumer.accept(r);
        }

        if (root instanceof Pair p) {
            consumer.accept(p);
            traverse(p.right, consumer);
        }
    }

    static Stream<Node> stream(Node root) {
        var builder = Stream.<Node>builder();

        traverse(root, builder::accept);

        return builder.build();
    }

    static Node of(String s) {
        int cursor = 1;
        Pair root = new Pair();
        Pair current = root;

        while (cursor < s.length()) {
            char c = s.charAt(cursor++);

            if (c == '[') {
                var pair = new Pair();

                if (current.left == null) {
                    current.left = pair;
                } else {
                    current.right = pair;
                }

                pair.parent = current;
                current = pair;
            } else if (Character.isDigit(c)) {
                var digit = Character.getNumericValue(c);
                var regular = new Regular();

                regular.value = digit;

                if (current.left == null) {
                    current.left = regular;
                } else {
                    current.right = regular;
                }

                regular.parent = current;
            } else if (c == ']') {
                current = (Pair) current.parent;
            }
        }

        return root;
    }

    int depth() {
        var current = this;
        var depth = 0;

        while (current != null) {
            current = current.parent;
            depth++;
        }

        return depth - 1;

    }
}

class Regular extends Node {

    int value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

class Pair extends Node {

    Node left, right;

    @Override
    public String toString() {
        return "[" + left + "," + right + "]";
    }
}
