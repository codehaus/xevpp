package org.codehaus.xevpp.events;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class EndElementImpl extends AbstractNamedEvent implements EndElement {

    private final String raw;
    private final List<Namespace> outOfScope;

    public EndElementImpl(String prefix, String namespaceUri, String localName, Iterator namespaces) {
        this(new QName(namespaceUri, localName, prefix), namespaces);
    }

    public EndElementImpl(QName name, Iterator namespaces) {
        super(name);
        outOfScope = new ArrayList<Namespace>();
        while (namespaces.hasNext()) {
            this.outOfScope.add((Namespace) namespaces.next());
        }
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
        return END_ELEMENT;
    }

    public boolean isEndElement() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write("</");
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(':');
        }
        writer.write(name.getLocalPart());
        writer.write('>');
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getNamespaces() {
        return Collections.unmodifiableList(outOfScope).iterator();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndElement)) return false;

        EndElement that = (EndElement)o;

        return name.equals(that.getName());
    }

    public int hashCode() {
        return name.hashCode();
    }


    public String toString() {
        return raw;
    }
}
