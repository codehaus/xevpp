package org.codehaus.xevpp.events;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import javax.xml.XMLConstants;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 *        Time: 20:57:23
 */
public class AttributeImpl extends AbstractNamedEvent implements Attribute {
    private final String value;
    private final String raw;

    public AttributeImpl(String prefix, String namespaceURI, String localName, String value) {
        this(new QName(namespaceURI, localName, prefix), value);
    }

    public AttributeImpl(String localName, String value) {
        this(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI, localName, value);
    }

    public AttributeImpl(QName name, String value) {
        super(name);
        this.value = value;
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
        return ATTRIBUTE;
    }

    public boolean isAttribute() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(':');
        }
        writer.write(name.getLocalPart());

        writer.write("=\"");
        final String data = this.value;
        int len = data.length();

        if (len > 0) {
            int i = 0;

            // Let's see how much we can output without encoding:
            loop:
            for (; i < len; ++i) {
                final char c = data.charAt(i);
                switch (c) {
                    case '&':
                    case '<':
                    case '"':
                        break loop;
                    default:
                        if (c < 32) {
                            break loop;
                        }
                }
            }

            // got it all?
            if (i == len) {
                writer.write(data);
            } else { // nope...
                if (i > 0) {
                    writer.write(data, 0, i);
                }
                for (; i < len; ++i) {
                    final char c = data.charAt(i);
                    switch (c) {
                        case '&':
                            writer.write("&amp;");
                            break;
                        case '<':
                            writer.write("&lt;");
                            break;
                        case '"':
                            writer.write("&quot;");
                            break;
                        default:
                            if (c < 32) {
                                writeEncodedChar(writer, c);
                            } else {
                                writer.write(c);
                            }
                    }
                }
            }
        }

        writer.write('"');
    }

    private static void writeEncodedChar(java.io.Writer writer, char c) throws java.io.IOException {
        // This is slow, but gets work done:
        writer.write("&#");
        writer.write(Integer.toString(c));
        writer.write(';');
    }


    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.append(raw);
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String getDTDType() {
        return "CDATA";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSpecified() {
        // they are always specified as we don't support attribute defaulting
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        if (o instanceof AttributeImpl) {
            final String that = ((AttributeImpl) o).raw;
            return raw == that || raw.equals(that);
        }

        Attribute attribute = (Attribute) o;

        if (!name.equals(attribute.getName())) return false;
        if (!value.equals(attribute.getValue())) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return raw;
    }
}
