package learning.pra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单校验器实战：注解 + 反射 = 声明式校验。
 *
 * <p>本类演示第九课 9.3 实战知识点：<br>
 * 1. 自定义三个规则注解 {@link NotNull} / {@link Length} / {@link Range}<br>
 * 2. 在 {@link User} 字段上贴规则，用 {@link #validate(Object)} 反射遍历字段做校验<br>
 * 3. 这是 Spring {@code @Valid} / Hibernate Validator 的简化原理
 */
public class ValidatorLab {

    /** 标记注解：字段值不能为 null（无属性，仅起标记作用）。 */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NotNull {
    }

    /**
     * 长度校验注解：String 字段长度必须在 [min, max] 区间。
     *
     * <p>多属性示例，演示：无默认值的属性必须赋值，带 default 的可省略。
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Length {
        /** 最小长度（含）。 */
        int min();

        /** 最大长度（含）。 */
        int max();

        /** 校验失败时的错误提示，默认"长度不合法"。 */
        String message() default "长度不合法";
    }

    /**
     * 数值范围校验注解：int 字段值必须在 [min, max] 区间。
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Range {
        /** 最小值（含）。 */
        int min();

        /** 最大值（含）。 */
        int max();

        /** 校验失败时的错误提示，默认"范围不合法"。 */
        String message() default "范围不合法";
    }

    /** 演示用 User 类：三字段各贴不同校验规则。 */
    static class User {
        @NotNull
        String name;

        @Length(min = 3, max = 20, message = "名字长度 3-20")
        String nickname;

        @Range(min = 0, max = 150, message = "年龄 0-150")
        int age;
    }

    /**
     * 用反射遍历对象的所有字段，读取并执行 @NotNull / @Length / @Range 校验。
     *
     * <p>核心模式：{@code getDeclaredFields()} 拿本类所有字段（含 private） -> 破防 ->
     * 逐个读取字段上的注解 -> 按注解规则校验字段值。
     *
     * <p>规则优先级约定：字段值为 null 时，只让 @NotNull 报错，
     * @Length / @Range 跳过（避免 null 时强转 NPE）。
     *
     * @param obj 待校验对象
     * @return 错误信息列表（字段名: 错误消息）；全部合规返回空 List
     * @throws IllegalAccessException 反射访问字段失败（理论上 setAccessible(true) 后不会发生）
     */
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
