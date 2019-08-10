package javax0.geci.tools;

import static javax0.geci.tools.GeciCompatibilityTools.createMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestCompoundParams {

    @Test
    @DisplayName("It proxies a normal map")
    void testFromMap() {
        final CompoundParams sut = new CompoundParams("theId", createMap("a", "1", "b", "2"));
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from a normal map")
    void testKeySetFromMap() {
        final CompoundParams sut = new CompoundParams("theId", createMap("a", "1", "b", "2"));
        Assertions.assertEquals("a,b",
                String.join(",", sut.keySet()));
    }

    @Test
    @DisplayName("It proxies multiple maps and earlier hides the later")
    void testFromMapMultiple() {
        final CompoundParams sut = new CompoundParams("theId", createMap("a", "1", "b", "2"), createMap("a", "4", "b", "4", "c", "5"));
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple maps")
    void testKeySetFromMapMultiple() {
        final CompoundParams sut = new CompoundParams("theId", createMap("a", "1", "b", "2"), createMap("a", "4", "b", "4", "c", "5"));
        Assertions.assertEquals("a,b,c",
                String.join(",", sut.keySet()));
    }

    @Test
    @DisplayName("It proxies multiple CompoundParameters and earlier hides the later")
    void testFromMapMultipleCompound() {
        final CompoundParams sut = new CompoundParams(
                new CompoundParams("theId",
                        createMap("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        createMap("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple CompoundParameters")
    void testKeySetFromMapMultipleCompound() {
        final CompoundParams sut = new CompoundParams(
                new CompoundParams("theId",
                        createMap("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        createMap("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("a,b,c",
                String.join(",", sut.keySet()));
    }

    @Test
    @DisplayName("It proxies multiple CompoundParameters with possible null Co..Pa..ms")
    void testFromMapMultipleCompoundWithNull() {
        final CompoundParams sut = new CompoundParams(
                null,
                new CompoundParams("theId",
                        createMap("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        createMap("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple CompoundParameters some null")
    void testKeySetFromMapMultipleCompoundWithNull() {
        final CompoundParams sut = new CompoundParams(
            new CompoundParams("theId",
                createMap("a", "1", "b", "2")),
            new CompoundParams("otherID-no one cares",
                createMap("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("a,b,c",
            String.join(",", sut.keySet()));
    }
}
