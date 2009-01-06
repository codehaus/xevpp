package org.codehaus.xevpp.events;

import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class EntityReferenceImpl extends XMLEventImpl implements EntityReference {
    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return ENTITY_REFERENCE;
    }

    public boolean isEntityReference() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public EntityDeclaration getDeclaration() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        throw new UnsupportedOperationException("todo");
    }
}
