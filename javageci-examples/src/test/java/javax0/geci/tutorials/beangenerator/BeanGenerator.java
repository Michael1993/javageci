// snippet BeanGenerator_head_00
package javax0.geci.tutorials.beangenerator;

import javax.xml.parsers.DocumentBuilder;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGeneratorEx;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static javax0.geci.api.Source.Set.set;
import static javax0.geci.tools.CaseTools.ucase;

public class BeanGenerator extends AbstractGeneratorEx {
// end snippet

    // snippet BeanGenerator_main1_01
    @Override
    public void processEx(Source source) throws Exception {
        if (source.getAbsoluteFile().endsWith(".xml")) {
//          ...
// end snippet
// snippet BeanGenerator_main2
            final String newKlass = source.getKlassSimpleName();
            final String pckage = source.getPackageName();
            final Source target = source.newSource(set("java"), newKlass + ".java");
            final Document doc = getDocument(source);
// end snippet
// snippet BeanGenerator_main3
            try (final Segment segment = target.open()) {
                segment.write("package " + pckage + ";");
                segment.write_r("public class " + newKlass + " {");
                NodeList fields = doc.getElementsByTagName("field");
                for (int index = 0; index < fields.getLength(); index++) {
                    Node field = fields.item(index);
                    NamedNodeMap attributes = field.getAttributes();
                    String name = attributes.getNamedItem("name").getNodeValue();
                    String type = attributes.getNamedItem("type").getNodeValue();
                    segment.write("private " + type + " " + name + ";");

                    segment.write_r("public " + type + " get" + ucase(name) + "() {");
                    segment.write("return " + name + ";");
                    segment.write_l("}");

                    segment.write_r("public void set" + ucase(name) + "(" + type + " " + name + ") {");
                    segment.write("this." + name + " = " + name + ";");
                    segment.write_l("}");

                }
                segment.write_l("}");
            }
// end snippet
// snippet BeanGenerator_main1_02
        }
    }
// end snippet

    // snippet BeanGenerator_aux
    private Document getDocument(Source source) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(source.toString())));
    }
    //end snippet
// snippet BeanGenerator_head_01
}
// end snippet
