package learning.pra.concurrent;

public class ConcurrencyLab {

    // 用 Runnable + start() 返回新线程的名字
    // 要求：必须真正 start() 新线程，不能在主线程同步跑
    // 提示：在线程体内调 Thread.currentThread().getName()
    //       用某种方式把名字传回主线程（想一想怎么跨线程传值）
    public static String threadStart(String threadName) {

        String[] name = new String[1];

        Runnable task = () -> {
            name[0] = Thread.currentThread().getName();
        };
        Thread t1 = new Thread(task, threadName);

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();   // 恢复中断标志
            throw new RuntimeException(e);          // 转成非受检异常抛出
        }

        return name[0];
    }

}
