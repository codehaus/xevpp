package org.codehaus.xevpp.events;

import javax.xml.stream.events.Comment;
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
public class CommentImpl extends XMLEventImpl implements Comment {

    private final String text;
    private final String raw;

    public CommentImpl(String text) {
        this.text = text;
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
        return COMMENT;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return text;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write("<!--");
        String text = getText();
        if (text.length() > 0) {
            writer.write(text);
        }
        writer.write("-->");
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        if ((o instanceof CommentImpl)) {
            String that = ((CommentImpl)o).raw;
            return raw == that || raw.equals(that);
        }

        Comment comment = (Comment) o;

        if (!text.equals(comment.getText())) return false;

        return true;
    }

    public int hashCode() {
        return text.hashCode();
    }

    public String toString() {
        return raw;
    }
}
