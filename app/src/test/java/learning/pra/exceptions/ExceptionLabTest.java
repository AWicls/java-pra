package learning.pra.exceptions;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void firstOf_returnsFirstElement() {
        assertEquals(42, ExceptionLab.firstOf(new int[]{42, 7, 9}));
    }

    @Test
    void firstOf_emptyArray_throwsArrayIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ExceptionLab.firstOf(new int[0]));
    }

    @Test
    void demoResourceOrder_returnsOk() throws Exception {
        assertEquals("ok", ExceptionLab.demoResourceOrder());
    }

    @Test
    void demoResourceOrder_closesInReverseDeclarationOrder() throws Exception {
        String output = captureStdout(() -> {
            try {
                ExceptionLab.demoResourceOrder();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        int openedA = output.indexOf("[A] opened");
        int openedB = output.indexOf("[B] opened");
        int usedA = output.indexOf("[A] used");
        int usedB = output.indexOf("[B] used");
        int closedB = output.indexOf("[B] close");
        int closedA = output.indexOf("[A] close");
        assertTrue(openedA >= 0 && openedB > openedA, "应先开 A 再开 B");
        assertTrue(usedA > openedB && usedB > usedA, "开完后依次使用");
        assertTrue(closedB > usedB, "关 B 在用 B 之后");
        assertTrue(closedA > closedB, "关闭逆序：B 先关，A 后关");
    }

    @Test
    void finallySwallowsReturn_overridesTryReturnValue() {
        assertEquals("finally", ExceptionLab.finallySwallowsReturn());
    }

    private static String captureStdout(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }
}
