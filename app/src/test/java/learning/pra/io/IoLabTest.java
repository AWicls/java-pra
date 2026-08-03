package learning.pra.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IoLabTest {

    private Path newTempFile(String name) throws IOException {
        Path dir = Path.of("build", "tmp", "io-lab-test");
        Files.createDirectories(dir);
        return dir.resolve(name);
    }

    @Test
    void writeAndRead_roundTrip_preservesContent() throws IOException {
        Path file = newTempFile("roundtrip-" + System.nanoTime() + ".txt");
        try {
            IoLab.writeTextFile(file.toString(), "hello\nworld");
            String read = IoLab.readTextFile(file.toString());
            // readLine 每行加 \n，所以读回时末尾多一个 \n
            assertEquals("hello\nworld\n", read);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void readTextFile_emptyLinesPreserved() throws IOException {
        Path file = newTempFile("empty-lines-" + System.nanoTime() + ".txt");
        try {
            // 三行：第一行有内容，第二行空行，第三行有内容
            IoLab.writeTextFile(file.toString(), "a\n\nb");
            String read = IoLab.readTextFile(file.toString());
            // 期望 "a\n\nb\n"：空行 readLine 返回 ""，append("") 后加 \n -> "\n"
            assertEquals("a\n\nb\n", read);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void readTextFile_missingPath_throwsIOException() {
        Path missing = Path.of("build", "tmp", "io-lab-not-exist-" + System.nanoTime() + ".txt");
        assertThrows(IOException.class, () -> IoLab.readTextFile(missing.toString()));
    }

    @Test
    void writeTextFile_overwritesExistingFile() throws IOException {
        Path file = newTempFile("overwrite-" + System.nanoTime() + ".txt");
        try {
            IoLab.writeTextFile(file.toString(), "old content");
            IoLab.writeTextFile(file.toString(), "new content");
            String read = IoLab.readTextFile(file.toString());
            assertTrue(read.contains("new content"), "应覆盖旧内容");
            assertEquals("new content\n", read);
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
