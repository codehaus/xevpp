package org.codehaus.xevpp.events;

import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class ProcessingInstructionImpl extends XMLEventImpl implements ProcessingInstruction {
    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return PROCESSING_INSTRUCTION;
    }

    public boolean isProcessingInstruction() {
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
    public String getTarget() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public String getData() {
        throw new UnsupportedOperationException("todo");
    }
}
