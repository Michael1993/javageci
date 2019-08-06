package javax0.geci.util;

import javax0.geci.api.Source;
import javax0.geci.tools.GeciCompatibilityTools;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFileCollector {
    @Test
    @DisplayName("Collect the file in this test directory and find the class for it.")
    void collectAllFiles() {
        final Map<Source.Set, javax0.geci.api.DirectoryLocator> sources = GeciCompatibilityTools.createMap(set(),new DirectoryLocator(exists(),new String[]{"src/test/java/javax0/geci/util"}));
        FileCollector collector = new FileCollector(sources);
        collector.collect(null,null, GeciCompatibilityTools.createSet());
        assertEquals(1, collector.getSources().size());
        assertTrue(collector.getSources().iterator().next().getKlassName().endsWith("TestFileCollector"));
    }
}
