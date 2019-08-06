package javax0.geci.jamal;

import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestJamalGenerator {

    @Test
    public void testJamalGenerator() throws Exception {
        final Geci geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-jamal").testSource())
                        .register(new JamalGenerator())
                        .generate(),
                geci.failed());
    }
}
