package learning.pra.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockLab {

}

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
