package learning.pra.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConcurrencyLabTest {

    @Test
    @DisplayName("ConcurrencyLabTest_线程名称")
    void ConcurrencyLabTest_线程名称() {
        String threadStart = ConcurrencyLab.threadStart("testName");
        assertEquals("testName", threadStart);
    }

}
