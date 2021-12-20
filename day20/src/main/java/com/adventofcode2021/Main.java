package com.adventofcode2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {

        var lines = Files.readAllLines(Paths.get("20.in"));
        var algorithm = lines.get(0);
        var image = new HashSet<Pixel>();

        for (int y = 2; y < lines.size(); y++) {
            var line = lines.get(y);

            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    image.add(new Pixel(x, y - 2));
                }
            }
        }

        var enhanced1x = enhance(algorithm, image, false);
        var enhanced2x = enhance(algorithm, enhanced1x, true);

        System.out.println("Part 1: " + enhanced2x.size());

        var enhanced50x = image;

        for (int i = 0; i < 50; i++) {
            enhanced50x = (HashSet<Pixel>) enhance(algorithm, enhanced50x, i % 2 == 1);
        }

        System.out.println("Part 2: " + enhanced50x.size());

    }

    static Set<Pixel> enhance(String algorithm, Set<Pixel> image, boolean isOdd) {
        var minX = image.stream().mapToInt(Pixel::x).min().orElseThrow();
        var maxX = image.stream().mapToInt(Pixel::x).max().orElseThrow();
        var minY = image.stream().mapToInt(Pixel::y).min().orElseThrow();
        var maxY = image.stream().mapToInt(Pixel::y).max().orElseThrow();

        var offsets = new int[][]{{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {0, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};
        var enhanced = new HashSet<Pixel>();

        for (int y = minY - 10; y < maxY + 10; y++) {
            for (int x = minX - 10; x < maxX + 10; x++) {
                var sb = new StringBuilder();

                for (var offset : offsets) {
                    var pixel = new Pixel(x + offset[0], y + offset[1]);

                    if (isOdd && !(minX < pixel.x() && pixel.x() < maxX && minY < pixel.y() && pixel.y() < maxY)) {
                        sb.append('1');
                    } else {
                        sb.append(image.contains(pixel) ? '1' : '0');
                    }
                }

                var number = sb.toString();
                var index = Integer.parseInt(number, 2);

                if (algorithm.charAt(index) == '#') {
                    enhanced.add(new Pixel(x, y));
                }
            }
        }

        return enhanced;
    }
}

record Pixel(int x, int y) {

}
