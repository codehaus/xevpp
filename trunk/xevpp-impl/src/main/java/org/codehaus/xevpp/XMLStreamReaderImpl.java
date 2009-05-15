package org.codehaus.xevpp;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link XMLStreamReader} that is based on an {@link XMLEventReader}.
 *
 * @author Stephen Connolly
 */
public class XMLStreamReaderImpl
        implements XMLStreamReader {
    private final XMLEventReader delegate;

    private XMLEvent current = null;

    private StartElement currentStart = null;

    public XMLStreamReaderImpl(XMLEventReader delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the value of a feature/property from the underlying implementation
     *
     * @param name The name of the property, may not be null
     * @return The value of the property
     * @throws IllegalArgumentException if name is null
     */
    public Object getProperty(String name)
            throws IllegalArgumentException {
        return delegate.getProperty(name);
    }

    /**
     * Get next parsing event - a processor may return all contiguous
     * character data in a single chunk, or it may split it into several chunks.
     * If the property javax.xml.stream.isCoalescing is set to true
     * element content must be coalesced and only one CHARACTERS event
     * must be returned for contiguous element content or
     * CDATA Sections.
     * <p/>
     * By default entity references must be
     * expanded and reported transparently to the application.
     * An exception will be thrown if an entity reference cannot be expanded.
     * If element content is empty (i.e. content is "") then no CHARACTERS event will be reported.
     * <p/>
     * <p>Given the following XML:<br>
     * &lt;foo>&lt;!--description-->content text&lt;![CDATA[&lt;greeting>Hello&lt;/greeting>]]>other content&lt;/foo><br>
     * The behavior of calling next() when being on foo will be:<br>
     * 1- the comment (COMMENT)<br>
     * 2- then the characters section (CHARACTERS)<br>
     * 3- then the CDATA section (another CHARACTERS)<br>
     * 4- then the next characters section (another CHARACTERS)<br>
     * 5- then the END_ELEMENT<br>
     * <p/>
     * <p><b>NOTE:</b> empty element (such as &lt;tag/>) will be reported
     * with  two separate events: START_ELEMENT, END_ELEMENT - This preserves
     * parsing equivalency of empty element to &lt;tag>&lt;/tag>.
     * <p/>
     * This method will throw an IllegalStateException if it is called after hasNext() returns false.
     *
     * @return the integer code corresponding to the current parse event
     * @throws javax.xml.stream.XMLStreamException
     *          if there is an error processing the underlying XML source
     * @see javax.xml.stream.events.XMLEvent
     */
    public int next()
            throws XMLStreamException {
        current = delegate.nextEvent();
        if (current.isStartElement()) {
            currentStart = current.asStartElement();
        }
        return current.getEventType();
    }

    /**
     * Test if the current event is of the given type and if the namespace and name match the current
     * namespace and name of the current event.  If the namespaceURI is null it is not checked for equality,
     * if the localName is null it is not checked for equality.
     *
     * @param type         the event type
     * @param namespaceURI the uri of the event, may be null
     * @param localName    the localName of the event, may be null
     * @throws javax.xml.stream.XMLStreamException
     *          if the required values are not matched.
     */
    public void require(int type, String namespaceURI, String localName)
            throws XMLStreamException {
        if (current.getEventType() != type) {
            throw new XMLStreamException("Require event of type " + type + " but actually " + current.getEventType(),
                    getLocation());
        }
        if (namespaceURI != null && !namespaceURI.equals(currentStart.getName().getNamespaceURI())) {
            throw new XMLStreamException("Require event with namespace " + namespaceURI + " but actually " 
                    + currentStart.getName().getNamespaceURI(), getLocation());
        }
        if (localName != null && !localName.equals(currentStart.getName().getLocalPart())) {
            throw new XMLStreamException("Require event with name '" + localName + "' but actually '" 
                    + currentStart.getName().getLocalPart() + "'", getLocation());
        }
    }

    /**
     * Reads the content of a text-only element, an exception is thrown if this is
     * not a text-only element.
     * Regardless of value of javax.xml.stream.isCoalescing this method always returns coalesced content.
     * <br /> Precondition: the current event is START_ELEMENT.
     * <br /> Postcondition: the current event is the corresponding END_ELEMENT.
     * <p/>
     * <br />The method does the following (implementations are free to optimized
     * but must do equivalent processing):
     * <pre>
     * if(getEventType() != XMLStreamConstants.START_ELEMENT) {
     * throw new XMLStreamException(
     * "parser must be on START_ELEMENT to read next text", getLocation());
     * }
     * int eventType = next();
     * StringBuffer content = new StringBuffer();
     * while(eventType != XMLStreamConstants.END_ELEMENT ) {
     * if(eventType == XMLStreamConstants.CHARACTERS
     * || eventType == XMLStreamConstants.CDATA
     * || eventType == XMLStreamConstants.SPACE
     * || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
     * buf.append(getText());
     * } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
     * || eventType == XMLStreamConstants.COMMENT) {
     * // skipping
     * } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
     * throw new XMLStreamException(
     * "unexpected end of document when reading element text content", this);
     * } else if(eventType == XMLStreamConstants.START_ELEMENT) {
     * throw new XMLStreamException(
     * "element text content may not contain START_ELEMENT", getLocation());
     * } else {
     * throw new XMLStreamException(
     * "Unexpected event type "+eventType, getLocation());
     * }
     * eventType = next();
     * }
     * return buf.toString();
     * </pre>
     *
     * @throws javax.xml.stream.XMLStreamException
     *          if the current event is not a START_ELEMENT
     *          or if a non text element is encountered
     */
    public String getElementText()
            throws XMLStreamException {
        if (getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", getLocation());
        }
        int eventType = next();
        StringBuffer content = new StringBuffer();
        while (eventType != XMLStreamConstants.END_ELEMENT) {
            if (eventType == XMLStreamConstants.CHARACTERS
                    || eventType == XMLStreamConstants.CDATA
                    || eventType == XMLStreamConstants.SPACE
                    || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                content.append(getText());
            } else if (eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                    || eventType == XMLStreamConstants.COMMENT) {
                // skipping
            } else if (eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException("unexpected end of document when reading element text content", getLocation());
            } else if (eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException("element text content may not contain START_ELEMENT", getLocation());
            } else {
                throw new XMLStreamException("Unexpected event type " + eventType, getLocation());
            }
            eventType = next();
        }
        return content.toString();
    }

    /**
     * Skips any white space (isWhiteSpace() returns true), COMMENT,
     * or PROCESSING_INSTRUCTION,
     * until a START_ELEMENT or END_ELEMENT is reached.
     * If other than white space characters, COMMENT, PROCESSING_INSTRUCTION, START_ELEMENT, END_ELEMENT
     * are encountered, an exception is thrown. This method should
     * be used when processing element-only content seperated by white space.
     * <p/>
     * <br /> Precondition: none
     * <br /> Postcondition: the current event is START_ELEMENT or END_ELEMENT
     * and cursor may have moved over any whitespace event.
     * <p/>
     * <br />Essentially it does the following (implementations are free to optimized
     * but must do equivalent processing):
     * <pre>
     * int eventType = next();
     * while((eventType == XMLStreamConstants.CHARACTERS &amp;&amp; isWhiteSpace()) // skip whitespace
     * || (eventType == XMLStreamConstants.CDATA &amp;&amp; isWhiteSpace())
     * // skip whitespace
     * || eventType == XMLStreamConstants.SPACE
     * || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
     * || eventType == XMLStreamConstants.COMMENT
     * ) {
     * eventType = next();
     * }
     * if (eventType != XMLStreamConstants.START_ELEMENT &amp;&amp; eventType != XMLStreamConstants.END_ELEMENT) {
     * throw new String XMLStreamException("expected start or end tag", getLocation());
     * }
     * return eventType;
     * </pre>
     *
     * @return the event type of the element read (START_ELEMENT or END_ELEMENT)
     * @throws javax.xml.stream.XMLStreamException
     *                                if the current event is not white space, PROCESSING_INSTRUCTION,
     *                                START_ELEMENT or END_ELEMENT
     * @throws NoSuchElementException if this is called when hasNext() returns false
     */
    public int nextTag()
            throws XMLStreamException {
        current = delegate.nextTag();
        if (current.isStartElement()) {
            currentStart = current.asStartElement();
        }
        return current.getEventType();
    }

    /**
     * Returns true if there are more parsing events and false
     * if there are no more events.  This method will return
     * false if the current state of the XMLStreamReader is
     * END_DOCUMENT
     *
     * @return true if there are more events, false otherwise
     * @throws javax.xml.stream.XMLStreamException
     *          if there is a fatal error detecting the next state
     */
    public boolean hasNext()
            throws XMLStreamException {
        return delegate.hasNext();
    }

    /**
     * Frees any resources associated with this Reader.  This method does not close the
     * underlying input source.
     *
     * @throws javax.xml.stream.XMLStreamException
     *          if there are errors freeing associated resources
     */
    public void close()
            throws XMLStreamException {
        delegate.close();
    }

    /**
     * Return the uri for the given prefix.
     * The uri returned depends on the current state of the processor.
     * <p/>
     * <p><strong>NOTE:</strong>The 'xml' prefix is bound as defined in
     * <a href="http://www.w3.org/TR/REC-xml-names/#ns-using">Namespaces in XML</a>
     * specification to "http://www.w3.org/XML/1998/namespace".
     * <p/>
     * <p><strong>NOTE:</strong> The 'xmlns' prefix must be resolved to following namespace
     * <a href="http://www.w3.org/2000/xmlns/">http://www.w3.org/2000/xmlns/</a>
     *
     * @param prefix The prefix to lookup, may not be null
     * @return the uri bound to the given prefix or null if it is not bound
     * @throws IllegalArgumentException if the prefix is null
     */
    public String getNamespaceURI(String prefix) {
        return currentStart.getNamespaceURI(prefix);
    }

    /**
     * Returns true if the cursor points to a start tag (otherwise false)
     *
     * @return true if the cursor points to a start tag, false otherwise
     */
    public boolean isStartElement() {
        return current.isStartElement();
    }

    /**
     * Returns true if the cursor points to an end tag (otherwise false)
     *
     * @return true if the cursor points to an end tag, false otherwise
     */
    public boolean isEndElement() {
        return current.isEndElement();
    }

    /**
     * Returns true if the cursor points to a character data event
     *
     * @return true if the cursor points to character data, false otherwise
     */
    public boolean isCharacters() {
        return current.isCharacters();
    }

    /**
     * Returns true if the cursor points to a character data event
     * that consists of all whitespace
     *
     * @return true if the cursor points to all whitespace, false otherwise
     */
    public boolean isWhiteSpace() {
        return current.isCharacters() && current.asCharacters().isWhiteSpace();
    }

    /**
     * Returns the normalized attribute value of the
     * attribute with the namespace and localName
     * If the namespaceURI is null the namespace
     * is not checked for equality
     *
     * @param namespaceURI the namespace of the attribute
     * @param localName    the local name of the attribute, cannot be null
     * @return returns the value of the attribute , returns null if not found
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeValue(String namespaceURI, String localName) {
        return currentStart.getAttributeByName(new QName(namespaceURI, localName)).getValue();
    }

    /**
     * Returns the count of attributes on this START_ELEMENT,
     * this method is only valid on a START_ELEMENT or ATTRIBUTE.  This
     * count excludes namespace definitions.  Attribute indices are
     * zero-based.
     *
     * @return returns the number of attributes
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public int getAttributeCount() {
        int count = 0;
        for (Iterator i = current.asStartElement().getAttributes(); i.hasNext(); count++) {
            i.next();
        }
        return count;
    }

    /**
     * Returns the qname of the attribute at the provided index
     *
     * @param index the position of the attribute
     * @return the QName of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public QName getAttributeName(int index) {
        return getAttribute(index).getName();
    }

    private Attribute getAttribute(int index) {
        Iterator i = current.asStartElement().getAttributes();
        for (; i.hasNext() && index > 0; index--) {
            i.next();
        }
        return (Attribute) i.next();
    }

    /**
     * Returns the namespace of the attribute at the provided
     * index
     *
     * @param index the position of the attribute
     * @return the namespace URI (can be null)
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeNamespace(int index) {
        return getAttributeName(index).getNamespaceURI();
    }

    /**
     * Returns the localName of the attribute at the provided
     * index
     *
     * @param index the position of the attribute
     * @return the localName of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeLocalName(int index) {
        return getAttributeName(index).getLocalPart();
    }

    /**
     * Returns the prefix of this attribute at the
     * provided index
     *
     * @param index the position of the attribute
     * @return the prefix of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributePrefix(int index) {
        return getAttributeName(index).getPrefix();
    }

    /**
     * Returns the XML type of the attribute at the provided
     * index
     *
     * @param index the position of the attribute
     * @return the XML type of the attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeType(int index) {
        return getAttribute(index).getDTDType();
    }

    /**
     * Returns the value of the attribute at the
     * index
     *
     * @param index the position of the attribute
     * @return the attribute value
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public String getAttributeValue(int index) {
        return getAttribute(index).getValue();
    }

    /**
     * Returns a boolean which indicates if this
     * attribute was created by default
     *
     * @param index the position of the attribute
     * @return true if this is a default attribute
     * @throws IllegalStateException if this is not a START_ELEMENT or ATTRIBUTE
     */
    public boolean isAttributeSpecified(int index) {
        return getAttribute(index).isSpecified();
    }

    /**
     * Returns the count of namespaces declared on this START_ELEMENT or END_ELEMENT,
     * this method is only valid on a START_ELEMENT, END_ELEMENT or NAMESPACE. On
     * an END_ELEMENT the count is of the namespaces that are about to go
     * out of scope.  This is the equivalent of the information reported
     * by SAX callback for an end element event.
     *
     * @return returns the number of namespace declarations on this specific element
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public int getNamespaceCount() {
        if (current.isStartElement()) {
            int count = 0;
            for (Iterator i = current.asStartElement().getNamespaces(); i.hasNext(); count++) {
                i.next();
            }
            return count;
        } else if (current.isEndElement()) {
            int count = 0;
            for (Iterator i = current.asEndElement().getNamespaces(); i.hasNext(); count++) {
                i.next();
            }
            return count;
        }
        throw new IllegalStateException();
    }

    private Namespace getNamespace(int index) {
        Iterator i = current.isStartElement()
                ? current.asStartElement().getNamespaces()
                : current.isEndElement() ? current.asEndElement().getNamespaces() : null;
        for (; i.hasNext() && index > 0; index--) {
            i.next();
        }
        return (Namespace) i.next();
    }

    /**
     * Returns the prefix for the namespace declared at the
     * index.  Returns null if this is the default namespace
     * declaration
     *
     * @param index the position of the namespace declaration
     * @return returns the namespace prefix
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public String getNamespacePrefix(int index) {
        return getNamespace(index).getPrefix();
    }

    /**
     * Returns the uri for the namespace declared at the
     * index.
     *
     * @param index the position of the namespace declaration
     * @return returns the namespace uri
     * @throws IllegalStateException if this is not a START_ELEMENT, END_ELEMENT or NAMESPACE
     */
    public String getNamespaceURI(int index) {
        return getNamespace(index).getNamespaceURI();
    }

    /**
     * Returns a read only namespace context for the current
     * position.  The context is transient and only valid until
     * a call to next() changes the state of the reader.
     *
     * @return return a namespace context
     */
    public NamespaceContext getNamespaceContext() {
        return current.asStartElement().getNamespaceContext();
    }

    /**
     * Returns an integer code that indicates the type
     * of the event the cursor is pointing to.
     */
    public int getEventType() {
        return current.getEventType();
    }

    /**
     * Returns the current value of the parse event as a string,
     * this returns the string value of a CHARACTERS event,
     * returns the value of a COMMENT, the replacement value
     * for an ENTITY_REFERENCE, the string value of a CDATA section,
     * the string value for a SPACE event,
     * or the String value of the internal subset of the DTD.
     * If an ENTITY_REFERENCE has been resolved, any character data
     * will be reported as CHARACTERS events.
     *
     * @return the current text or null
     * @throws IllegalStateException if this state is not
     *                               a valid text state.
     */
    public String getText() {
        return current.asCharacters().getData();
    }

    /**
     * Returns an array which contains the characters from this event.
     * This array should be treated as read-only and transient. I.e. the array will
     * contain the text characters until the XMLStreamReader moves on to the next event.
     * Attempts to hold onto the character array beyond that time or modify the
     * contents of the array are breaches of the contract for this interface.
     *
     * @return the current text or an empty array
     * @throws IllegalStateException if this state is not
     *                               a valid text state.
     */
    public char[] getTextCharacters() {
        return getText().toCharArray();
    }

    /**
     * Gets the the text associated with a CHARACTERS, SPACE or CDATA event.
     * Text starting a "sourceStart" is copied into "target" starting at "targetStart".
     * Up to "length" characters are copied.  The number of characters actually copied is returned.
     * <p/>
     * The "sourceStart" argument must be greater or equal to 0 and less than or equal to
     * the number of characters associated with the event.  Usually, one requests text starting at a "sourceStart" of 0.
     * If the number of characters actually copied is less than the "length", then there is no more text.
     * Otherwise, subsequent calls need to be made until all text has been retrieved. For example:
     * <p/>
     * <code>
     * int length = 1024;
     * char[] myBuffer = new char[ length ];
     * <p/>
     * for ( int sourceStart = 0 ; ; sourceStart += length )
     * {
     * int nCopied = stream.getTextCharacters( sourceStart, myBuffer, 0, length );
     * <p/>
     * if (nCopied < length)
     * break;
     * }
     * </code>
     * XMLStreamException may be thrown if there are any XML errors in the underlying source.
     * The "targetStart" argument must be greater than or equal to 0 and less than the length of "target",
     * Length must be greater than 0 and "targetStart + length" must be less than or equal to length of "target".
     *
     * @param sourceStart the index of the first character in the source array to copy
     * @param target      the destination array
     * @param targetStart the start offset in the target array
     * @param length      the number of characters to copy
     * @return the number of characters actually copied
     * @throws javax.xml.stream.XMLStreamException
     *                                       if the underlying XML source is not well-formed
     * @throws IndexOutOfBoundsException     if targetStart < 0 or > than the length of target
     * @throws IndexOutOfBoundsException     if length < 0 or targetStart + length > length of target
     * @throws UnsupportedOperationException if this method is not supported
     * @throws NullPointerException          is if target is null
     */
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns the offset into the text character array where the first
     * character (of this text event) is stored.
     *
     * @throws IllegalStateException if this state is not
     *                               a valid text state.
     */
    public int getTextStart() {
        return 0;
    }

    /**
     * Returns the length of the sequence of characters for this
     * Text event within the text character array.
     *
     * @throws IllegalStateException if this state is not
     *                               a valid text state.
     */
    public int getTextLength() {
        return getTextCharacters().length;
    }

    /**
     * Return input encoding if known or null if unknown.
     *
     * @return the encoding of this instance or null
     */
    public String getEncoding() {
        return null;
    }

    /**
     * Return true if the current event has text, false otherwise
     * The following events have text:
     * CHARACTERS,DTD ,ENTITY_REFERENCE, COMMENT, SPACE
     */
    public boolean hasText() {
        switch (current.getEventType()) {
            case CHARACTERS:
            case DTD:
            case ENTITY_REFERENCE:
            case COMMENT:
            case SPACE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Return the current location of the processor.
     * If the Location is unknown the processor should return
     * an implementation of Location that returns -1 for the
     * location and null for the publicId and systemId.
     * The location information is only valid until next() is
     * called.
     */
    public Location getLocation() {
        return current.getLocation();
    }

    /**
     * Returns a QName for the current START_ELEMENT or END_ELEMENT event
     *
     * @return the QName for the current START_ELEMENT or END_ELEMENT event
     * @throws IllegalStateException if this is not a START_ELEMENT or
     *                               END_ELEMENT
     */
    public QName getName() {
        if (current.isStartElement()) {
            return current.asStartElement().getName();
        }
        if (current.isEndElement()) {
            return current.asEndElement().getName();
        }
        throw new IllegalStateException();
    }

    /**
     * Returns the (local) name of the current event.
     * For START_ELEMENT or END_ELEMENT returns the (local) name of the current element.
     * For ENTITY_REFERENCE it returns entity name.
     * The current event must be START_ELEMENT or END_ELEMENT,
     * or ENTITY_REFERENCE
     *
     * @return the localName
     * @throws IllegalStateException if this not a START_ELEMENT,
     *                               END_ELEMENT or ENTITY_REFERENCE
     */
    public String getLocalName() {
        return getName().getLocalPart();
    }

    /**
     * returns true if the current event has a name (is a START_ELEMENT or END_ELEMENT)
     * returns false otherwise
     */
    public boolean hasName() {
        return current.isStartElement() || current.isEndElement();
    }

    /**
     * If the current event is a START_ELEMENT or END_ELEMENT  this method
     * returns the URI of the prefix or the default namespace.
     * Returns null if the event does not have a prefix.
     *
     * @return the URI bound to this elements prefix, the default namespace, or null
     */
    public String getNamespaceURI() {
        return getName().getNamespaceURI();
    }

    /**
     * Returns the prefix of the current event or null if the event does not have a prefix
     *
     * @return the prefix or null
     */
    public String getPrefix() {
        return getName().getPrefix();
    }

    /**
     * Get the xml version declared on the xml declaration
     * Returns null if none was declared
     *
     * @return the XML version or null
     */
    public String getVersion() {
        return null;
    }

    /**
     * Get the standalone declaration from the xml declaration
     *
     * @return true if this is standalone, or false otherwise
     */
    public boolean isStandalone() {
        return false;
    }

    /**
     * Checks if standalone was set in the document
     *
     * @return true if standalone was set in the document, or false otherwise
     */
    public boolean standaloneSet() {
        return false;
    }

    /**
     * Returns the character encoding declared on the xml declaration
     * Returns null if none was declared
     *
     * @return the encoding declared in the document or null
     */
    public String getCharacterEncodingScheme() {
        return null;
    }

    /**
     * Get the target of a processing instruction
     *
     * @return the target or null
     */
    public String getPITarget() {
        return ((ProcessingInstruction) current).getTarget();
    }

    /**
     * Get the data section of a processing instruction
     *
     * @return the data or null
     */
    public String getPIData() {
        return ((ProcessingInstruction) current).getData();
    }
}
