package org.codehaus.xevpp.events;

import javax.xml.stream.events.Characters;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class CharactersImpl extends XMLEventImpl implements Characters {

    private final String raw;
    private final String content;
    private final boolean isCData;
    private final boolean isSpace;
    private final boolean isIgnorable;

    public CharactersImpl(String content, boolean isCData) {
        this(content, isCData, false, false);
    }

    public CharactersImpl(String content, boolean isCData, boolean isSpace, boolean isIgnorable) {
        this.content = content;
        this.isCData = isCData;
        this.isSpace = isSpace;
        this.isIgnorable = isIgnorable;
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
        return CHARACTERS;
    }

    public boolean isCharacters() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        if (isCData) {
            writer.write("<![CDATA[");
            writer.write(getData());
            writer.write("]]>");
        } else {
            String data = getData();
            int len = data.length();

            if (len > 0) {
                int i = 0;

                // Let's see how much we can output without encoding:
                loop:
                for (; i < len; ++i) {
                    switch (data.charAt(i)) {
                    case '&':
                    case '<':
                    case '>': // only mandatory as part of ']]>' but let's play it safe
                        break loop;
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
                        case '>':
                            writer.write("&gt;");
                            break;
                        default:
                            writer.write(c);
                        }
                    }
                }
            }
        }
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }

    /**
     * {@inheritDoc}
     */
    public String getData() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWhiteSpace() {
        return isSpace;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCData() {
        return isCData;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIgnorableWhiteSpace() {
        return isIgnorable;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Characters)) return false;
        if (o instanceof CharactersImpl) {
            final String that = ((CharactersImpl) o).raw;
            return raw == that || raw.equals(that);
        }


        Characters that = (Characters) o;

        if (isCData != that.isCData()) return false;
        if (isIgnorable != that.isIgnorableWhiteSpace()) return false;
        if (isSpace != that.isWhiteSpace()) return false;
        if (!content.equals(that.getData())) return false;

        return true;
    }

    public int hashCode() {
        return content.hashCode();
    }

    public String toString() {
        return raw;
    }
}
