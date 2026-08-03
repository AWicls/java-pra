package learning.pra.exceptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 异常体系学习实验：受检/非受检异常、try-with-resources、异常链与自定义异常。
 *
 * <p>本类演示第六课知识点：<br>
 * 1. {@link #readFileLine}：受检异常声明 + try-with-resources<br>
 * 2. {@link #firstOf}：非受检异常不强制处理<br>
 * 3. {@link #demoResourceOrder}：多资源关闭顺序（声明逆序）<br>
 * 4. {@link #finallySwallowsReturn}：finally 覆盖 try 返回值（反模式示例）<br>
 * 5. {@link #loadConfig}：异常包装（IOException → ConfigException）<br>
 * 6. {@link #unwrapRoot}：遍历 cause 链到最内层
 */
public class ExceptionLab {

    /**
     * 用 BufferedReader 读取文件第一行。
     *
     * @param path 文件路径
     * @return 第一行内容；文件为空时返回 {@code null}
     * @throws IOException 文件不存在或读取失败时抛出（受检异常，调用方必须处理）
     */
    public static String readFileLine(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();
        }
    }

    /**
     * 返回数组首元素。
     *
     * <p>演示非受检异常：数组为空时抛 {@link ArrayIndexOutOfBoundsException}，
     * 编译器不强制声明。
     *
     * @param arr 数组
     * @return 首元素
     */
    public static int firstOf(int[] arr) {
        return arr[0];
    }

    /**
     * 演示 try-with-resources 多资源关闭顺序（声明逆序）。
     *
     * <p>声明 A、B 两个资源，关闭顺序为 B 先关、A 后关。
     *
     * @return 恒为 {@code "ok"}
     * @throws Exception 资源关闭时可能抛出
     */
    public static String demoResourceOrder() throws Exception {
        try (Resource a = new Resource("A"); Resource b = new Resource("B")) {
            a.use();
            b.use();
            return "ok";
        }
    }

    /**
     * 演示 finally 块 return 覆盖 try 块 return 值的反模式。
     *
     * <p>finally 永远执行，若其 return 会丢弃 try 的返回值。
     * 实际返回 {@code "finally"}。
     *
     * @return 恒为 {@code "finally"}
     */
    public static String finallySwallowsReturn() {
        try {
            return "yes1";
        } finally {
            return "finally";
        }
    }

    /**
     * 读取配置文件首行，失败时包装为 {@link ConfigException}。
     *
     * <p>演示异常包装：捕获底层 {@link IOException}，作为 {@link ConfigException}
     * 的 cause 上传，避免上层看到底层 IO 细节。
     *
     * @param path 配置文件路径
     * @return 配置首行；文件为空时返回 {@code null}
     * @throws ConfigException 读取失败时抛出，cause 为底层 {@link IOException}
     */
    public static String loadConfig(String path) {
        try {
            return readFileLine(path);
        } catch (IOException e) {
            throw new ConfigException("failed to load config", e);
        }
    }

    /**
     * 遍历 cause 链，返回最内层的根因异常。
     *
     * <p>若传入异常没有 cause，则返回其自身。
     *
     * @param ex 任意异常
     * @return {code getCause()==null} 的最内层异常
     */
    public static Throwable unwrapRoot(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    /**
     * 演示 {@link AutoCloseable} 自定义资源，用于展示 try-with-resources 关闭顺序。
     */
    static class Resource implements AutoCloseable {

        final String name;

        Resource(String name) {
            this.name = name;
            System.out.println("[" + name + "] opened");
        }

        @Override
        public void close() {
            System.out.println("[" + name + "] closed");
        }

        public void use() {
            System.out.println("[" + name + "] used");
        }
    }

}
