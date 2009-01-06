package org.codehaus.xevpp.reader;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 22:11:53
 */
public class SmokeTest {

    @Test
    public void simple() throws Exception {
        final String source = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<!--\r" +
                "comment\n\r" +
                "more\r\n" +
                "again\r" +
                "--><root>\n" +
                "    < child />  \t<child >\t\t</root>\n\n\n";
        InputStream is = new ByteArrayInputStream(source.getBytes("UTF-8"));
        Reader reader = XmlReader.createReader(is);
        StringBuilder target = new StringBuilder();
        int ch;
        char[] buf = new char[128];
        while (-1 != (ch = reader.read(buf))) {
            target.append(buf, 0, ch);
        }
        assertEquals(source, target.toString());

        assertTrue(true);
    }
}
