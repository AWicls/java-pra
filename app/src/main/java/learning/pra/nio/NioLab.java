package learning.pra.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.ByteBuffer;
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

    public static int bufferFlipDemo() {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 10);
        buffer.put((byte) 20);
        buffer.put((byte) 30);
        buffer.flip();
        int sum = 0;
        while (buffer.hasRemaining()) {
            sum += buffer.get();
        }
        return sum;
    }

    public static String writeLines(String file, List<String> lines) {
        Path path = Path.of(file);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            String content = String.join(System.lineSeparator(), lines);
            Path writeString = Files.writeString(path, content);
            return Files.readString(writeString);
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public static boolean filesIdentical(String a, String b) throws IOException {
        Path pa = Path.of(a);
        Path pb = Path.of(b);
        long mismatch = Files.mismatch(pa, pb);
        return mismatch == -1 ? true : false;
    }

}
