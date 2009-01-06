package org.codehaus.xevpp.events;

import javax.xml.stream.events.StartDocument;
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
public class StartDocumentImpl extends XMLEventImpl implements StartDocument {
    protected String systemId = "";
    protected String publicId = "";
    protected String encoding = "UTF-8";
    protected boolean standalone = false;
    protected String version = "1.0";
    private boolean encodingDefined = false;
    private boolean standaloneDefined = false;
    private final String raw;

    public StartDocumentImpl() {
        StringWriter temp = new StringWriter();
        try {
            writeAsEncodedUnicode(temp);
        } catch (XMLStreamException e) {
            throw new UnsupportedOperationException(e);
        }
        raw = temp.toString();
    }

    public StartDocumentImpl(String encoding, String version, boolean standalone) {
        this.encoding = encoding;
        this.encodingDefined = true;
        this.version = version;
        this.standalone = standalone;
        this.standaloneDefined = true;
        StringWriter temp = new StringWriter();
        try {
            writeAsEncodedUnicode(temp);
        } catch (XMLStreamException e) {
            throw new UnsupportedOperationException(e);
        }
        raw = temp.toString();
    }

    public StartDocumentImpl(String encoding, String version) {
        this.encoding = encoding;
        this.encodingDefined = true;
        this.version = version;
        StringWriter temp = new StringWriter();
        try {
            writeAsEncodedUnicode(temp);
        } catch (XMLStreamException e) {
            throw new UnsupportedOperationException(e);
        }
        raw = temp.toString();
    }

    public StartDocumentImpl(String encoding) {
        this.encoding = encoding;
        this.encodingDefined = true;
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
        return START_DOCUMENT;
    }

    public boolean isStartDocument() {
        return true;
    }

    protected void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write("<?xml version=\"");
        writer.write(version);
        writer.write("\"");
        if (encodingDefined) {
            writer.write(" encoding=\"");
            writer.write(encoding);
            writer.write('\"');
        }
        if (standaloneDefined) {
            writer.write(" standalone=\"");
            writer.write(standalone ? "yes\"" : "no\"");
        }
        writer.write("?>");
    }

    protected void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException {
        writer.write(raw);
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemId() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getCharacterEncodingScheme() {
        return encoding;
    }

    /**
     * {@inheritDoc}
     */
    public boolean encodingSet() {
        return encodingDefined;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStandalone() {
        return standalone;
    }

    /**
     * {@inheritDoc}
     */
    public boolean standaloneSet() {
        return standaloneDefined;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return version;
    }
}
