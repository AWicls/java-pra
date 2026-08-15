package learning.pra.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition 练习壳类（第十七课概念点 2）：真正的实现在同文件 {@link BoundedBlockingQueue}。
 *
 * <p>演示 ReentrantLock + 双 Condition（notFull / notEmpty）手写有界阻塞队列。
 *
 * @see BoundedBlockingQueue
 */
public class ConditionLab {

}

/**
 * 基于 ReentrantLock + Condition 的有界阻塞队列（环形缓冲）。
 *
 * <p>put 满时 await notFull，take 空时 await notEmpty；生产者只叫消费者、消费者只叫生产者。
 * 条件检查必须用 while 防虚假唤醒；环形下标用 {@code % buffer.length} 取模。
 */
class BoundedBlockingQueue<E> {
    private final Object[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    BoundedBlockingQueue(int capacity) {
        this.buffer = new Object[capacity];
    }

    public void put(E item) throws InterruptedException {
        lock.lock();
        try {
            while (this.count == buffer.length) {
                notFull.await();
            }
            buffer[this.tail] = item;
            this.tail = (this.tail + 1) % buffer.length;
            this.count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException {
        lock.lock();
        E result;
        try {
            while (this.count == 0) {
                notEmpty.await();
            }
            // 强转类型
            result = (E) buffer[this.head];
            this.head = (this.head + 1) % buffer.length;
            this.count--;
            notFull.signal();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
