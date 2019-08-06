package javax0.geci.jamal.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import static javax0.geci.jamal.util.EntityStringer.isFingerPrintAField;

/**
 * Macro that evaluates to the space separated list of modifiers of the method or field. The method or field is
 * specified by the fingerprint in the macro argument.
 */
public class Modifiers implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final String fingerPrint = in.toString().trim();
        final int modifiers;
        if (isFingerPrintAField(fingerPrint)) {
            Field field = EntityStringer.fingerprint2Field(fingerPrint);
            modifiers = field.getModifiers();
        } else {
            Method method = EntityStringer.fingerprint2Method(fingerPrint);
            modifiers = method.getModifiers();
        }
        return GeciReflectionTools.unmask(modifiers);
    }
}
