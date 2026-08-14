package learning.pra.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionLab {

}

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
