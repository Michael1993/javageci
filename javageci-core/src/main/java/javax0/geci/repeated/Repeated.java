package javax0.geci.repeated;

import java.util.Arrays;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.templated.Context;
import javax0.geci.templated.Triplet;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciCompatibilityTools;
import javax0.geci.tools.TemplateLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repeated extends AbstractJavaGenerator {
    private class Config {
        private String start = ".*//\\s*START\\s*";
        private String matchLine = "(.*)";
        private String end = ".*//\\s*END\\s*";
        private String templateStart = "\\s*/\\*\\s*TEMPLATE\\s+(\\w*)\\s*";
        private String templateEnd = "\\s*\\*/\\s*";
        private String values = null;
        private Function<Class, List<String>> valuesSupplier = null;
        private CharSequence selector = "";
        private final CharSequence template = "";
        private Context ctx = new Triplet();
        private final Map<String, String> templatesMap = new HashMap<>();
        private BiFunction<Context, String, String> resolver;
        private final Map<String, BiFunction<Context, String, String>> resolverMap = new HashMap<>();
        private BiConsumer<Context, String> define;
        private final Map<String, BiConsumer<Context, String>> defineMap = new HashMap<>();

        private void setTemplate(CharSequence template) {
            if (templatesMap.containsKey(selector.toString())) {
                throw new GeciException("Selector '" + selector + "' already has a template");
            }
            templatesMap.put(selector.toString(), template.toString());
        }

        private void setResolver(BiFunction<Context, String, String> resolver) {
            if (resolverMap.containsKey(selector)) {
                throw new GeciException("Selector '" + selector + "' already has a resolver");
            }
            resolverMap.put(selector.toString(), resolver);
        }

        private void setDefine(BiConsumer<Context, String> define) {
            if (defineMap.containsKey(selector)) {
                throw new GeciException("Selector '" + selector + "' already has a define");
            }
            defineMap.put(selector.toString(), define);
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final Config local = localConfig(global);
        final Pattern startPattern = Pattern.compile(local.start);
        final Pattern matchLinePattern = Pattern.compile(local.matchLine);
        final Pattern endPattern = Pattern.compile(local.end);
        final Pattern templateStartPattern = Pattern.compile(local.templateStart);
        final Pattern templateEndPattern = Pattern.compile(local.templateEnd);
        boolean switchOn = false;
        boolean templateOn = false;
        final List<String> loopVars = new ArrayList<String>();
        final StringBuilder parsed = new StringBuilder();
        String selector = "";
        int templateTabbing = 0;
        for (final String line : source.getLines()) {
            if (!templateOn && !switchOn) {
                if (startPattern.matcher(line).matches()) {
                    switchOn = true;
                    continue;
                }
                final Matcher templateStartMatcher = templateStartPattern.matcher(line);
                if (templateStartMatcher.matches()) {
                    templateOn = true;
                    selector = templateStartMatcher.group(1);
                    templateTabbing = countSpacesAtStart(line);
                    continue;
                }
            }

            if (switchOn && endPattern.matcher(line).matches()) {
                switchOn = false;
                continue;
            }

            if (!switchOn && templateOn && templateEndPattern.matcher(line).matches()) {
                templateOn = false;
                deleteTrailingNewLine(parsed);
                config.templatesMap.put(selector, TemplateLoader.quote(parsed.toString()));
                parsed.delete(0, parsed.length());
                continue;
            }

            if (templateOn) {
                if (line.length() > templateTabbing) {
                    parsed.append(line.substring(templateTabbing));
                }
                parsed.append("\n");
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
            loopVars.addAll(Arrays.asList(local.values.split(",")));
        }
        if (local.valuesSupplier != null) {
            loopVars.addAll(local.valuesSupplier.apply(klass));
        }
        for (final String key : config.templatesMap.keySet()) {
            final Segment segment;
            if (key.equals("")) {
                segment = source.open(global.id());
            } else {
                segment = source.open(key);
            }
            final String template = config.templatesMap.get(key);
            if (template != null) {
                config.ctx.triplet(source, klass, segment);
                final BiFunction<Context, String, String> resolver = config.resolverMap.get(key);
                final BiConsumer<Context, String> define = config.defineMap.get(key);
                for (final String loopVar : loopVars) {
                    final String templateContent = TemplateLoader.getTemplateContent(template);
                    final String resolvedTemplate = resolver == null ? templateContent : resolver.apply(config.ctx, templateContent);
                    segment.param("value", loopVar);
                    if (define != null) {
                        define.accept(config.ctx, loopVar);
                    }
                    segment.write(resolvedTemplate);
                }
            }
        }
    }

    private void deleteTrailingNewLine(StringBuilder parsed) {
        if (parsed.length() > 0 && parsed.charAt(parsed.length() - 1) == '\n') {
            parsed.deleteCharAt(parsed.length() - 1);
            if (parsed.length() > 0 && parsed.charAt(parsed.length() - 1) == '\r') {
                parsed.deleteCharAt(parsed.length() - 1);
            }
        }
    }

    private static int countSpacesAtStart(String line) {
        int i = 0;
        while (i < line.length()) {
            if (line.charAt(i) != ' ') {
                return i;
            }
            i++;
        }
        return 0;
    }

    //<editor-fold id="configBuilder" configurableMnemonic="repeated">
    private String configuredMnemonic = "repeated";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static Repeated.Builder builder() {
        return new Repeated().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = GeciCompatibilityTools.createSet(
        "end",
        "matchLine",
        "start",
        "templateEnd",
        "templateStart",
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

        public Builder selector(CharSequence selector) {
            config.selector = selector;
            return this;
        }

        public Builder start(String start) {
            config.start = start;
            return this;
        }

        public Builder template(CharSequence template) {
            config.setTemplate(template);
            return this;
        }

        public Builder templateEnd(String templateEnd) {
            config.templateEnd = templateEnd;
            return this;
        }

        public Builder templateStart(String templateStart) {
            config.templateStart = templateStart;
            return this;
        }

        public Builder values(String values) {
            config.values = values;
            return this;
        }

        public Builder valuesSupplier(java.util.function.Function<Class,java.util.List<String>> valuesSupplier) {
            config.valuesSupplier = valuesSupplier;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Repeated build() {
            return Repeated.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final Config local = new Config();
        local.ctx = config.ctx;
        local.setDefine(config.define);
        local.end = params.get("end",config.end);
        local.matchLine = params.get("matchLine",config.matchLine);
        local.setResolver(config.resolver);
        local.selector = config.selector;
        local.start = params.get("start",config.start);
        local.setTemplate(config.template);
        local.templateEnd = params.get("templateEnd",config.templateEnd);
        local.templateStart = params.get("templateStart",config.templateStart);
        local.values = params.get("values",config.values);
        local.valuesSupplier = config.valuesSupplier;
        return local;
    }
    //</editor-fold>
}
