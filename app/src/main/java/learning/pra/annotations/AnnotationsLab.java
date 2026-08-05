package learning.pra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import learning.pra.annotations.AnnotationsLab.Label;

public class AnnotationsLab {

    public static Map<String, String> demoBuiltInAnnotations() {
        HashMap<String, String> map = new HashMap<>();

        Greeter greeter = new Greeter() {
            @Override
            public String greet(String name) {
                return "greeter";
            }
        };

        Worker worker = new Worker();
        String oldMethod = worker.oldMethod();
        String useOld = worker.useOld();

        MyWorker myWorker = new MyWorker();
        String oldMethod2 = myWorker.oldMethod();

        map.put("FunctionalInterface", greeter.toString());
        map.put("Deprecated", oldMethod);
        map.put("SuppressWarnings", useOld);
        map.put("Override", oldMethod2);

        return map;
    }

    @FunctionalInterface
    interface Greeter {
        String greet(String name);
    }

    static class Worker {
        @Deprecated
        public String oldMethod() {
            return "oldMethod";
        }

        @SuppressWarnings("deprecation")
        public String useOld() {
            return oldMethod();
        }
    }

    static class MyWorker extends Worker {
        @Override
        public String oldMethod() {
            return "MyWorker";
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Label {
        String value();
    }

    static class User {
        @Label("用户名")
        String name;

        @Label("年龄")
        int age;
    }

    public static Map<String, String> readLabels(Class<?> clazz) {
        HashMap<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Label annotation = field.getAnnotation(Label.class);
            if (annotation == null) {
                continue;
            }
            String name = field.getName();
            map.put(name, annotation.value());
        }

        return map;
    }
}
