package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("equals")
@Retention(RetentionPolicy.RUNTIME)
public @interface Equals {

    String value() default "";
    String filter() default "";
    String hashFilter() default "";
    String notNull() default "";
    String subclass() default "";
    String useObjects() default "";
    String useSuper() default "";
}
