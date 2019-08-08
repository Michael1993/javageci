package javax0.geci.tools;

import static javax0.geci.tools.GeciCompatibilityTools.entry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestGeciCompatibilityTools {
    @Test
    @DisplayName("createMap() should create an Empty Map.")
    public void emptyMapShouldBeEmpty() {
        Map<String, Boolean> map = GeciCompatibilityTools.createMap();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    @DisplayName("Can't add items to Map created by createMap().")
    public void cantAddItemsToEmptyMap() {
        Map<Boolean, Integer> map = GeciCompatibilityTools.createMap();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> map.put(false, 1));
    }

    @Test
    @DisplayName("Can create Map with 1 element.")
    public void canCreateMapWithOneElement() {
        Assertions.assertDoesNotThrow(() -> GeciCompatibilityTools.createMap("Baba", "Cdcd"));
    }


    @Test
    @DisplayName("Can create Map with 2 elements.")
    public void canCreateMapWithTwoElements() {
        Assertions.assertDoesNotThrow(() ->
            GeciCompatibilityTools.createMap(
                "Baba", false,
                "CFc", true
            ));
    }

    @Test
    @DisplayName("Can create Map with 3 elements.")
    public void canCreateMapWithThreeElements() {
        Assertions.assertDoesNotThrow(() ->
            GeciCompatibilityTools.createMap(
                "Baba", 0,
                "CFc", 1,
                "hhhh", 2
            ));
    }

    @Test
    @DisplayName("Creating a map using the same key twice will overwrite the first value.")
    public void cantUseSameKeyTwice() {
        Map<Integer, String> map = GeciCompatibilityTools.createMap(
            052, "Hello",
            42, "World!",
            42, "Another value",
            0x2A, "This is it"
        );
        Assertions.assertEquals(1, map.size());
        Assertions.assertEquals("This is it", map.get(42));
    }

    @Test
    @DisplayName("Can create a Map holding more than 10 entries, using the entry() function.")
    public void canCreateLargeMapWithEntries() {
        Map<Integer, String> map = GeciCompatibilityTools.createMapOfEntries(
            entry(1, "A"),
            entry(2, "B"),
            entry(3, "C"),
            entry(4, "D"),
            entry(5, "E"),
            entry(6, "F"),
            entry(7, "G"),
            entry(8, "H"),
            entry(9, "I"),
            entry(10, "J"),
            entry(11, "K"),
            entry(12, "L"),
            entry(13, "M"),
            entry(14, "N")
        );
        Assertions.assertEquals(14, map.size());
        Assertions.assertArrayEquals(
            new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"},
            map.values().stream().sorted().toArray()
        );
        Assertions.assertArrayEquals(
            new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14},
            map.keySet().stream().sorted().toArray()
        );
    }

    @Test
    @DisplayName("Entry created by entry() is immutable.")
    public void immutableEntry() {
        Map.Entry<Integer, String> entry = entry(12, "BABA");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> entry.setValue("ABAB"));
    }

    @Test
    @DisplayName("Can repeat a string with repeat().")
    public void repeat() {
        Assertions.assertEquals("abababababab", GeciCompatibilityTools.repeat("ab", 6));
    }

    @Test
    @DisplayName("Negative repeat number throws IllegalArgumentException.")
    public void illegal() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeciCompatibilityTools.repeat("aba", -12));
    }

    @Test
    @DisplayName("Null string param throws IllegalArgumentException in repeat().")
    public void nullString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GeciCompatibilityTools.repeat(null, 12));
    }

    @Test
    @DisplayName("Empty string repeated any times returns empty string.")
    public void empty() {
        Assertions.assertEquals("", GeciCompatibilityTools.repeat("", 11));
    }

    @Test
    @DisplayName("Repeating a string 0 times returns empty string.")
    public void zeroLength() {
        Assertions.assertEquals("", GeciCompatibilityTools.repeat("ababa", 0));
    }

    @Test
    @DisplayName("Can strip any whitespace character from start of string.")
    public void stripLeading() {
        String test = "\n\t\r\f  Message";
        Assertions.assertEquals("Message", GeciCompatibilityTools.stripLeading(test));
    }

    @Test
    @DisplayName("Can strip any whitespace character from end of string.")
    public void stripTrailing() {
        String test = "Message\n"
            + "\t\n"
            + "\f  ";
        Assertions.assertEquals("Message", GeciCompatibilityTools.stripTrailing(test));
    }

    @Test
    @DisplayName("Can create a List with createList() with the right values.")
    public void list() {
        List<String> list = GeciCompatibilityTools.createList("A", "B", "C");
        Assertions.assertEquals(3, list.size());
        Assertions.assertArrayEquals(new String[]{"A", "B", "C"}, list.stream().sorted().toArray());

    }

    @Test
    @DisplayName("List created by createList() is immutable.")
    public void immutableList() {
        List<Integer> list = GeciCompatibilityTools.createList(1, 2);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> list.remove(1));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> list.add(123));
    }

    @Test
    @DisplayName("Can create a Set with the right values with createSet().")
    public void set() {
        Set<Integer> set = GeciCompatibilityTools.createSet(1, 33);
        Assertions.assertArrayEquals(new Integer[]{1, 33}, set.stream().sorted().toArray());
    }

    @Test
    @DisplayName("Set created with createSet() doesn't have repeat values.")
    public void setNoRepeat() {
        Set<Integer> set = GeciCompatibilityTools.createSet(1, 33, 1, 33, 1);
        Assertions.assertArrayEquals(new Integer[]{1, 33}, set.stream().sorted().toArray());
    }
}
