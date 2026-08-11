package learning.pra.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NioLabTest {

    private static final String DIR = "build/tmp/nio-lab";

    @Test
    void writeAndRead_roundTripsContent() throws IOException {
        String content = "你好 NIO，中文内容!";
        String result = NioLab.writeAndRead(DIR, "roundtrip.txt", content);
        assertEquals(content, result);
    }

    @Test
    void writeAndRead_createsParentDirectories() throws IOException {
        String nestedDir = DIR + "/a/b/c";
        NioLab.writeAndRead(nestedDir, "nested.txt", "deep");
        assertTrue(Files.exists(Path.of(nestedDir, "nested.txt")));
    }

    @Test
    void readAllLines_returnsEachLine() throws IOException {
        Path p = Path.of(DIR, "lines.txt");
        Files.createDirectories(p.getParent());
        Files.writeString(p, "第一行\n第二行\n第三行");
        List<String> lines = NioLab.readAllLines(p.toString());
        assertEquals(List.of("第一行", "第二行", "第三行"), lines);
    }

    @Test
    void fileCensus_countsTotalTxtAndBytes() throws IOException {
        String result = NioLab.fileCensus(DIR + "/census");
        assertEquals("total=3 txt=2 bytes=6", result);
    }
}
