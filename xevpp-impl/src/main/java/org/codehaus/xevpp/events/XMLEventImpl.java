package org.codehaus.xevpp.events;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
abstract public class XMLEventImpl implements XMLEvent, Location {
    private int lineNumber = -1;
    private int columnNumber = -1;
    private int characterOffset = 0;

    public void setLocation(Location location) {
        lineNumber = location.getLineNumber();
        columnNumber = location.getColumnNumber();
        characterOffset = location.getCharacterOffset();
    }

    /**
     * {@inheritDoc}
     */
    public String getPublicId() {
        // optional method
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemId() {
        // optional method
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Setter for property 'lineNumber'.
     *
     * @param lineNumber Value to set for property 'lineNumber'.
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Setter for property 'columnNumber'.
     *
     * @param columnNumber Value to set for property 'columnNumber'.
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * {@inheritDoc}
     */
    public int getCharacterOffset() {
        return characterOffset;
    }

    /**
     * Setter for property 'characterOffset'.
     *
     * @param characterOffset Value to set for property 'characterOffset'.
     */
    public void setCharacterOffset(int characterOffset) {
        this.characterOffset = characterOffset;
    }


    /**
     * {@inheritDoc}
     */
    public Location getLocation() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStartElement() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAttribute() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNamespace() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEndElement() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEntityReference() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProcessingInstruction() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCharacters() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStartDocument() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEndDocument() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public StartElement asStartElement() {
        return (StartElement) this;
    }

    /**
     * {@inheritDoc}
     */
    public EndElement asEndElement() {
        return (EndElement) this;
    }

    /**
     * {@inheritDoc}
     */
    public Characters asCharacters() {
        return (Characters) this;
    }

    /**
     * {@inheritDoc}
     */
    public QName getSchemaType() {
        // optional method
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            doWriteAsEncodedUnicode(writer);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * Template method to be implemented by sub-classes.
     *
     * @param writer The writer that will output the data.
     * @throws java.io.IOException on I/O problems.
     * @throws javax.xml.stream.XMLStreamException
     *                             on XML Stream problems.
     */
    protected abstract void doWriteAsEncodedUnicode(Writer writer) throws IOException, XMLStreamException;

    /**
     * This method will write the XMLEvent verbatim as the original Unicode characters that were read in.
     *
     * @param writer The writer that will output the data
     * @throws XMLStreamException if there is a fatal error writing the event
     */
    public void writeAsVerbatimUnicode(Writer writer) throws XMLStreamException {
        try {
            doWriteAsVerbatimUnicode(writer);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * Template method to be implemented by sub-classes.
     *
     * @param writer The writer that will output the data.
     * @throws java.io.IOException on I/O problems.
     * @throws javax.xml.stream.XMLStreamException
     *                             on XML Stream problems.
     */
    protected abstract void doWriteAsVerbatimUnicode(Writer writer) throws IOException, XMLStreamException;
}
