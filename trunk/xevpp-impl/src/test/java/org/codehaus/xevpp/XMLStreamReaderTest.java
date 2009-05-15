package org.codehaus.xevpp;

import com.ctc.wstx.stax.WstxInputFactory;
import com.sun.xml.internal.stream.*;
import junit.framework.TestCase;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 05-Feb-2009
 * Time: 08:05:44
 * To change this template use File | Settings | File Templates.
 */
public class XMLStreamReaderTest extends TestCase {
    private static InputStream getInputStream(String resourceName) {
        return XMLStreamReaderTest.class.getResourceAsStream(resourceName);
    }

    public static interface InputStreamFactory {
        InputStream newInstance();
    }
    
    public void testSimpleWoodstox() throws Exception {
        XMLInputFactory xmlInputFactory = new WstxInputFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        doTest(new InputStreamFactory(){
            public InputStream newInstance() {
                return getInputStream("/test-data/simple.xml");
            }
        }, xmlInputFactory);
    }
    
    public void testSimpleSun() throws Exception {
        XMLInputFactory xmlInputFactory = new com.sun.xml.internal.stream.XMLInputFactoryImpl();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        doTest(new InputStreamFactory(){
            public InputStream newInstance() {
                return getInputStream("/test-data/simple.xml");
            }
        }, xmlInputFactory);
    }
    
    public void testComplexWoodstox() throws Exception {
        XMLInputFactory xmlInputFactory = new WstxInputFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        doTest(new InputStreamFactory(){
            public InputStream newInstance() {
                return getInputStream("/test-data/complex.xml");
            }
        }, xmlInputFactory);
    }
    
    public void doTest(InputStreamFactory inputStreamFactory, XMLInputFactory xmlInputFactory) throws Exception {
        XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(inputStreamFactory.newInstance());
        try {
            XMLStreamReader our = new XMLStreamReaderImpl(xmlInputFactory.createXMLEventReader(inputStreamFactory.newInstance()));
            try {
                boolean finished = false;
                int expected = XMLStreamConstants.START_DOCUMENT;
                while (!finished) {
                    int e1 = xsr.getEventType();
                    int e2 = our.getEventType();
                    System.out.print(decode(e1) + formatLocation(xsr.getLocation()));
                    System.out.print(" <--> ");
                    System.out.println(decode(e2) + formatLocation(our.getLocation()));

                    assertEquals("Events are the same: " + xsr.getLocation() + ", " + our.getLocation(), decode(e1), decode(e2));
                    assertEquals("Events is as expected", decode(expected), decode(e2));
                    assertEquals(xsr.hasNext(), our.hasNext());
                    if (xsr.hasNext() && our.hasNext()) {
                        e1 = xsr.next();
                        e2 = our.next();
                        assertEquals("Events are the same: " + xsr.getLocation() + ", " + our.getLocation(), decode(e1), decode(e2));
                        expected = e2;
                    } else {
                        finished = true;
                    }

                }
            } finally {
                our.close();
            }
        } finally {
            xsr.close();
        }

    }

    private static String formatLocation(Location loc) {
        return "@" + loc.getLineNumber() + "," + loc.getColumnNumber();
    }

    private String decode(int c) {
        switch (c) {
            case XMLStreamConstants.START_ELEMENT:
                return "START_ELEMENT";
            case XMLStreamConstants.END_ELEMENT:
                return "END_ELEMENT";
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLStreamConstants.CHARACTERS:
                return "CHARACTERS";
            case XMLStreamConstants.COMMENT:
                return "COMMENT";
            case XMLStreamConstants.SPACE:
                return "SPACE";
            case XMLStreamConstants.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLStreamConstants.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLStreamConstants.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLStreamConstants.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLStreamConstants.DTD:
                return "DTD";
            case XMLStreamConstants.CDATA:
                return "CDATA";
            case XMLStreamConstants.NAMESPACE:
                return "NAMESPACE";
            case XMLStreamConstants.NOTATION_DECLARATION:
                return "NOTATION_DECLARATION";
            case XMLStreamConstants.ENTITY_DECLARATION:
                return "ENTITY_DECLARATION";
        }
        return "UNKNOWN_" + Integer.toString(c);
    }
}
