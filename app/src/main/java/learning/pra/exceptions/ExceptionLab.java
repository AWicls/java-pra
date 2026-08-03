package learning.pra.exceptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.management.RuntimeErrorException;

public class ExceptionLab {

    public static String readFileLine(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();
        }
    }

    public static int firstOf(int[] arr) {
        return arr[0];
    }

    static class Resource implements AutoCloseable {

        final String name;

        Resource(String name) {
            this.name = name;
            System.out.println("[" + name + "] opened");
        }

        @Override
        public void close() throws Exception {
            System.out.println("[" + name + "] close");
        }

        public void use() {
            System.out.println("[" + name + "] used");
        }
    };

    public static String demoResourceOrder() throws Exception {
        try (Resource a = new Resource("A"); Resource b = new Resource("B")) {
            a.use();
            b.use();
            return "ok";
        }
    }

    public static String finallySwallowsReturn() {
        try {
            return "yes1";
        } finally {
            return "finally";
        }
    }

    public static String loadConfig(String path) {
        try {
            return readFileLine(path);
        } catch (IOException e) {
            throw new ConfigException("failed to load config", e);
        }
    }

    public static Throwable unwrapRoot(Throwable ex) {
        Throwable current = ex;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        return current;

    }

}
