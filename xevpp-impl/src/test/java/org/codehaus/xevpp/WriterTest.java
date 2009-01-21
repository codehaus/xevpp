package org.codehaus.xevpp;

import org.junit.Test;
import static org.junit.Assert.*;
import org.codehaus.xevpp.XMLEventFactoryImpl;
import org.codehaus.xevpp.XMLOutputFactoryImpl;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 09-Aug-2008 00:50:49
 */
public class WriterTest {

    @Test
    public void basic() throws Exception {
        assertTrue(true);
        XMLOutputFactory of = XMLOutputFactoryImpl.newInstance();
        XMLEventFactory ef = XMLEventFactoryImpl.newInstance();

        StringWriter buf = new StringWriter();

        XMLEventWriter writer = of.createXMLEventWriter(buf);

        writer.add(ef.createStartDocument());
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createComment("a test document"));
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createStartElement("", "", "root"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createStartElement("", "", "child"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndElement("", "", "child"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndElement("", "", "root"));
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createEndDocument());

        writer.close();

        System.out.println(buf);
    }

    @Test
    public void basic2() throws Exception {
        assertTrue(true);
        XMLOutputFactory of = XMLOutputFactoryImpl.newInstance();
        XMLEventFactory ef = XMLEventFactoryImpl.newInstance();

        StringWriter buf = new StringWriter();

        XMLEventWriter writer = of.createXMLEventWriter(buf);

        writer.add(ef.createStartDocument());
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createComment("a test document"));
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createStartElement("", "", "root"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createStartElement("", "", "child"));
        writer.add(ef.createEndElement("", "", "child"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndElement("", "", "root"));
        writer.add(ef.createIgnorableSpace("\n"));
        writer.add(ef.createEndDocument());

        writer.close();

        System.out.println(buf);
    }

    @Test
    public void aaronTest() throws Exception {
        XMLOutputFactory of = XMLOutputFactoryImpl.newInstance();
        XMLEventFactory ef = XMLEventFactoryImpl.newInstance();

        StringWriter doc = new StringWriter();

        XMLEventWriter writer = of.createXMLEventWriter(doc);

        writer.add(ef.createStartDocument("utf-8", "1.0"));
        writer.add(ef.createIgnorableSpace("\n\n"));
        writer.add(ef.createStartElement("", "", "root"));
        writer.add(ef.createSpace("\n    "));
        writer.add(ef.createCharacters("Hello World!"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndElement("", "", "root"));
        writer.add(ef.createEndDocument());

        writer.close();

        assertEquals("<?xml version='1.0' encoding='utf-8'?>\n" +
                "\n" +
                "<root>\n" +
                "    Hello World!\n" +
                "</root>", doc.toString());
    }

    @Test
    public void testFormattingAttributes() throws Exception {
        XMLOutputFactory of = XMLOutputFactoryImpl.newInstance();
        XMLEventFactory ef = XMLEventFactoryImpl.newInstance();

        StringWriter doc = new StringWriter();

        XMLEventWriter writer = of.createXMLEventWriter(doc);

        writer.add(ef.createStartDocument("utf-8"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createStartElement("", "http://maven.apache.org/POM/4.0.0", "project"));
        writer.add(ef.createNamespace("http://maven.apache.org/POM/4.0.0"));
        writer.add(ef.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        //writer.add(ef.createIgnorableSpace("\n        "));
        writer.add(ef.createAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndElement("", "http://maven.apache.org/POM/4.0.0", "project"));
        writer.add(ef.createSpace("\n"));
        writer.add(ef.createEndDocument());
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "</project>\n" +
                        "",
                doc.toString());
    }
}
