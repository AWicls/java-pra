package learning.pra.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class NioLab {

    public static String writeAndRead(String dir, String filename, String content) throws IOException {
        Path path = Path.of(dir, filename);
        Files.createDirectories(path.getParent());
        Path writeString = Files.writeString(path, content);
        String string = Files.readString(writeString);
        return string;
    }

    public static List<String> readAllLines(String file) {
        try {
            return Files.readAllLines(Path.of(file));
        } catch (Exception e) {
            return List.of();
        }
    }

    public static String fileCensus(String dir) throws IOException {
        Path p = Path.of(dir);
        Files.createDirectories(p);
        Files.writeString(p.resolve("a.txt"), "A");
        Files.writeString(p.resolve("b.txt"), "BB");
        Files.writeString(p.resolve("c.log"), "CCC");
        long count = Files.list(p).count();
        long count2 = Files.list(p).filter(path -> path.getFileName().toString().endsWith(".txt")).count();
        long count3 = Files.list(p).mapToLong(path -> {
            try {
                return Files.size(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }).sum();
        return "total=" + count + " txt=" + count2 + " bytes=" + count3;
    }

}
