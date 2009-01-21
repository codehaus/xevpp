package org.codehaus.xevpp.events;

import javax.xml.stream.events.EndDocument;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class EndDocumentImpl extends XMLEventImpl implements EndDocument {

    private final String raw;

    public EndDocumentImpl() {
        raw = "";
    }

    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return END_DOCUMENT;
    }

    public boolean isEndDocument() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        // nothing to output
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }
}
