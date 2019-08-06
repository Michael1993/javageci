package javax0.geci.tools;

import static javax0.geci.tools.GeciCompatibilityTools.createMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TestTemplate {

    @Test
    @DisplayName("When there are no params every string just returns as is")
    void emptyTest(){
        final Template sut = new Template(new HashMap<>());
        final List<String> samples = Arrays.asList("alma", "{{kirte}}", "just {a{{nithing");
        for( final String sample : samples) {
            Assertions.assertEquals(sample, sut.resolve(sample));
        }
    }

    @Test
    @DisplayName("When there are params they are replaced")
    void goodTest(){
        final Template sut = new Template(createMap("a","b", "huhh","spooky"));
            Assertions.assertEquals("this is a spooky baboon", sut.resolve("this is a {{huhh}} {{a}}a{{a}}oon"));
    }

    @Test
    @DisplayName("Parameters replaces also at the start")
    void startTest(){
        final Template sut = new Template(createMap("a","b", "huhh","spooky"));
        Assertions.assertEquals("bthis is {{a...}} spooky bab{{oon", sut.resolve("{{a}}this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"));
    }

    @Test
    @DisplayName("When there are params they are replaced but not the undefined")
    void goodTestStill(){
        final Template sut = new Template(createMap("a","b", "huhh","spooky"));
        Assertions.assertEquals("this is {{a...}} spooky baboon", sut.resolve("this is {{a...}} {{huhh}} {{a}}a{{a}}oon"));
    }

    @Test
    @DisplayName("Unterminated placeholders are handled gracefully")
    void unterminatedTest(){
        final Template sut = new Template(createMap("a","b", "huhh","spooky"));
        Assertions.assertEquals("this is {{a...}} spooky bab{{oon", sut.resolve("this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"));
    }
}
