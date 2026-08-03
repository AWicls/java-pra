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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class IoLab {

    public static String readTextFile(String path) throws IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public static void writeTextFile(String path, String content) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        }
    }

    public static String readTextFileWithBytes(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(path)), StandardCharsets.UTF_8))) {
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
            //    Files.mismatch(Path.of(src), Path.of(dst));
            }
        }
    }

}
