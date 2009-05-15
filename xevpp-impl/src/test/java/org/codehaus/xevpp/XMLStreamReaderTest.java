package org.codehaus.xevpp;

import junit.framework.TestCase;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.Location;
import javax.xml.XMLConstants;
import java.io.InputStream;

import com.ctc.wstx.stax.WstxInputFactory;

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
    
    public void testSimple() throws Exception {
        XMLInputFactory inF = new WstxInputFactory();
        inF.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        XMLStreamReader xsr = inF.createXMLStreamReader(getInputStream("/test-data/simple.xml"));
        try {
        XMLStreamReader our = new XMLStreamReaderImpl(inF.createXMLEventReader(getInputStream("/test-data/simple.xml")));
            try {
                boolean finished = false;
                int expected = XMLStreamConstants.START_DOCUMENT;
        while (!finished) {
            int e1 = xsr.getEventType();
            int e2 = our.getEventType();
            System.out.print(decode(e1) + formatLocation(xsr.getLocation()));
            System.out.print(" <--> ");
            System.out.println(decode(e2) + formatLocation(our.getLocation()));
            
            assertEquals("Events are the same: " + xsr.getLocation() + ", " + our.getLocation(), e1, e2);
            assertEquals("Events is as expected", expected, e2);
            assertEquals(xsr.hasNext(), our.hasNext());
            if (xsr.hasNext() && our.hasNext()) {
                e1 = xsr.next();
                e2 = our.next();
                assertEquals("Events are the same: " + xsr.getLocation() + ", " + our.getLocation(), e1, e2);
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
        return "UNKNOWN_"+Integer.toString(c);
    }
}
