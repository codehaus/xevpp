package org.codehaus.xevpp.events;

import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class NotationDeclarationImpl extends XMLEventImpl implements NotationDeclaration {
    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return NOTATION_DECLARATION;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public String getPublicId() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemId() {
        throw new UnsupportedOperationException("todo");
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }
}
