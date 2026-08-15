package learning.pra.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * IoLab 流式 IO 的单元测试（第七课）。
 *
 * <p>覆盖字符/字节流读写往返、空行保留、缺失文件抛 IOException、覆盖写、
 * 分块拷贝、字节/字符桥一致性、NIO 版读写复制。
 *
 * @see IoLab
 */
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

    @Test
    void readTextFileWithBytes_matchesReadTextFile() throws IOException {
        Path file = newTempFile("bytes-vs-chars-" + System.nanoTime() + ".txt");
        try {
            String content = "line1\nline2\n\nline4\n中文content";
            IoLab.writeTextFile(file.toString(), content);
            String viaChars = IoLab.readTextFile(file.toString());
            String viaBytes = IoLab.readTextFileWithBytes(file.toString());
            assertEquals(viaChars, viaBytes, "字节流+桥 读取结果应与字符流一致");
            assertEquals("line1\nline2\n\nline4\n中文content\n", viaBytes);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void copyFile_makesIdenticalCopy() throws IOException {
        Path src = newTempFile("copy-src-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("copy-dst-" + System.nanoTime() + ".bin");
        try {
            byte[] content = new byte[30000];   // 超过 8KB 缓冲，验证分块拷贝
            for (int i = 0; i < content.length; i++) {
                content[i] = (byte) (i % 256);
            }
            Files.write(src, content);
            IoLab.copyFile(src.toString(), dst.toString());
            assertEquals(-1, Files.mismatch(src, dst), "复制后两文件应完全相同");
        } finally {
            Files.deleteIfExists(src);
            Files.deleteIfExists(dst);
        }
    }

    @Test
    void copyFile_emptyFile_copies() throws IOException {
        Path src = newTempFile("copy-empty-src-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("copy-empty-dst-" + System.nanoTime() + ".bin");
        try {
            Files.createFile(src);   // 显式创建空文件
            IoLab.copyFile(src.toString(), dst.toString());
            assertEquals(0, Files.size(dst), "空文件复制后应为空");
            assertEquals(-1, Files.mismatch(src, dst));
        } finally {
            Files.deleteIfExists(src);
            Files.deleteIfExists(dst);
        }
    }

    @Test
    void copyFile_missingSource_throwsIOException() throws IOException {
        Path src = Path.of("build", "tmp", "io-copy-not-exist-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("copy-missing-dst-" + System.nanoTime() + ".bin");
        try {
            assertThrows(IOException.class, () -> IoLab.copyFile(src.toString(), dst.toString()));
        } finally {
            Files.deleteIfExists(dst);
        }
    }

    @Test
    void sumNumbersFromFile_sumsAllNumbers() throws IOException {
        Path file = newTempFile("sum-" + System.nanoTime() + ".txt");
        try {
            IoLab.writeTextFile(file.toString(), "1\n2\n3\n");
            assertEquals(6, IoLab.sumNumbersFromFile(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void sumNumbersFromFile_emptyFile_returnsZero() throws IOException {
        Path file = newTempFile("sum-empty-" + System.nanoTime() + ".txt");
        try {
            Files.createFile(file);
            assertEquals(0, IoLab.sumNumbersFromFile(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void writeLines_writesEachLine() throws IOException {
        Path file = newTempFile("lines-" + System.nanoTime() + ".txt");
        try {
            IoLab.writeLines(file.toString(), "第一行", "第二行", "第三行");
            String read = IoLab.readTextFile(file.toString());
            assertEquals("第一行\n第二行\n第三行\n", read);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void readTextFileNio_returnsRawContent() throws IOException {
        Path file = newTempFile("nio-read-" + System.nanoTime() + ".txt");
        try {
            IoLab.writeTextFile(file.toString(), "a\nb");
            // readString 不补末尾换行，返回原始内容
            assertEquals("a\nb", IoLab.readTextFileNio(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void copyFileNio_makesIdenticalCopy() throws IOException {
        Path src = newTempFile("nio-copy-src-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("nio-copy-dst-" + System.nanoTime() + ".bin");
        try {
            byte[] content = new byte[20000];
            for (int i = 0; i < content.length; i++) {
                content[i] = (byte) i;
            }
            Files.write(src, content);
            IoLab.copyFileNio(src.toString(), dst.toString());
            assertEquals(-1, Files.mismatch(src, dst), "复制后应完全相同");
        } finally {
            Files.deleteIfExists(src);
            Files.deleteIfExists(dst);
        }
    }

    @Test
    void copyFileNio_overwritesExisting() throws IOException {
        Path src = newTempFile("nio-ov-src-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("nio-ov-dst-" + System.nanoTime() + ".bin");
        try {
            Files.writeString(src, "new data");
            Files.writeString(dst, "old data");
            IoLab.copyFileNio(src.toString(), dst.toString());
            assertEquals("new data", Files.readString(dst), "REPLACE_EXISTING 应覆盖旧内容");
        } finally {
            Files.deleteIfExists(src);
            Files.deleteIfExists(dst);
        }
    }

    @Test
    void copyFileNio_missingSource_throwsIOException() throws IOException {
        Path src = Path.of("build", "tmp", "io-nio-copy-not-exist-" + System.nanoTime() + ".bin");
        Path dst = newTempFile("nio-copy-missing-dst-" + System.nanoTime() + ".bin");
        try {
            assertThrows(IOException.class, () -> IoLab.copyFileNio(src.toString(), dst.toString()));
        } finally {
            Files.deleteIfExists(dst);
        }
    }
}
