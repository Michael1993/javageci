package javax0.geci.fluent.internal;

import javax0.geci.tools.MethodTool;
import javax0.geci.tools.Tools;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class FluentMethodTool extends MethodTool {

    final protected String klassName;

    public static FluentMethodTool from(Class klass) {
        return new FluentMethodTool(klass);
    }

    private FluentMethodTool(Class klass) {
        super();
        this.klassName = Tools.normalizeTypeName(klass.getName());
    }

    @Override
    public String getArgCall(Type t) {
        final var normType = Tools.normalizeTypeName(t.getTypeName());
        final String arg;
        if (normType.equals(klassName)) {
            arg = "((Wrapper)arg" + argCounter.addAndGet(1) + ").that";
        } else {
            arg = "arg" + argCounter.addAndGet(1);
        }
        return arg;
    }

    @Override
    public String getArg(Type t) {
        final var normType = Tools.normalizeTypeName(t.getTypeName());
        final String actualType;
        if (normType.equals(klassName)) {
            actualType = "WrapperInterface";
        } else {
            actualType = normType;
        }
        return actualType + " arg" + argCounter.addAndGet(1);
    }
}