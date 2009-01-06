package org.codehaus.xevpp;

import org.codehaus.xevpp.events.StartElementImpl;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.io.Writer;
import java.io.IOException;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 21:15:26
 */
public class XMLEventWriterImpl implements XMLEventWriter {
    private final Writer writer;

    private Stack<XMLEventWriterImpl.State> path = null;

    public XMLEventWriterImpl(Writer writer) {
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws XMLStreamException {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws XMLStreamException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(XMLEvent event) throws XMLStreamException {
        if (event instanceof StartDocument) {
            if (path != null) {
                // we have already started writing, only a StartDocument is allowed
                throw new XMLStreamException("StartDocument is only valid at the start of the document");
            }
            event.writeAsEncodedUnicode(writer);
            path = new Stack<State>();
            return;
        } else if (event instanceof DTD || event instanceof ProcessingInstruction) {
            if (path == null) {
                path = new Stack<State>();
            }
            if (!path.isEmpty()) {
                throw new XMLStreamException("The root element has already started");
            }
        } else if (event instanceof StartElement) {
            if (path == null) {
                path = new Stack<State>();
            }
            if (!path.isEmpty()) {
                closeStartElement();
            }
            path.push(new State((StartElement) event));
            return;
        } else if (event instanceof Namespace) {
            if (path == null || path.isEmpty()) {
                throw new XMLStreamException("Namespaces are not valid outside of a tag");
            }
            path.peek().addNamespace((Namespace) event);
            return;
        } else if (event instanceof Attribute) {
            if (path == null || path.isEmpty()) {
                throw new XMLStreamException("Attributes are not valid outside of a tag");
            }
            path.peek().addAttribute((Attribute) event);
            return;
        } else if (event instanceof EndElement) {
            if (path == null || path.isEmpty()) {
                throw new XMLStreamException("Cannot have an EndElement without a StartElement");
            }
            path.pop().writeAsEncodedUnicode(writer, (EndElement) event);
            return;
        } else if (event instanceof Characters) {
            Characters chars = (Characters) event;
            if (path == null || path.isEmpty()) {
                if (!chars.isWhiteSpace() && !chars.isIgnorableWhiteSpace()) {
                    throw new XMLStreamException("Non-whitespace is not allowed outside of the root tag");
                }
            } else {
                closeStartElement();
            }
        } else if (event instanceof EntityReference) {
            if (path == null || path.isEmpty()) {
                throw new XMLStreamException("Non-whitespace is not allowed outside of the root tag");
            }
            closeStartElement();
        }
        event.writeAsEncodedUnicode(writer);
    }

    private void closeStartElement() throws XMLStreamException {
        if (!path.peek().isClosed()) {
            // close the tag
            path.peek().writeAsEncodedUnicode(writer, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefix(String uri) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException("todo");
    }

    /**
     * {@inheritDoc}
     */
    public NamespaceContext getNamespaceContext() {
        throw new UnsupportedOperationException("todo");
    }

    public static class State {
        private final StartElement element;
        private final List<Attribute> attributes = new ArrayList<Attribute>();
        private final List<Namespace> namespaces = new ArrayList<Namespace>();
        private boolean closed = false;
        private boolean modified = false;

        public State(StartElement element) {
            this.element = element;
            Iterator i = element.getAttributes();
            while (i.hasNext()) {
                attributes.add((Attribute) i.next());
            }
            i = element.getNamespaces();
            while (i.hasNext()) {
                namespaces.add((Namespace) i.next());
            }
        }

        public void addAttribute(Attribute attribute) throws XMLStreamException {
            if (closed) {
                throw new XMLStreamException("Attributes are not allowed outside of a start tag");
            }
            final QName qName = attribute.getName();
            for (Attribute attr : attributes) {
                if (qName.equals(attr.getName())) {
                    throw new XMLStreamException("Attributes has already been defined");
                }
            }
            for (Namespace ns : namespaces) {
                if (qName.equals(ns.getName())) {
                    throw new XMLStreamException("Namespaces has already been defined");
                }
            }
            attributes.add(attribute);
            modified = true;
        }

        public void writeAsEncodedUnicode(Writer writer, EndElement endElement) throws XMLStreamException {
            if (endElement == null && closed) {
                throw new XMLStreamException("Start tag has already been written");
            } else if (endElement != null && !endElement.getName().equals(element.getName())) {
                throw new XMLStreamException("Unmatched start and end elements");
            }
            if (!closed) {
                StartElement element;
                if (modified) {
                    element = new StartElementImpl(this.element.getName(),
                            attributes.iterator(),
                            namespaces.iterator(),
                            this.element.getNamespaceContext());
                } else {
                    element = this.element;
                }
                closed = true;
                if (element instanceof StartElementImpl) {
                    ((StartElementImpl) element).writeAsEncodedUnicode(writer, endElement);
                } else {
                    element.writeAsEncodedUnicode(writer);
                    if (endElement != null) {
                        endElement.writeAsEncodedUnicode(writer);
                    }
                }
            } else {
                // this is the matching end element
                endElement.writeAsEncodedUnicode(writer);
            }
        }

        public boolean isClosed() {
            return closed;
        }

        public void addNamespace(Namespace namespace) throws XMLStreamException {
            if (closed) {
                throw new XMLStreamException("Namespaces are not allowed outside of a start tag");
            }
            final QName qName = namespace.getName();
            for (Attribute attr : attributes) {
                if (qName.equals(attr.getName())) {
                    throw new XMLStreamException("Namespaces has already been defined");
                }
            }
            for (Namespace ns : namespaces) {
                if (qName.equals(ns.getName())) {
                    throw new XMLStreamException("Namespaces has already been defined");
                }
            }
            namespaces.add(namespace);
            modified = true;
        }
    }
}
