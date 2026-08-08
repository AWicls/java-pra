package learning.pra.optional;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void orElseDefaultWithValue() {
        assertEquals("hi", OptionalLab.orElseDefault(Optional.of("hi")));
    }

    @Test
    void orElseDefaultWhenEmpty() {
        assertEquals("default", OptionalLab.orElseDefault(Optional.empty()));
    }

    @Test
    void orElseThrowWithValue() {
        assertEquals("hi", OptionalLab.orElseThrow(Optional.of("hi")));
    }

    @Test
    void orElseThrowWhenEmptyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> OptionalLab.orElseThrow(Optional.empty()));
    }

    @Test
    void mapLengthWithValue() {
        assertEquals(5, OptionalLab.mapLength(Optional.of("hello")));
    }

    @Test
    void mapLengthWhenEmpty() {
        assertEquals(0, OptionalLab.mapLength(Optional.empty()));
    }

    @Test
    void flatMapWrapWithValue() {
        assertEquals(Optional.of("hi-wrapped"), OptionalLab.flatMapWrap(Optional.of("hi")));
    }

    @Test
    void flatMapWrapWhenEmpty() {
        assertEquals(Optional.empty(), OptionalLab.flatMapWrap(Optional.empty()));
    }

    @Test
    void filterLongKeepsLongEnough() {
        assertEquals(Optional.of("hello"), OptionalLab.filterLong(Optional.of("hello")));
    }

    @Test
    void filterLongDropsShort() {
        assertEquals(Optional.empty(), OptionalLab.filterLong(Optional.of("hi")));
    }

    @Test
    void filterLongWhenEmpty() {
        assertEquals(Optional.empty(), OptionalLab.filterLong(Optional.empty()));
    }

    @Test
    void safeGetCityFullPath() {
        OptionalLab.Address addr = new OptionalLab().new Address("Shanghai");
        OptionalLab.User user = new OptionalLab().new User(addr);
        assertEquals("Shanghai", OptionalLab.safeGetCity(user));
    }

    @Test
    void safeGetCityNullAddress() {
        OptionalLab.User user = new OptionalLab().new User(null);
        assertEquals("unknown", OptionalLab.safeGetCity(user));
    }

    @Test
    void safeGetCityNullUser() {
        assertEquals("unknown", OptionalLab.safeGetCity(null));
    }
}
