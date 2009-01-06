package org.codehaus.xevpp.events;

import javax.xml.stream.events.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class StartElementImpl extends AbstractNamedEvent implements StartElement {
    private final String raw;
    private final List<Attribute> attributes;
    private final List<Namespace> namespaces;
    private final NamespaceContext context;

    public StartElementImpl(QName name, Iterator attributes, Iterator namespaces) {
        this(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attributes, namespaces);
    }

    public StartElementImpl(String prefix, String namespaceUri, String localName) {
        this(prefix, namespaceUri, localName, Collections.emptySet().iterator(), Collections.emptySet().iterator());
    }

    public StartElementImpl(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces) {
        this(prefix, namespaceUri, localName, attributes, namespaces, null);
    }

    public StartElementImpl(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) {
        this(new QName(namespaceUri, localName, prefix), attributes, namespaces, context);
    }

    public StartElementImpl(QName name, Iterator attributes, Iterator namespaces, NamespaceContext context) {
        super(name);
        this.attributes = new ArrayList<Attribute>();
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            this.attributes.add(attribute);
        }
        this.namespaces = new ArrayList<Namespace>();
        while (namespaces.hasNext()) {
            Namespace namespace = (Namespace) namespaces.next();
            this.namespaces.add(namespace);
        }
        this.context = context;
        StringWriter temp = new StringWriter();
        try {
            writeAsEncodedUnicode(temp);
        } catch (XMLStreamException e) {
            throw new UnsupportedOperationException(e);
        }
        raw = temp.toString();
    }

    /**
     * {@inheritDoc}
     */
    public int getEventType() {
        return START_ELEMENT;
    }

    public boolean isStartElement() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getAttributes() {
        return Collections.unmodifiableList(attributes).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getNamespaces() {
        return Collections.unmodifiableList(namespaces).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public Attribute getAttributeByName(QName name) {
        for (Attribute attribute : attributes) {
            if (name.equals(attribute.getName())) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public NamespaceContext getNamespaceContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI(String prefix) {
        for (Namespace namespace : namespaces) {
            if (prefix.equals(namespace.getPrefix())) {
                return namespace.getNamespaceURI();
            }
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StartElement)) return false;

        if (o instanceof StartElementImpl) {
            final String that = ((StartElementImpl) o).raw;
            return raw == that || raw.equals(that);
        }
        StartElement that = (StartElement) o;

        if (!name.equals(that.getName())) return false;
        int match = 0;
        Iterator i = that.getAttributes();
        while (i.hasNext()) {
            Attribute attr = (Attribute) i.next();
            if (attr.equals(getAttributeByName(attr.getName()))) {
                match++;
            } else {
                return false;
            }
        }
        if (match != attributes.size()) {
            return false;
        }
        match = 0;
        i = that.getNamespaces();
        while (i.hasNext()) {
            Namespace namespace = (Namespace) i.next();
            if (namespace.getNamespaceURI().equals(getNamespaceURI(namespace.getPrefix()))) {
                match++;
            } else {
                return false;
            }
        }
        return match == namespaces.size();

    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        return result;
    }

    public String toString() {
        return raw;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        doWriteStartAsEncodedUnicode(writer);

        writer.write('>');
    }

    private void doWriteStartAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write('<');
        QName name = getName();
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(':');
        }
        writer.write(name.getLocalPart());

        // Any namespace declarations?
        Iterator ni = getNamespaces();
        while (ni.hasNext()) {
            writer.write(' ');
            // Ouch: neither ns nor attr are based on BaseEvent... doh!
            XMLEvent evt = (XMLEvent) ni.next();
            evt.writeAsEncodedUnicode(writer);
        }

        // Any attributes?
        Iterator ai = getAttributes();
        while (ai.hasNext()) {
            writer.write(' ');
            XMLEvent evt = (XMLEvent) ai.next();
            evt.writeAsEncodedUnicode(writer);
        }
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }

    public void writeAsEncodedUnicode(Writer writer, EndElement endElement) throws XMLStreamException {
        if (endElement == null) {
            writeAsEncodedUnicode(writer);
        } else {
            try {
                doWriteStartAsEncodedUnicode(writer);
                writer.write("/>");
            } catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
    }
}
