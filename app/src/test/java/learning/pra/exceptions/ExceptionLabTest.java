package learning.pra.exceptions;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ExceptionLab 异常体系的单元测试（第六课）。
 *
 * <p>覆盖读文件受检异常、数组越界、try-with-resources 关闭逆序、finally 吞 return、
 * 自定义 {@code ConfigException} 包裹 cause、异常链逐层解包。
 *
 * @see ExceptionLab
 * @see ConfigException
 */
class ExceptionLabTest {

    @Test
    void readFileLine_returnsFirstLine() throws IOException {
        Path dir = Path.of("build", "tmp", "exc-lab-test"); // workspace 内可写，沙箱 /tmp 只读
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
        assertEquals(42, ExceptionLab.firstOf(new int[] { 42, 7, 9 }));
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

    @Test
    void loadConfig_validPath_returnsFirstLine() throws IOException {
        Path dir = Path.of("build", "tmp", "exc-lab-test");
        Files.createDirectories(dir);
        Path file = dir.resolve("cfg.txt");
        try {
            Files.writeString(file, "key=value\nother");
            assertEquals("key=value", ExceptionLab.loadConfig(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadConfig_missingPath_throwsConfigExceptionWithIoCause() {
        Path missing = Path.of("build", "tmp", "exc-lab-cfg-not-exist-" + System.nanoTime() + ".txt");
        ConfigException ex = assertThrows(ConfigException.class,
                () -> ExceptionLab.loadConfig(missing.toString()));
        assertTrue(ex.getCause() instanceof IOException, "cause 应是 IOException");
        assertEquals("failed to load config", ex.getMessage());
    }

    @Test
    void unwrapRoot_singleLayer_returnsItself() {
        RuntimeException alone = new RuntimeException("alone");
        assertSame(alone, ExceptionLab.unwrapRoot(alone));
    }

    @Test
    void unwrapRoot_twoLayers_returnsInnerCause() {
        IOException root = new IOException("io");
        ConfigException wrapper = new ConfigException("wrapped", root);
        assertSame(root, ExceptionLab.unwrapRoot(wrapper));
    }

    @Test
    void unwrapRoot_threeLayers_returnsDeepestCause() {
        IllegalArgumentException deepest = new IllegalArgumentException("deepest");
        IOException middle = new IOException("middle", deepest);
        ConfigException outer = new ConfigException("outer", middle);
        assertSame(deepest, ExceptionLab.unwrapRoot(outer));
    }

    @Test
    void unwrapRoot_fiveLayers_returnsDeepestCause() {
        // 5 层嵌套：E <- D <- C <- B <- A
        IllegalStateException deepest = new IllegalStateException("L5");
        IllegalAccessError l4 = new IllegalAccessError("L4");
        l4.initCause(deepest);
        NumberFormatException l3 = new NumberFormatException("L3");
        l3.initCause(l4);
        ClassCastException l2 = new ClassCastException("L2");
        l2.initCause(l3);
        ConfigException outer = new ConfigException("L1", l2);
        assertSame(deepest, ExceptionLab.unwrapRoot(outer));
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
