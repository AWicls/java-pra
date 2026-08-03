package learning.pra.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * IO 流学习实验：字符流、字节流、装饰器、Scanner/PrintWriter、NIO.2。
 *
 * <p>本类演示第七课知识点：<br>
 * 1. {@link #readTextFile} / {@link #writeTextFile}：字符流 + try-with-resources<br>
 * 2. {@link #readTextFileWithBytes}：字节流 + 装饰器四层套娃 + 显式 UTF-8<br>
 * 3. {@link #copyFile}：8KB 缓冲流式复制（读-写循环）<br>
 * 4. {@link #sumNumbersFromFile}：Scanner 按类型解析<br>
 * 5. {@link #writeLines}：PrintWriter 逐行写<br>
 * 6. {@link #readTextFileNio} / {@link #copyFileNio}：NIO.2 现代 API
 */
public class IoLab {

    /**
     * 用字符流（FileReader + BufferedReader）读取整个文本文件。
     *
     * <p>每行经 {@code readLine()} 读取后追加 {@code \n}，故返回的字符串末尾
     * 会多一个换行（与 {@link #readTextFileNio} 的原始内容不同）。
     *
     * @param path 文件路径
     * @return 文件全部内容（每行后补 {@code \n}）
     * @throws IOException 文件不存在或读取失败时抛出
     */
    public static String readTextFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 用字符流（FileWriter + BufferedWriter）写入文本文件。
     *
     * <p>默认覆盖已有文件；关闭时缓冲自动 flush。
     *
     * @param path    目标文件路径
     * @param content 要写入的文本内容
     * @throws IOException 写入失败时抛出
     */
    public static void writeTextFile(String path, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        }
    }

    /**
     * 用字节流 + 装饰器读取文本文件（演示四层套娃 + 显式 UTF-8）。
     *
     * <p>装饰器链从内到外：FileInputStream → BufferedInputStream → InputStreamReader → BufferedReader。
     * 结果与 {@link #readTextFile} 一致（每行后补 {@code \n}）。
     *
     * @param path 文件路径
     * @return 文件全部内容（每行后补 {@code \n}）
     * @throws IOException 文件不存在或读取失败时抛出
     */
    public static String readTextFileWithBytes(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new BufferedInputStream(new FileInputStream(path)), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 用字节流 + 8KB 缓冲块流式复制文件（支持大文件，不整读进内存）。
     *
     * <p>经典读-写循环：{@code (n = read(buffer)) != -1} 边读边判末尾，
     * {@code write(buffer, 0, n)} 只写实际读到的 n 字节。
     *
     * @param src 源文件路径
     * @param dst 目标文件路径（不存在则创建）
     * @throws IOException 源不存在或读写失败时抛出
     */
    public static void copyFile(String src, String dst) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dst))) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }
        }
    }

    /**
     * 用 Scanner 读取文件中的全部整数并求和。
     *
     * <p>每行一个数字，用 {@code hasNextInt()} 与 {@code nextInt()} 配对读取。
     *
     * @param path 文件路径
     * @return 所有整数之和；无数字时返回 0
     * @throws IOException 文件不存在或读取失败时抛出
     */
    public static int sumNumbersFromFile(String path) throws IOException {
        int num = 0;
        try (Scanner sc = new Scanner(Path.of(path), StandardCharsets.UTF_8)) {
            while (sc.hasNextInt()) {
                num += sc.nextInt();
            }
            return num;
        }
    }

    /**
     * 用 PrintWriter 逐行写入文件。
     *
     * <p>可变参数 {@code lines} 在方法内是 {@code String[]}，需遍历逐行 println。
     *
     * @param path  目标文件路径
     * @param lines 要写入的各行（可变参数）
     * @throws IOException 写入失败时抛出
     */
    public static void writeLines(String path, String... lines) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    /**
     * 用 NIO.2 读取文本文件，返回原始内容。
     *
     * <p>单行调用，自动按 UTF-8 解码、自动关闭资源，但会把整个文件读进内存
     * （大文件建议用 {@code Files.lines()} 流式处理）。与 {@link #readTextFile}
     * 不同：不补末尾换行。
     *
     * @param path 文件路径
     * @return 文件原始内容（不含末尾补换行）
     * @throws IOException 文件不存在或读取失败时抛出
     */
    public static String readTextFileNio(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    /**
     * 用 NIO.2 复制文件，覆盖目标已存在文件。
     *
     * @param src 源文件路径
     * @param dst 目标文件路径
     * @throws IOException 源不存在或复制失败时抛出
     */
    public static void copyFileNio(String src, String dst) throws IOException {
        Files.copy(Path.of(src), Path.of(dst), StandardCopyOption.REPLACE_EXISTING);
    }

}
