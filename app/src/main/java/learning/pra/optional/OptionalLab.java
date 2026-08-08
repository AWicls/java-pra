package learning.pra.optional;

import java.util.Optional;

public class OptionalLab {

    public static boolean isPresent(Optional<String> opt) {
            return opt.isPresent();
    }

    public static String orElseDefault(Optional<String> opt){
        return opt.orElse("default");
    }

    public static String orElseThrow(Optional<String> opt) {
        return opt.orElseThrow(() -> new IllegalArgumentException());
    }

    public static int mapLength(Optional<String> opt) {
        return opt.map(s -> s.length()).orElse(0);
    }

    public static Optional<String> flatMapWrap(Optional<String> opt) {
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        return opt.flatMap(s -> Optional.of(s + "-wrapped"));
    }

    public static Optional<String> filterLong(Optional<String> opt) {
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        return opt.filter(s -> s.length() >= 5);
    }

}
