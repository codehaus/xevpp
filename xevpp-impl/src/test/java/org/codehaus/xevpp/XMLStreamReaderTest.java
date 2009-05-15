package org.codehaus.xevpp;

import junit.framework.TestCase;

import javax.xml.stream.XMLInputFactory;
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
    
    public void testSimple() throws Exception {
        XMLInputFactory inF = new com.sun.xml.internal.stream.XMLInputFactoryImpl();
        XMLStreamReader xsr = inF.createXMLStreamReader(getInputStream("/test-data/simple.xml"));
        try {
        XMLStreamReader our = new XMLStreamReaderImpl(inF.createXMLEventReader(getInputStream("/test-data/simple.xml")));
            try {
        while (xsr.hasNext() && our.hasNext()) {
            int e1 = xsr.next();
            int e2 = our.next();
            System.out.print(e1);
            System.out.print(" <--> ");
            System.out.println(e2);
            
            assertEquals("Events are the same: " + xsr.getLocation() + ", " + our.getLocation(), e1, e2);
            assertEquals(xsr.hasNext(), our.hasNext());
        }
            } finally {
                our.close();
            }
        } finally {
            xsr.close();
        }
        
    }
}
