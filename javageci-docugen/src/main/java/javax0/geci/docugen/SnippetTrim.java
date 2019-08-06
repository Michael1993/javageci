package javax0.geci.docugen;

import java.util.List;
import javax0.geci.annotations.Geci;
import javax0.geci.api.CompoundParams;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;

import java.util.ArrayList;
import javax0.geci.tools.GeciCompatibilityTools;

@Geci("configBuilder localConfigMethod='' configurableMnemonic='trim'")
public class SnippetTrim extends AbstractSnippeter {

    private static class Config extends AbstractSnippeter.Config {
        // snippet SnippetTrim_config_001
        private String to = "0";
        /*

        This parameter can define the number of spaces on the left of
        the lines. Although the parameter is a string the value should
        obviously, be an integer number as it is recommended to specify
        it without `"` or `'` characters surrounding, just simply, for
        example

                       trim="to=2"


        end snippet */
    }

    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final int to;
        try {
            to = Integer.parseInt(params.get("to", config.to));
        } catch (NumberFormatException nfe) {
            throw new GeciException("Can not interpret 'to' parameter " + params.get("to", config.to) + " as a number in snippet " + snippet.name() + " in source " + source.getAbsoluteFile());
        }
        final int untab = calculateTabbing(snippet);

        final List<String> modifiedLines = new ArrayList<String>();
        for (final String line : snippet.lines()) {
            modifiedLines.add(GeciCompatibilityTools.repeat(" ", to) + (line.length() >= untab ? line.substring(untab) : ""));
        }
        snippet.lines().clear();
        snippet.lines().addAll(modifiedLines);
    }

    private int calculateTabbing(Snippet snippet) {
        int min = Integer.MAX_VALUE;
        for (final String line : snippet.lines()) {
            final int stripped = GeciCompatibilityTools.stripLeading(line).length();
            if (stripped > 0) {
                final int spaces = line.length() - stripped;
                if (spaces < min) {
                    min = spaces;
                }
            }
        }
        return min;
    }

    //<editor-fold id="configBuilder">
    private String configuredMnemonic = "trim";

    @Override
    public String mnemonic() {
        return configuredMnemonic;
    }

    private final Config config = new Config();

    public static SnippetTrim.Builder builder() {
        return new SnippetTrim().new Builder();
    }

    public class Builder extends javax0.geci.docugen.AbstractSnippeter.Builder {
        public Builder to(String to) {
            config.to = to;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public SnippetTrim build() {
            return SnippetTrim.this;
        }
    }
//</editor-fold>
}
