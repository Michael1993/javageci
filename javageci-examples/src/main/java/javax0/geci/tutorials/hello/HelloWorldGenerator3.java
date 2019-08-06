package javax0.geci.tutorials.hello;

import java.lang.reflect.Method;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;

import java.io.IOException;

public class HelloWorldGenerator3 extends AbstractJavaGenerator {
    public void process(Source source, Class<?> klass, CompoundParams global)
            throws IOException {
        final Segment segment = source.open(global.get("id"));
        final String methodName = global.get("methodName", "hello");
        segment.write_r("public static void %s(){", methodName);
        segment.write("System.out.println(\"Hello, World\");");
        segment.write_l("}");
    }

    public String mnemonic() {
        return "HelloWorld3";
    }
}
