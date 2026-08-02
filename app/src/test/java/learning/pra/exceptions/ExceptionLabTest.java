package learning.pra.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionLabTest {

    @Test
    void readFileLine_returnsFirstLine() throws IOException {
        Path dir = Path.of("build", "tmp", "exc-lab-test");   // workspace 内可写，沙箱 /tmp 只读
        Files.createDirectories(dir);
        Path file = dir.resolve("test.txt");
        try {
            Files.writeString(file, "hello\nworld");
            assertEquals("hello", ExceptionLab.readFileLine(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void readFileLine_missingPath_throwsIOException() {
        Path missing = Path.of("build", "tmp", "exc-lab-not-exist-" + System.nanoTime() + ".txt");
        assertThrows(IOException.class, () -> ExceptionLab.readFileLine(missing.toString()));
    }

    @Test
    void firestOf_returnsFirstElement() {
        assertEquals(42, ExceptionLab.firstOf(new int[]{42, 7, 9}));
    }

    @Test
    void firestOf_emptyArray_throwsArrayIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ExceptionLab.firstOf(new int[0]));
    }
}
