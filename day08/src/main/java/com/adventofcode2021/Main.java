package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var pairs = Files.lines(Paths.get("8.in")).map(Pair::of).toList();
        var lengths = List.of(2, 3, 4, 7);

        System.out.println("Part 1: " + pairs.stream().flatMap(p -> p.digits().stream()).mapToInt(String::length).filter(lengths::contains).count());
        System.out.println("Part 2: " + pairs.stream().map(Main::decipher).mapToInt(Integer::parseInt).sum());
    }

    static String decipher(Pair pair) {
        var lengths = pair.signals().stream().collect(Collectors.groupingBy(String::length));

        var one = lengths.get(2).get(0);
        var seven = lengths.get(3).get(0);
        var four = lengths.get(4).get(0);
        var eight = lengths.get(7).get(0);
        var three = lengths.get(5).stream().filter(s -> contains(s, one)).findAny().orElseThrow();
        var five = lengths.get(5).stream().filter(s -> contains(s, difference(one, four))).findAny().orElseThrow();
        var two = lengths.get(5).stream().filter(s -> !s.equals(three) && !s.equals(five)).findAny().orElseThrow();
        var nine = lengths.get(6).stream().filter(s -> contains(s, three)).findAny().orElseThrow();
        var zero = lengths.get(6).stream().filter(s -> !s.equals(nine) && contains(s, one)).findAny().orElseThrow();
        var six = lengths.get(6).stream().filter(s -> !s.equals(nine) && !s.equals(zero)).findAny().orElseThrow();
        
        var translations = List.of(zero, one, two, three, four, five, six, seven, eight, nine).stream().map(Main::sort).toList();

        return pair.digits().stream().map(Main::sort).map(translations::indexOf).map(String::valueOf).collect(Collectors.joining());
    }

    static boolean contains(String s1, String s2) {
        return s2.chars().mapToObj(i -> (char) i).map(String::valueOf).allMatch(s1::contains);
    }

    static String difference(String s1, String s2) {
        var buffer = new StringBuffer();

        s1.chars().filter(c -> s2.indexOf(c) == -1).mapToObj(i -> (char) i).forEach(buffer::append);
        s2.chars().filter(c -> s1.indexOf(c) == -1).mapToObj(i -> (char) i).forEach(buffer::append);

        return buffer.toString();
    }
    
    static String sort(String s) {
        return s.chars().mapToObj(i -> (char) i).map(String::valueOf).sorted().collect(Collectors.joining());
    }
}

record Pair(List<String> signals, List<String> digits) {

    static Pattern pattern = Pattern.compile("\\w+");

    static Pair of(String s) {
        var groups = pattern.matcher(s).results().map(MatchResult::group).toList();

        return new Pair(groups.subList(0, 10), groups.subList(10, 14));
    }
}
