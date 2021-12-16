package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        var conversions = """
                         0 = 0000
                         1 = 0001
                         2 = 0010
                         3 = 0011
                         4 = 0100
                         5 = 0101
                         6 = 0110
                         7 = 0111
                         8 = 1000
                         9 = 1001
                         A = 1010
                         B = 1011
                         C = 1100
                         D = 1101
                         E = 1110
                         F = 1111
                         """.lines().collect(Collectors.toMap(s -> s.substring(0, 1), s -> s.substring(4)));

        var transmission = Files.readString(Paths.get("16.in")).chars().mapToObj(c -> String.valueOf((char) c)).map(conversions::get).collect(Collectors.joining());
        var packet = Packet.of(transmission);

        System.out.println("Part 1: " + sum(packet));
        System.out.println("Part 2: " + packet.value());
    }

    static int sum(Packet packet) {
        var sum = packet.version();

        if (packet instanceof LengthOperator p) {
            sum += p.packets().stream().mapToInt(Main::sum).sum();
        }

        if (packet instanceof SubPacketOperator p) {
            sum += p.packets().stream().mapToInt(Main::sum).sum();
        }

        return sum;
    }
}

interface Packet {

    int version();

    int typeId();

    int length();

    long value();

    static Packet of(String transmission) {
        var cursor = 0;

        var version = Integer.parseInt(transmission.substring(cursor, cursor + 3), 2);
        var typeId = Integer.parseInt(transmission.substring(cursor + 3, cursor + 6), 2);

        var packet = switch (typeId) {
            case 4 ->
                Literal.of(version, typeId, transmission);
            default ->
                transmission.charAt(cursor + 6) == '0'
                ? LengthOperator.of(version, typeId, 0, transmission)
                : SubPacketOperator.of(version, typeId, 1, transmission);
        };

        return packet;
    }
}

record Literal(int version, int typeId, long value, int length) implements Packet {

    static Literal of(int version, int typeId, String transmission) {
        var cursor = 6;
        var literal = new StringBuilder();

        while (transmission.charAt(cursor) == '1') {
            literal.append(transmission.substring(cursor + 1, cursor + 5));
            cursor += 5;
        }

        literal.append(transmission.substring(cursor + 1, cursor + 5));
        cursor += 5;

        return new Literal(version, typeId, Long.parseLong(literal.toString(), 2), cursor);
    }
}

record LengthOperator(int version, int typeId, int lengthTypeId, int bits, List<Packet> packets) implements Packet {

    @Override
    public int length() {
        return 3 + 3 + 1 + 15 + bits;
    }

    @Override
    public long value() {
        return switch (typeId) {
            case 0 -> packets.stream().mapToLong(Packet::value).sum();
            case 1 -> packets.stream().mapToLong(Packet::value).reduce(1, (a, b) -> a * b);
            case 2 -> packets.stream().mapToLong(Packet::value).min().orElseThrow();
            case 3 -> packets.stream().mapToLong(Packet::value).max().orElseThrow();
            case 5 -> packets.get(0).value() > packets().get(1).value() ? 1L : 0L;
            case 6 -> packets.get(0).value() < packets().get(1).value() ? 1L : 0L;
            case 7 -> packets.get(0).value() == packets().get(1).value() ? 1L : 0L;
            default -> throw new IllegalArgumentException();
        };
    }

    static LengthOperator of(int version, int typeId, int lenghTypeId, String transmission) {
        var cursor = 7;

        var bits = Integer.parseInt(transmission.substring(cursor, cursor + 15), 2);
        cursor += 15;

        var packets = new ArrayList<Packet>();

        while (cursor < 7 + 15 + bits) {
            var packet = Packet.of(transmission.substring(cursor));
            cursor += packet.length();
            packets.add(packet);
        }

        return new LengthOperator(version, typeId, lenghTypeId, bits, packets);
    }
}

record SubPacketOperator(int version, int typeId, int lengthTypeId, List<Packet> packets, int length) implements Packet {

    @Override
    public int length() {
        return 3 + 3 + 1 + 11 + packets.stream().mapToInt(Packet::length).sum();
    }

    @Override
    public long value() {
        return switch (typeId) {
            case 0 -> packets.stream().mapToLong(Packet::value).sum();
            case 1 -> packets.stream().mapToLong(Packet::value).reduce(1, (a, b) -> a * b);
            case 2 -> packets.stream().mapToLong(Packet::value).min().orElseThrow();
            case 3 -> packets.stream().mapToLong(Packet::value).max().orElseThrow();
            case 5 -> packets.get(0).value() > packets().get(1).value() ? 1L : 0L;
            case 6 -> packets.get(0).value() < packets().get(1).value() ? 1L : 0L;
            case 7 -> packets.get(0).value() == packets().get(1).value() ? 1L : 0L;
            default -> throw new IllegalArgumentException();
        };
    }

    static SubPacketOperator of(int version, int typeId, int lenghTypeId, String transmission) {
        var cursor = 7;

        var subpackets = Integer.parseInt(transmission.substring(cursor, cursor + 11), 2);
        cursor += 11;

        var packets = new ArrayList<Packet>();

        while (packets.size() < subpackets) {
            var packet = Packet.of(transmission.substring(cursor));
            cursor += packet.length();
            packets.add(packet);
        }

        return new SubPacketOperator(version, typeId, lenghTypeId, packets, cursor);
    }
}