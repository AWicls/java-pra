package learning.pra.concurrent;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class ReentrantLockLabTest {

    @Test
    void 并发存取余额正确() throws InterruptedException {
        BankAccount account = new BankAccount("acc-001");
        int threadCount = 8;
        int depositCountPerThread = 1000;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < depositCountPerThread; j++) {
                    account.deposit(1);
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(8000, account.getBalance());
    }

    @Test
    void 锁可重入同线程重复获取不死锁() throws Exception {
        BankAccount account = new BankAccount("acc-002");
        ReentrantLock lock = grabLock(account);
        lock.lock();
        lock.lock();               // 同一线程第二次获取，可重入 => 不死锁
        assertEquals(2, lock.getHoldCount());
        lock.unlock();
        lock.unlock();
        assertEquals(0, lock.getHoldCount());
    }

    @Test
    void tryLock超时拿不到锁返回false且不扣款() throws Exception {
        BankAccount account = new BankAccount("acc-003");
        ReentrantLock lock = grabLock(account);

        CountDownLatch held = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            lock.lock();
            held.countDown();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            lock.unlock();
        });
        holder.start();
        held.await();

        boolean result = account.tryWithdraw(10, 200, TimeUnit.MILLISECONDS);
        assertFalse(result);
        assertEquals(0, account.getBalance());

        holder.join();
        assertEquals(0, account.getBalance());
    }

    @Test
    void 余额不足抛IllegalArgumentException且余额不变() {
        BankAccount account = new BankAccount("acc-004");
        account.deposit(100);
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(101));
        assertEquals(100, account.getBalance());
    }

    private static ReentrantLock grabLock(BankAccount account) throws Exception {
        Field lockField = BankAccount.class.getDeclaredField("lock");
        lockField.setAccessible(true);   // 反射破防私有字段（第八课知识）
        return (ReentrantLock) lockField.get(account);
    }
}
