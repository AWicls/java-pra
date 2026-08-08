package learning.pra.optional;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalLabTest {

    @Test
    void ofWithValueIsPresent() {
        assertTrue(OptionalLab.isPresent(Optional.of("hello")));
    }

    @Test
    void ofNullableNullIsEmpty() {
        assertFalse(OptionalLab.isPresent(Optional.ofNullable(null)));
    }

    @Test
    void emptyIsNotPresent() {
        assertFalse(OptionalLab.isPresent(Optional.empty()));
    }

    @Test
    void ofNullThrowsNpe() {
        assertThrows(NullPointerException.class, () -> Optional.of(null));
    }
}
