package org.codehaus.xevpp;

import javax.xml.stream.*;
import javax.xml.transform.Result;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class XMLOutputFactoryImpl extends XMLOutputFactory {
    /**
     * Create a new instance of the factory.
     * @return The new instance
     * @throws javax.xml.stream.FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLOutputFactory newInstance() throws FactoryConfigurationError {
        return new XMLOutputFactoryImpl();
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        try {
            return new XMLEventWriterImpl(new OutputStreamWriter(stream, encoding));
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return new XMLEventWriterImpl(stream);
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySupported(String name) {
        throw new UnsupportedOperationException("todo");
    }
}
