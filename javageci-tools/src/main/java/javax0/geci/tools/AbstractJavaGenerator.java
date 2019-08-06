package javax0.geci.tools;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Generators that work on Java source files that already have their
 * compiled class files during testing time are encouraged to extend
 * this class instead of {@link AbstractGeneratorEx} or instead of
 * implementing the interface {@link javax0.geci.api.Generator}.
 *
 * <p> Concrete classes have to override te method {@link
 * #process(Source, Class, CompoundParams)}.
 *
 * <p> When the method is called the parameters are collected from the
 * class or in case the class is not annotated then from the comments in
 * front of the class declaration. The requirement is that the
 * configuration line is commented out using {@code //} in front of the
 * configuration "annotation" and the next line is the start of the
 * class. Since this is read from the source file the code checks that
 * the next line matches the pattern {@link #CLASS_LINE}. The pattern is
 * created to check that the {@code class} keyword is on the line
 * followed by some space, then the name of the class and that the end
 * of the line is the opening brace of the class on the same line.
 *
 * <p> If neither the annotation nor the annotation syntax comment is
 * present on the source code there is still a last resort to recognize
 * the file as one to be handled by the generator. The code finds the
 * segments that have the {@code id} parameter value of the generator
 * mnemonic and uses all the parameters defined on that line to
 * configure the generator.
 *
 * <pre>{@code
 *   // <editor-fold if="mnemonic" key="foo">
 * }</pre>
 * <p>
 * will be processed and the {@code key} will be associated with the
 * {@code foo}.
 *
 * <p> The parameters on the segment starting line is used as additional
 * and secondary values even if the annotation or the comment
 * configuration exists. Note, however, that the annotation and the
 * commented annotations are found first when querying a parameter. Thus
 * if there is a
 *
 * <pre>
 * {@code @}{@code Geci("mnemonic key='bar'")
 * class myClass {
 *  ...
 *
 *   // <editor-fold if="mnemonic" key="foo">
 *
 *       ...
 *  }
 * }</pre>
 *
 * <p> annotation in the code on the class then the value {@code foo}
 * will be hidden by the value {@code bar} and when the generator code
 * queries the {@code global} (3rd parameter) of the method {@link
 * #process(Source, Class, CompoundParams)} with the key {@code key} the
 * value will be {@code bar}.
 */
public abstract class AbstractJavaGenerator extends AbstractGeneratorEx {
    private static final Pattern CLASS_LINE = Pattern.compile("class\\s+[a-zA-Z_][\\w$]*\\s*.*\\{\\s*$");

    protected int phase = 0;

    protected void writeGenerated(Segment segment, Class<? extends Annotation> annotation) {
        if (annotation != null) {
            segment.write("@" + annotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        }
    }

    protected final List<Class<?>> classes = new ArrayList<>();

    /**
     * Chlid classes can override this method to return {@code true} in case they want to process a source class and
     * source code even if the class is not annotated, there is no annotation before the class line in a comment and
     * there is no editor-fold segment with the mnemonic of the generator.
     *
     * <p>In situations like that the generator may scan the class and look at the methods, fields and other members to
     * decide if it wants to work and generate some code. When there is no editor-fold segment, Geci will insert it
     * before closing brace of the class using regular expression pattern matching. That way the generator only needs
     * perhaps a simple annotation on a field and nothing else to be typed by the programmer. This makes the usage of
     * the generator extremely simple.
     *
     * <p> The concrete generators are supposed to define a configuration parameter with {@code boolean} value (so that
     * it can only be used in the builder) with the name {@code processAllClasses} so that the user can decide if they
     * want to use this feature. Note that scanning through all the classes via reflection may need significant time and
     * in case of large projects this may increase the build time. Considering that implementing this method
     * non-configurable and returning a {@code true} value all the time seems to be a non-optimal choice.
     *
     * @return {@code false} as a default.
     */
    protected boolean processAllClasses() {
        return false;
    }

    public final void processEx(Source source) throws Exception {
        final Class<?> klass = source.getKlass();
        if (klass != null) {
            if (phase == 0) {
                classes.add(klass);
            }
            CompoundParams annotationParams = Optional.ofNullable(GeciReflectionTools.getParameters(klass, mnemonic())).orElseGet(() ->
                GeciAnnotationTools.getParameters(source, mnemonic(), "//", CLASS_LINE));
            CompoundParams editorFoldParams = GeciAnnotationTools.getSegmentParameters(source, mnemonic());
            CompoundParams global = new CompoundParams(annotationParams, editorFoldParams);
            global.setConstraints(source, mnemonic(), implementedKeys());
            if (annotationParams != null || processAllClasses() ) {
                source.allowDefaultSegment();
            }
            if (annotationParams != null || editorFoldParams != null || processAllClasses()) {
                process(source, klass, global);
            }
        }
    }

    /**
     * Concrete classes can return an immutable set of the keys that the
     * generator processes. The method {@code processEx()} checks the
     * actual configuration keys against this set and in case there is
     * any configuration key that is not used by the generator then it
     * throws {@code GeciException}.
     *
     * @return {@code null}. Concrete implementations should return the
     * set of the keys that are accepted by the generator.
     */
    protected Set<String> implementedKeys() {
        return null;
    }

    /**
     * Concrete classes extending this abstract class {@link AbstractJavaGenerator} should implement this method.
     *
     * @param source is the source object that the generator will work from.
     * @param klass  the klass that was created from the source during the compilation
     * @param global contains the parameters collected from the {@link javax0.geci.annotations.Geci} annotation
     *               on the class or from the annotation like comment in front of the class start in the source.
     * @throws Exception any exception thrown by the generator
     */
    public abstract void process(Source source, Class<?> klass, CompoundParams global) throws Exception;

    private final String calculatedMnemonic = CaseTools.lcase(this.getClass().getSimpleName());

    public String mnemonic() {
        return calculatedMnemonic;
    }
}
