package org.codehaus.xevpp.events;

import javax.xml.stream.XMLStreamException;
import java.util.List;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class DTDImpl extends XMLEventImpl implements javax.xml.stream.events.DTD {

    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return DTD;
    }

    /**
     * {@inheritDoc}
     */
    public String getDocumentTypeDeclaration() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public Object getProcessedDTD() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public List getNotations() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public List getEntities() {
        throw new UnsupportedOperationException("todo");
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }
}
