package org.codehaus.xevpp;

import org.junit.Test;
import static org.junit.Assert.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 21:27:34
 */
public class SmokeTest {

    @Test
    public void testXmlError() throws Exception {
        InputStream stream = new ByteArrayInputStream(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><document>&lt;text&gt;</document>"
                        .getBytes());
        XMLInputFactory factory = XMLInputFactory.newInstance("org.codehaus.xevpp.XMLInputFactoryImpl", Thread.currentThread().getContextClassLoader());
        XMLEventReader reader = factory.createXMLEventReader(stream);
        StartDocument startdoc = (StartDocument) reader.nextEvent();
        assertEquals("UTF-8", startdoc.getCharacterEncodingScheme());
        assertEquals("1.0", startdoc.getVersion());
        assertTrue(reader.hasNext());
        XMLEvent event = reader.nextEvent();
        assertTrue(event.isStartElement());
        event = reader.nextEvent();
        assertTrue(event.isCharacters());
        String c = event.asCharacters().getData();

        event = reader.nextEvent();
        assertTrue(event.isCharacters());
        c += event.asCharacters().getData();

        assertEquals("<text>", c); //FAILURE expected "<text>" but was "<text"
        event = reader.nextEvent();
        assertTrue(event.isEndElement());
    }

    @Test
    public void testXmlError2() throws Exception {
        InputStream stream = new ByteArrayInputStream(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><document>&lt;text&gt; </document>"
                .getBytes());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(stream);
        StartDocument startdoc = (StartDocument) reader.nextEvent();
        assertEquals("UTF-8", startdoc.getCharacterEncodingScheme());
        assertEquals("1.0", startdoc.getVersion());
        assertTrue(reader.hasNext());
        XMLEvent event = reader.nextEvent();
        assertTrue(event.isStartElement());
        event = reader.nextEvent();
        assertTrue(event.isCharacters());
        String c = event.asCharacters().getData();
        event = reader.nextEvent();
        assertTrue(event.isCharacters());
        c+=event.asCharacters().getData();
        event = reader.nextEvent();
        assertTrue(event.isEndElement());

        assertEquals("<text> ",c); //SUCCES
    }

}
