package learning.pra.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class IoLab {

    public static String readTextFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public static void writeTextFile(String path, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        }
    }

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

    public static void copyFile(String src, String dst) throws IOException {

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(src));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dst))) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
                // Files.mismatch(Path.of(src), Path.of(dst));
            }
        }
    }

    public static int sumNumbersFromFile(String path) throws IOException {
        int num = 0;
        try (Scanner sc = new Scanner(Path.of(path), StandardCharsets.UTF_8)) {
            while (sc.hasNextInt()) {
                num += sc.nextInt();
            }
            return num;
        }
    }

    public static void writeLines(String path, String... lines) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path));) {
            for (String string : lines) {
                writer.println(string);
            }
        }
    }

    public static String readTextFileNio(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    public static void copyFileNio(String src, String dst) throws IOException {
        Files.copy(Path.of(src), Path.of(dst), StandardCopyOption.REPLACE_EXISTING);
    }

}
