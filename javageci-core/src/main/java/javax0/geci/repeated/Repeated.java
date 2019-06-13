package javax0.geci.repeated;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.templated.Context;
import javax0.geci.templated.Triplet;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.TemplateLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repeated extends AbstractJavaGenerator {
    private class Config {
        private String start = ".*//\\s*START\\s*";
        private String matchLine = "(.*)";
        private String end = ".*//\\s*END\\s*";
        private String values = null;
        private String selector = "";
        private String template = null;
        private Context ctx = new Triplet();
        private final Map<String, String> templatesMap = new HashMap<>();
        private BiFunction<Context, String, String> resolver;
        private final Map<String, BiFunction<Context, String, String>> resolverMap = new HashMap<>();
        private BiConsumer<Context, String> define;
        private final Map<String, BiConsumer<Context, String>> defineMap = new HashMap<>();

        private void setTemplate(String template) {
            if (templatesMap.containsKey(selector)) {
                throw new GeciException("Selector '" + selector + "' already has a template");
            }
            templatesMap.put(selector, template);
        }

        private void setResolver(BiFunction<Context, String, String> resolver) {
            if (resolverMap.containsKey(selector)) {
                throw new GeciException("Selector '" + selector + "' already has a resolver");
            }
            resolverMap.put(selector, resolver);
        }

        private void setDefine(BiConsumer<Context, String> define) {
            if (defineMap.containsKey(selector)) {
                throw new GeciException("Selector '" + selector + "' already has a define");
            }
            defineMap.put(selector, define);
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var local = localConfig(global);
        final var startPattern = Pattern.compile(local.start);
        final var matchLinePattern = Pattern.compile(local.matchLine);
        final var endPattern = Pattern.compile(local.end);
        boolean switchOn = false;
        final var loopVars = new ArrayList<String>();
        for (final var line : source.getLines()) {
            if (startPattern.matcher(line).matches()) {
                switchOn = true;
                continue;
            }
            if (endPattern.matcher(line).matches()) {
                switchOn = false;
                continue;
            }
            if (switchOn) {
                final Matcher matcher = matchLinePattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.groupCount() != 1) {
                        throw new GeciException("matchLine does not contain any group between ( and )");
                    }
                    loopVars.add(matcher.group(1));
                }
            }
        }
        if (local.values != null) {
            loopVars.addAll(List.of(local.values.split(",")));
        }
        for (final var key : config.templatesMap.keySet()) {
            final Segment segment;
            if (key.equals("")) {
                segment = source.open(global.id());
            } else {
                segment = source.open(key);
            }
            final var template = config.templatesMap.get(key);
            if (template != null) {
                config.ctx.triplet(source, klass, segment);
                final var resolver = config.resolverMap.get(key);
                final var define = config.defineMap.get(key);
                for (final var loopVar : loopVars) {
                    final var templateContent = TemplateLoader.getTemplateContent(template);
                    final var resolvedTemplate = resolver == null ? templateContent : resolver.apply(config.ctx, templateContent);
                    segment.param("value",loopVar);
                    if (define != null) {
                        define.accept(config.ctx, loopVar);
                    }
                    segment.write(resolvedTemplate);
                }
            }
        }
    }


    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static Repeated.Builder builder() {
        return new Repeated().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "end",
        "matchLine",
        "selector",
        "start",
        "template",
        "values",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder ctx(javax0.geci.templated.Context ctx) {
            config.ctx = ctx;
            return this;
        }

        public Builder define(java.util.function.BiConsumer<javax0.geci.templated.Context,String> define) {
            config.setDefine(define);
            return this;
        }

        public Builder end(String end) {
            config.end = end;
            return this;
        }

        public Builder matchLine(String matchLine) {
            config.matchLine = matchLine;
            return this;
        }

        public Builder resolver(java.util.function.BiFunction<javax0.geci.templated.Context,String,String> resolver) {
            config.setResolver(resolver);
            return this;
        }

        public Builder selector(String selector) {
            config.selector = selector;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder template(String template) {
            config.setTemplate(template);
            return this;
        }

        public Builder values(String values) {
            config.values = values;
            return this;
        }

        public Repeated build() {
            return Repeated.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.ctx = config.ctx;
        local.setDefine(config.define);
        local.end = params.get("end",config.end);
        local.matchLine = params.get("matchLine",config.matchLine);
        local.setResolver(config.resolver);
        local.selector = params.get("selector",config.selector);
        local.start = params.get("start",config.start);
        local.setTemplate(params.get("template",config.template));
        local.values = params.get("values",config.values);
        return local;
    }
    //</editor-fold>
}
