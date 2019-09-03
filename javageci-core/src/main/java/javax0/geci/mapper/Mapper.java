package javax0.geci.mapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciCompatibilityTools;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

/**
 * Code generator class that generates toMap and fromMap methods that will convert the object and possibly contained
 * other objects recursively (and also circular object references) into Map and back.
 * <p>
 * The {@code fromMap()} method creates a new object. To do that it uses the factory that can be configured
 * in the Geci annotation of the class, or else it just tries to use the default constructor of the class.
 *
 */
public class Mapper extends AbstractJavaGenerator {

    private static final String DEFAULTS = "!transient & !static";

    public static class Config {
        private String filter = DEFAULTS;
        private Class<? extends Annotation> generatedAnnotation = javax0.geci.annotations.Generated.class;
        private Function<String, String> field2MapKeyMapper = s -> s;
        private String factory;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final String gid = global.get("id");
        Segment segment = source.open(gid);
        final String factory = localConfig(global).factory;
        segment.param(
                "mnemonic", mnemonic(),
                "generatedBy", config.generatedAnnotation.getCanonicalName(),
                "class", klass.getSimpleName(),
                "factory", factory,
                "Map", "java.util.Map",
                "HashMap", "java.util.HashMap"
        );
        generateToMap(segment, source, klass, global);
        generateFromMap(segment, source, klass, global);
    }

    private void generateToMap(Segment segment, Source source, Class<?> klass, CompoundParams global) throws Exception {
        final Field[] fields = GeciReflectionTools.getAllFieldsSorted(klass);
        segment.write_r(getResourceString("tomap.template"));
        for (final Field field : fields) {
            final CompoundParams params = GeciReflectionTools.getParameters(field, mnemonic());
            final Config local = localConfig(new CompoundParams(params, global));
            if (Selector.compile(local.filter).match(field)) {
                final String name = field.getName();
                if (hasToMap(field.getType())) {
                    segment.write("map.put(\"%s\", %s == null ? null : %s.toMap0(cache));", field2MapKey(name), name, name);
                } else {
                    segment.write("map.put(\"%s\",%s);", field2MapKey(name), name);
                }
            }
        }
        segment.write("return map;")
            ._l("}");
    }

    /**
     * Get the resource from the resouce file and remove all \r
     * characters in case it is Windows style.
     *
     * @param resource the name of the resurce in the same JAR directory
     *                 where the class is.
     * @return the content of the resource file
     * @throws IOException if the file was not found
     */
    private String getResourceString(String resource) throws IOException {
        return new String(GeciCompatibilityTools.readBytesFromInput(getClass().getResourceAsStream(resource)), StandardCharsets.UTF_8)
                .replaceAll("\r", "");
    }

    private String field2MapKey(String name) {
        return config.field2MapKeyMapper.apply(name);
    }

    private boolean hasToMap(Class<?> type) {
        try {
            type.getDeclaredMethod("toMap");
            type.getDeclaredMethod("toMap0", Map.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private boolean hasFromMap(Class<?> type) {
        try {
            type.getDeclaredMethod("fromMap", Map.class);
            type.getDeclaredMethod("fromMap0", Map.class, Map.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }

    }

    private void generateFromMap(Segment segment, Source source, Class<?> klass, CompoundParams global) throws IOException {
        final Field[] fields = GeciReflectionTools.getAllFieldsSorted(klass);
        segment.write_r(getResourceString("frommap.template"));
        for (final Field field : fields) {
            final CompoundParams params = GeciReflectionTools.getParameters(field, mnemonic());
            final Config local = localConfig(new CompoundParams(params, global));
            if (Selector.compile(local.filter).match(field)) {
                final String name = field.getName();
                if (hasFromMap(field.getType())) {
                    segment.write("it.%s = %s.fromMap0(({{Map}}<String,Object>)map.get(\"%s\"),cache);",
                            name,
                            field.getType().getCanonicalName(),
                            field2MapKey(name));
                } else {
                    segment.write("it.%s = (%s)map.get(\"%s\");",
                            name,
                            field.getType().getCanonicalName(),
                            field2MapKey(name));
                }
            }
        }
        segment.write("return it;")
            ._l("}");
    }

    //<editor-fold id="configBuilder" configurableMnemonic="mapper">
    private String configuredMnemonic = "mapper";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static Mapper.Builder builder() {
        return new Mapper().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = GeciCompatibilityTools.createSet(
        "factory",
        "filter",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder factory(String factory) {
            config.factory = factory;
            return this;
        }

        public Builder field2MapKeyMapper(java.util.function.Function<String,String> field2MapKeyMapper) {
            config.field2MapKeyMapper = field2MapKeyMapper;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generatedAnnotation(Class<? extends java.lang.annotation.Annotation> generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Mapper build() {
            return Mapper.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final Config local = new Config();
        local.factory = params.get("factory",config.factory);
        local.field2MapKeyMapper = config.field2MapKeyMapper;
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        return local;
    }
    //</editor-fold>
}
