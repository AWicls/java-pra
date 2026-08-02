package learning.pra.exceptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExceptionLab {

        public static String readFileLine(String path) throws IOException {
            try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
                return reader.readLine();
            }
        }

        public static int firstOf(int[] arr) {
            return arr[0];
        }

}
