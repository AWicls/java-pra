package learning.pra.concurrent;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture 异步聚合练习（第十七课概念点 3）：用户/商品/物流三个并行任务。
 *
 * <p>三个独立异步任务分别 sleep 模拟耗时，用 {@code thenCombine} 两两合并，
 * 最终 {@code aggregate} 拼出"用户|商品|物流"——验证并行聚合耗时远小于串行和。
 */
public class AsyncAggregateLab {

    public static CompletableFuture<String> fetchUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "用户" + userId;
        });
    }

    public static CompletableFuture<String> fetchProduct(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "商品" + productId;
        });
    }

    public static CompletableFuture<String> fetchLogistics(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "物流" + orderId;
        });
    }

    public static String aggregate(int userId, int productId, int orderId) {
        long start = System.currentTimeMillis();

        CompletableFuture<String> userFuture = fetchUser(userId);
        CompletableFuture<String> productFuture = fetchProduct(productId);
        CompletableFuture<String> logisticsFuture = fetchLogistics(orderId);

        // CompletableFuture.allOf(userFuture, productFuture, logisticsFuture).join();

        // String result = userFuture.join() + " | " + productFuture.join() + " | " + logisticsFuture.join();
        // long elapsed = System.currentTimeMillis() - start;
        // return result + " (耗时" + elapsed + "ms)";


        CompletableFuture<String> userAndProduct = userFuture.thenCombine(productFuture, (a, b) -> a + "|" + b);
        CompletableFuture<String> full = userAndProduct.thenCombine(logisticsFuture, (a, b) -> a + "|" + b);

        String result = full.join();
        // String result = userFuture.join() + " | " + productFuture.join() + " | " +
        // logisticsFuture.join();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("聚合耗时: " + (elapsed - start) + "ms");
        return result;
    }

}
