package learning.pra.concurrent;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConditionLab 有界阻塞队列的单元测试（第十七课概念点 2）。
 *
 * <p>覆盖 FIFO、满时 put 阻塞、空时 take 阻塞（CountDownLatch + AtomicBoolean 验证）、
 * 多生产者多消费者不丢不重。
 *
 * @see ConditionLab
 * @see BoundedBlockingQueue
 */
class ConditionLabTest {

    @Test
    void 先进先出() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(3);
        queue.put(1);
        queue.put(2);
        queue.put(3);
        assertEquals(1, queue.take());
        assertEquals(2, queue.take());
        assertEquals(3, queue.take());
        assertEquals(0, queue.size());
    }

    @Test
    void 队列满时put阻塞直到有空间() throws Exception {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(2);
        queue.put(1);
        queue.put(2);   // 已满

        AtomicBoolean thirdPutDone = new AtomicBoolean(false);
        CountDownLatch started = new CountDownLatch(1);
        Thread producer = new Thread(() -> {
            try {
                started.countDown();
                queue.put(3);            // 满了，应阻塞在 await(notFull)
                thirdPutDone.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        started.await();

        Thread.sleep(100);               // 让出 CPU，确认还阻塞着
        assertFalse(thirdPutDone.get()); // 没放进去

        int taken = queue.take();        // 取走一个，腾出位置
        producer.join(1000);             // put(3) 应被唤醒完成
        assertFalse(producer.isAlive());
        assertTrue(thirdPutDone.get());
        assertEquals(2, queue.size());   // 1(剩余) + 3(新放入)
    }

    @Test
    void 队列空时take阻塞直到有元素() throws Exception {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(3);
        AtomicReference<Integer> result = new AtomicReference<>();
        CountDownLatch started = new CountDownLatch(1);
        Thread consumer = new Thread(() -> {
            try {
                started.countDown();
                result.set(queue.take()); // 空，应阻塞在 await(notEmpty)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        started.await();

        Thread.sleep(100);               // 确认还阻塞着
        assertNull(result.get());        // 没取到

        queue.put(42);                   // 放入一个，唤醒消费者
        consumer.join(1000);
        assertFalse(consumer.isAlive());
        assertEquals(42, result.get());
    }

    @Test
    void 多生产者多消费者不丢不重() throws Exception {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(5);
        int producerCount = 2;
        int consumerCount = 2;
        int perProducer = 100;

        Set<Integer> expected = new HashSet<>();
        Thread[] producers = new Thread[producerCount];
        for (int p = 0; p < producerCount; p++) {
            final int base = p * perProducer;   // 生产者 p 放 [base, base+99]
            for (int i = 0; i < perProducer; i++) {
                expected.add(base + i);
            }
            producers[p] = new Thread(() -> {
                try {
                    for (int i = 0; i < perProducer; i++) {
                        queue.put(base + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producers[p].start();
        }

        List<Integer> received = Collections.synchronizedList(new ArrayList<>());
        Thread[] consumers = new Thread[consumerCount];
        for (int c = 0; c < consumerCount; c++) {
            consumers[c] = new Thread(() -> {
                try {
                    for (int i = 0; i < perProducer * producerCount / consumerCount; i++) {
                        received.add(queue.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumers[c].start();
        }

        for (Thread t : producers) t.join();
        for (Thread t : consumers) t.join();

        assertEquals(200, received.size());
        assertEquals(expected, new HashSet<>(received));  // 集合相等 => 不丢不重
        assertEquals(0, queue.size());                    // 全消费完，队列腾空
    }
}
