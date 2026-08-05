package learning.pra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ValidatorLab {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NotNull {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Length {
        int min();

        int max();

        String message() default "长度不合法";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Range {
        int min();

        int max();

        String message() default "范围不合法";
    }

    static class User {
        @NotNull
        String name;

        @Length(min = 3, max = 20, message = "名字长度 3-20")
        String nickname;

        @Range(min = 0, max = 150, message = "年龄 0-150")
        int age;
    }

    public static List<String> validate(Object obj) throws IllegalArgumentException, IllegalAccessException {
        List<String> errors = new ArrayList<>();

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);

            // 检查 1：有没有贴 @NotNull
            if (field.isAnnotationPresent(NotNull.class)) {
                if (value == null) {
                    errors.add(field.getName() + ": 不能为 null");
                }
            }

            // 检查 2：有没有贴 @Length（校验 String 长度）
            Length length = field.getAnnotation(Length.class);
            if (length != null && value != null) {     // value 为 null 时跳过（让 @NotNull 管）
                String str = (String) value;
                if (str.length() < length.min() || str.length() > length.max()) {
                    errors.add(field.getName() + ": " + length.message());
                }
            }

            Range range = field.getAnnotation(Range.class);
            if (range != null && value != null) {
                int n = (int) value;
                if (n < range.min() || n > range.max()) {
                    errors.add(field.getName() + ": " + range.message());
                }
            }

        }

        return errors;
    }

}
