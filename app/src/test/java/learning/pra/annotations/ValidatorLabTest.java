package learning.pra.annotations;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorLabTest {

    // ========== 辅助构造方法 ==========

    private ValidatorLab.User validUser() {
        ValidatorLab.User u = new ValidatorLab.User();
        u.name = "小明";
        u.nickname = "xiaoming123";
        u.age = 20;
        return u;
    }

    // ========== 测试用例 ==========

    @Test
    void validate_all_valid_returns_empty() throws Exception {
        ValidatorLab.User u = validUser();
        List<String> errors = ValidatorLab.validate(u);
        assertTrue(errors.isEmpty(), "全合规对象应返回空 List");
    }

    @Test
    void validate_name_null_reports_notnull_error() throws Exception {
        ValidatorLab.User u = validUser();
        u.name = null;
        List<String> errors = ValidatorLab.validate(u);
        // 至少包含 name 的 NotNull 错误
        boolean hasNameError = errors.stream().anyMatch(e -> e.contains("name") && e.contains("null"));
        assertTrue(hasNameError, "应报告 name 不能为 null");
    }

    @Test
    void validate_nickname_too_short_reports_length_error() throws Exception {
        ValidatorLab.User u = validUser();
        u.nickname = "ab";   // 长度 2，违反 min=3
        List<String> errors = ValidatorLab.validate(u);
        boolean hasLengthError = errors.stream()
                .anyMatch(e -> e.contains("nickname") && e.contains("名字长度 3-20"));
        assertTrue(hasLengthError, "应报告 nickname 长度不合法");
    }

    @Test
    void validate_nickname_too_long_reports_length_error() throws Exception {
        ValidatorLab.User u = validUser();
        u.nickname = "a".repeat(25);   // 长度 25，违反 max=20
        List<String> errors = ValidatorLab.validate(u);
        boolean hasLengthError = errors.stream()
                .anyMatch(e -> e.contains("nickname") && e.contains("名字长度 3-20"));
        assertTrue(hasLengthError, "应报告 nickname 长度不合法");
    }

    @Test
    void validate_nickname_boundary_max_ok() throws Exception {
        ValidatorLab.User u = validUser();
        u.nickname = "a".repeat(20);   // 长度 20，刚好等于 max，合规
        List<String> errors = ValidatorLab.validate(u);
        boolean hasLengthError = errors.stream().anyMatch(e -> e.contains("nickname"));
        assertFalse(hasLengthError, "长度等于 max 应合规");
    }

    @Test
    void validate_age_over_max_reports_range_error() throws Exception {
        ValidatorLab.User u = validUser();
        u.age = 200;   // 违反 max=150
        List<String> errors = ValidatorLab.validate(u);
        boolean hasRangeError = errors.stream()
                .anyMatch(e -> e.contains("age") && e.contains("年龄 0-150"));
        assertTrue(hasRangeError, "应报告 age 范围越界");
    }

    @Test
    void validate_age_below_min_reports_range_error() throws Exception {
        ValidatorLab.User u = validUser();
        u.age = -1;   // 违反 min=0
        List<String> errors = ValidatorLab.validate(u);
        boolean hasRangeError = errors.stream()
                .anyMatch(e -> e.contains("age") && e.contains("年龄 0-150"));
        assertTrue(hasRangeError, "应报告 age 范围越界");
    }

    @Test
    void validate_age_boundary_min_max_ok() throws Exception {
        ValidatorLab.User u = validUser();
        u.age = 0;     // 边界值，合规
        List<String> errors = ValidatorLab.validate(u);
        boolean hasRangeError = errors.stream().anyMatch(e -> e.contains("age"));
        assertFalse(hasRangeError, "age=0 边界值应合规");

        u.age = 150;   // 边界值，合规
        errors = ValidatorLab.validate(u);
        hasRangeError = errors.stream().anyMatch(e -> e.contains("age"));
        assertFalse(hasRangeError, "age=150 边界值应合规");
    }

    @Test
    void validate_multiple_violations_reports_all() throws Exception {
        ValidatorLab.User u = new ValidatorLab.User();
        u.name = null;        // 违反 @NotNull
        u.nickname = "ab";    // 违反 @Length(min=3)
        u.age = 200;          // 违反 @Range(max=150)
        List<String> errors = ValidatorLab.validate(u);
        assertEquals(3, errors.size(), "三个字段都违规，应返回 3 条错误");
    }

    @Test
    void validate_nickname_null_only_reports_notnull_not_length() throws Exception {
        // 边界：nickname 为 null 时，@NotNull 会报错，但 @Length 不应该 NPE
        ValidatorLab.User u = validUser();
        u.nickname = null;   // nickname 没贴 @NotNull，所以不报 NotNull
        // 但 @Length 跳过 null 值（让 @NotNull 管），所以也不报长度错误
        List<String> errors = ValidatorLab.validate(u);
        boolean hasNicknameError = errors.stream().anyMatch(e -> e.contains("nickname"));
        assertFalse(hasNicknameError, "nickname 为 null 时 @Length 应跳过（不应 NPE 或误报长度）");
    }
}
