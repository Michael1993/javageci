package javax0.geci.accessor;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Function;

import static javax0.geci.tools.CaseTools.ucase;

public class AbstractAccessor extends AbstractFilteredFieldsGenerator {

    protected static class Config {
        protected String access = "public";
        protected String filter = "true";
        protected String getter = null;
        protected Function<String,String> getterNameGenerator = (name) -> getterName(name);
        protected Function<String,String> setterNameGenerator = (name) -> setterName(name);
        protected Function<String,String> getterReturnValueDecorator = (name) -> name;
    }

    private static final Set<String> accessModifiers =
            Set.of("public", "private", "protected", "package");

    protected void writeGetter(Field field, String name, String getterName,
                               String type, String access, Segment segment) {
        segment._r("%s %s %s(){", access, type, getterName)
                .write("return %s;", config.getterReturnValueDecorator.apply(name))
                ._l("}")
                .newline();
    }

    protected void writeSetter(Field field, String name, String setterName,
                               String type, String access, Segment segment) {
    }

    protected static String getterName(String name) {
        return "get" + ucase(name);
    }

    protected static String setterName(String name) {
        return "set" + ucase(name);
    }

    private String check(final String access) {
        if (!access.endsWith("!") && !accessModifiers.contains(access)) {
            throw new GeciException("'" + access + "' is not a valid access modifier");
        }
        final String modifiedAccess;
        if (access.endsWith("!")) {
            modifiedAccess = access.substring(0, access.length() - 1);
        } else {
            modifiedAccess = access;
        }
        if (modifiedAccess.equals("package")) {
            return "";
        }
        return modifiedAccess;
    }


    @Override
    public void process(Source source, Class<?> klass,
                        CompoundParams params,
                        Field field,
                        Segment segment) {
        final var isFinal = Modifier.isFinal(field.getModifiers());
        final var name = field.getName();
        final var fieldType = GeciReflectionTools.typeAsString(field);
        final var access = check(params.get("access", "public"));
        final var setter = params.get("setter", () -> config.setterNameGenerator.apply(name));
        final var getter = params.get("getter", () -> config.getterNameGenerator.apply(name));
        final var only = params.get("only");
        if (!isFinal && !"getter".equals(only)) {
            writeSetter(field, name, setter, fieldType, access, segment);
        }
        if (!"setter".equals(only)) {
            writeGetter(field, name, getter, fieldType, access, segment);
        }
    }

    //<editor-fold id="configBuilder" filter="true" configAccess="protected">
    protected final Config config = new Config();
    public static AbstractAccessor.Builder builder() {
        return new AbstractAccessor().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "access",
        "filter",
        "getter",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder access(String access) {
            config.access = access;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder getter(String getter) {
            config.getter = getter;
            return this;
        }

        public Builder getterNameGenerator(java.util.function.Function getterNameGenerator) {
            config.getterNameGenerator = getterNameGenerator;
            return this;
        }

        public Builder getterReturnValueDecorator(java.util.function.Function getterReturnValueDecorator) {
            config.getterReturnValueDecorator = getterReturnValueDecorator;
            return this;
        }

        public Builder setterNameGenerator(java.util.function.Function setterNameGenerator) {
            config.setterNameGenerator = setterNameGenerator;
            return this;
        }

        public AbstractAccessor build() {
            return AbstractAccessor.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.access = params.get("access",config.access);
        local.filter = params.get("filter",config.filter);
        local.getter = params.get("getter",config.getter);
        local.getterNameGenerator = config.getterNameGenerator;
        local.getterReturnValueDecorator = config.getterReturnValueDecorator;
        local.setterNameGenerator = config.setterNameGenerator;
        return local;
    }
    //</editor-fold>

}
