// DO NOT EDIT THIS FILE. THIS WAS GENERATED.
package javax0.geci.tools;


import javax0.geci.api.Source;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * This abstract generator does the same as {@link AbstractMethodsGenerator} with the additional functionality
 * that it looks at the {@code Geci} annotation parameter named {@code filter}, which supposed to contain a
 * {@link Selector} expression and invokes the {@link #process(Source, Class, CompoundParams, Method)} method only
 * for the methods, which match the filter criterion.
 * <p>
 * Note that the filter criteria is taken from the {@code Geci} annotation from the class level but also form the
 * method level. Since the method level configuration overwrites the class level configuration and since it is controlling
 * the filtering of the single method, which is annotation the only reasonable selector expression on a method
 * {@code Geci} annotation is either {@code true} and {@code false}.
 */

public abstract class AbstractFilteredMethodsGenerator extends AbstractMethodsGenerator {
    private final List<Method> methods = new ArrayList<>();

    @Override
    protected final void processMethodHook(Source source, Class<?> klass, CompoundParams params, Method method)
        throws Exception {
        String filter = params.get("filter", defaultFilterExpression());
        Selector selector = Selector.compile(filter);
        if (selector.match(method)) {
            processSelectedMethodHook(source, klass, params, method);
            methods.add(method);
        }
    }

    /**
     * This implementation clears the private {@code methods} method ArrayList and then passes the control to the
     * method {@link #preprocess(Source, Class, CompoundParams)}. This way the original functionality is kept and
     * the method {@link #processSelectedMethodHook(Source, Class, CompoundParams, Method)} can collect the filtered
     * methods after a fresh start into the object variable {@code methods} even if the generator object was called to
     * generate some code for a different class beforehand.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the {@code Geci} annotation on the class.
     * @throws Exception any exception that the is thrown by the generator
     */
    @Override
    protected final void preprocessHook(Source source, Class<?> klass, CompoundParams global) throws Exception {
        methods.clear();
        preprocess(source, klass, global);
    }

    @Override
    protected final void processMethodHook(Source source, Class<?> klass, CompoundParams global, Method[] methods)
        throws Exception {
        processSelectedMethodHook(source, klass, global, this.methods.toArray(new Method[0]));
    }

    /**
     * Implementations should override this method if they need a different default filter expression for the methods.
     *
     * @return the filter expression that is used when none is specified in the configuration in the class needing
     * the generated code.
     */
    protected String defaultFilterExpression() {
        return "true";
    }

    /**
     * Extending this interface can override this method adding extra functionality and keeping the signature and the
     * name of the abstract method {@link AbstractMethodsGenerator#process(Source, Class, CompoundParams, Method)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the class and also from the actual method.
     * @param methods the method that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator
     */
    protected void processSelectedMethodHook(Source source, Class<?> klass, CompoundParams global, Method[] methods)
        throws Exception {
        process(source, klass, global, methods);
    }

    /**
     * Extending this interface can override this method adding extra functionality and keeping the signature and the
     * name of the abstract method {@link AbstractMethodsGenerator#process(Source, Class, CompoundParams, Method)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param params the parameters collected from the class and also from the actual method. The parameters defined on
     *               the method annotation have precedence over the annotations on the class.
     * @param method  the method that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator     *
     */
    protected void processSelectedMethodHook(Source source, Class<?> klass, CompoundParams params, Method method)
        throws Exception {
        process(source, klass, params, method);
    }
}
