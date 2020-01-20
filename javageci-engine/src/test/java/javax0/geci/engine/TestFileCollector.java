package javax0.geci.engine;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax0.geci.api.Source;
import javax0.geci.tools.JVM8Tools;
import javax0.geci.util.DirectoryLocator;

public class TestFileCollector {
    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final Map<Source.Set, javax0.geci.api.DirectoryLocator> sources = JVM8Tools.asMap(set(),new DirectoryLocator(exists(),new String[]{"src/test/java/javax0/geci/engine"}));
        var collector = new FileCollector(sources);
        collector.collect(null,null, Collections.emptySet());
        assertEquals(4, collector.getSources().size());
        assertTrue(collector.getSources().stream().anyMatch(source -> source.getKlassName().endsWith("TestFileCollector")),
            "TestFileCollector was not found by the collector");
    }
}
