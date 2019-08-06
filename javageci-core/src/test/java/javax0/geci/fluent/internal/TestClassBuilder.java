package javax0.geci.fluent.internal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax0.geci.api.GeciException;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tools.GeciCompatibilityTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class TestClassBuilder {

    @Test
    public void testIncompleteBuildup() {
        FluentBuilder fluent = FluentBuilder.from(TestClass.class);
        Assertions.assertThrows(GeciException.class, () -> new ClassBuilder((FluentBuilderImpl) fluent).build());
    }

    private static void assertEqualToFile(String result, String resourceName) throws Exception {
        try (OutputStream output = Files.newOutputStream(
                FileSystems.getDefault().getPath("target", resourceName),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            output.write(result.getBytes(StandardCharsets.UTF_8));
        }
        InputStream is = TestClassBuilder.class.getResourceAsStream(resourceName);
        String expected = new String(GeciCompatibilityTools.readBytesFromInput(is), StandardCharsets.UTF_8).replace("\r", "");
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testTerminalsBuildup() throws Exception {
        FluentBuilder fluent = FluentBuilder.from(TestClass.class)
                .one("a")
                .optional("b")
                .oneOf("a", "b", "c", "d");
        String result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTerminalsBuildup.txt");
    }

    @Test
    public void testTerminalsBuildupFullSample() throws Exception {
        FluentBuilder fluent = FluentBuilder.from(TestClass.class)
                .one("a")
                .optional("b")
                .oneOrMore("c")
                .zeroOrMore("d")
                .oneOf("a", "b");
        String result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTerminalsBuildupFullSample.txt");
    }

    @Test
    public void testTreeBuildup() throws Exception {
        FluentBuilder f = FluentBuilder.from(TestClass.class);
        FluentBuilder aOrB = f.oneOf("a", "b");
        FluentBuilder fluent = f.one("a")
                .optional(aOrB)
                .oneOf("a", "b", "c", "d");
        String result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testTreeBuildup.txt");
    }

    @Test
    public void testComplexTreeBuildup() throws Exception {
        FluentBuilder f = FluentBuilder.from(TestClass.class);
        FluentBuilder aOrB = f.oneOf("a", "b");
        FluentBuilder fluent = f.one("a")
                .optional(aOrB)
                .one("c")
                .zeroOrMore(aOrB)
                .oneOf("a", "b", "c", "d");
        String result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testComplexTreeBuildup.txt");
    }

    @Test
    public void testOptionalInOptionalTreeBuildup() throws Exception {
        FluentBuilder f = FluentBuilder.from(TestClass.class);
        FluentBuilder aOrB = f.oneOf(f.optional("a"), f.one("b"));
        FluentBuilder fluent = f.one("a")
                .optional(aOrB)
                .one("c").cloner("copy");
        String result = new ClassBuilder((FluentBuilderImpl) fluent).build();
        assertEqualToFile(result, "testOptionalInOptionalTreeBuildup.txt");
    }

    public static class TestClass {
        public TestClass copy() {
            return this;
        }

        public void a() {
        }

        public void b() {
        }

        public void c() {
        }

        public void d() {
        }

        public void e() {
        }

        public void f() {
        }

    }
}
