package org.codehaus.xevpp;

import org.codehaus.xevpp.reader.XmlReader;

import javax.xml.stream.*;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class XMLInputFactoryImpl extends XMLInputFactory {

    /**
     * Create a new instance of the factory.
     * @return The new instance
     * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLInputFactory newInstance() throws FactoryConfigurationError {
        return XMLInputFactory.newInstance(XMLInputFactoryImpl.class.getName(),
                XMLInputFactoryImpl.class.getClassLoader());
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        try {
            return createXMLStreamReader(XmlReader.createReader(stream));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        try {
            return createXMLStreamReader(XmlReader.createReader(stream, encoding));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return createXMLStreamReader(stream);
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return createXMLStreamReader(reader);
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLResolver getXMLResolver() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public void setXMLResolver(XMLResolver resolver) {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLReporter getXMLReporter() {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public void setXMLReporter(XMLReporter reporter) {
        throw new UnsupportedOperationException("todo");
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

    /**
     * {@inheritDoc}
     */
    public void setEventAllocator(XMLEventAllocator allocator) {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public XMLEventAllocator getEventAllocator() {
        throw new UnsupportedOperationException("todo");
    }
}
