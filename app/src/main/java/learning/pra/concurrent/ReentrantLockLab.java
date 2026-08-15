package learning.pra.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 练习壳类（第十七课概念点 1）：真正的实现在同文件 {@link BankAccount}。
 *
 * <p>演示手动加锁（lock / unlock + finally）、可重入、tryLock 限时获取。
 *
 * @see BankAccount
 */
public class ReentrantLockLab {

}

/**
 * 基于 ReentrantLock 的银行账户：存取手动加锁，tryWithdraw 限时尝试扣款。
 *
 * <p>关键：检查与扣款必须都在锁内（防 TOCTOU 竞态）；lock 后任何路径都要 finally unlock。
 */
class BankAccount {
    private final String accountId;
    private int balance;
    private final ReentrantLock lock = new ReentrantLock();

    BankAccount(String accountId) {
        this.accountId = accountId;
        this.balance = 0;
    }

    public void deposit(int amount) {
        lock.lock();
        try {
            this.balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount) {
        lock.lock();
        try {
            int n = this.balance - amount;
            if (n < 0) {
                throw new IllegalArgumentException();
            }
            this.balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public int getBalance() {
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryWithdraw(int amount, long timeout, TimeUnit unit) throws InterruptedException {
        if (lock.tryLock(timeout, unit)) {
            try {
                if (this.balance - amount < 0) {
                    throw new IllegalArgumentException();
                }
                this.balance -= amount;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

}
