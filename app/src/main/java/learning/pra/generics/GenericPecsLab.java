package learning.pra.generics;

import java.util.List;

public class GenericPecsLab {

    public static <T> void copyAll(List<? extends T> src, List<? super T> dst) {
        for (T object : src) {
            dst.addLast(object);
        }
    }

}
