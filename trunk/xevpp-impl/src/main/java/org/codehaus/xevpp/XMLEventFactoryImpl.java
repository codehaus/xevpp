package org.codehaus.xevpp;

import org.codehaus.xevpp.events.*;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.Location;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.events.*;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 22:30:57
 */
public class XMLEventFactoryImpl extends XMLEventFactory implements Location {
    /**
     * Create a new instance of the factory.
     * @return The new instance
     * @throws javax.xml.stream.FactoryConfigurationError if an instance of this factory cannot be loaded
     */
    public static XMLEventFactory newInstance() throws FactoryConfigurationError {
        return XMLEventFactory.newInstance(XMLEventFactoryImpl.class.getName(),
                XMLEventFactoryImpl.class.getClassLoader());
    }

    private int lineNumber = -1;
    private int columnNumber = -1;
    private int characterOffset = 0;

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getCharacterOffset() {
        return characterOffset;
    }

    public String getPublicId() {
        return "";
    }

    public String getSystemId() {
        return "";
    }

    public void setLocation(Location location) {
        lineNumber = location.getLineNumber();
        columnNumber = location.getColumnNumber();
        characterOffset = location.getCharacterOffset();
    }

    public Attribute createAttribute(String prefix, String namespaceUri, String localName, String value) {
        final AttributeImpl attribute = new AttributeImpl(prefix, namespaceUri, localName, value);
        attribute.setLocation(this);
        return attribute;
    }

    public Attribute createAttribute(String localName, String value) {
        final AttributeImpl attribute = new AttributeImpl(localName, value);
        attribute.setLocation(this);
        return attribute;
    }

    public Attribute createAttribute(QName name, String value) {
        final AttributeImpl attribute = new AttributeImpl(name, value);
        attribute.setLocation(this);
        return attribute;
    }

    public Namespace createNamespace(String namespaceUri) {
        final NamespaceImpl namespace = new NamespaceImpl(namespaceUri);
        namespace.setLocation(this);
        return namespace;
    }

    public Namespace createNamespace(String prefix, String namespaceUri) {
        final NamespaceImpl namespace = new NamespaceImpl(prefix, namespaceUri);
        namespace.setLocation(this);
        return namespace;
    }

    public StartElement createStartElement(QName name, Iterator attributes, Iterator namespaces) {
        final StartElementImpl startElement = new StartElementImpl(name, attributes, namespaces);
        startElement.setLocation(this);
        return startElement;
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName) {
        final StartElementImpl startElement = new StartElementImpl(prefix, namespaceUri, localName);
        startElement.setLocation(this);
        return startElement;
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces) {
        final StartElementImpl startElement = new StartElementImpl(prefix, namespaceUri, localName, attributes, namespaces);
        startElement.setLocation(this);
        return startElement;
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) {
        final StartElementImpl startElement = new StartElementImpl(prefix, namespaceUri, localName, attributes, namespaces, context);
        startElement.setLocation(this);
        return startElement;
    }

    public EndElement createEndElement(QName name, Iterator namespaces) {
        final EndElementImpl endElement = new EndElementImpl(name, namespaces);
        endElement.setLocation(this);
        return endElement;
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName) {
        final EndElementImpl endElement = new EndElementImpl(prefix, namespaceUri, localName,
                Collections.emptySet().iterator());
        endElement.setLocation(this);
        return endElement;
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName, Iterator namespaces) {
        final EndElementImpl endElement = new EndElementImpl(prefix, namespaceUri, localName, namespaces);
        endElement.setLocation(this);
        return endElement;
    }

    public Characters createCharacters(String content) {
        final CharactersImpl characters = new CharactersImpl(content, false);
        characters.setLocation(this);
        return characters;
    }

    public Characters createCData(String content) {
        final CharactersImpl characters = new CharactersImpl(content, true);
        characters.setLocation(this);
        return characters;
    }

    public Characters createSpace(String content) {
        final CharactersImpl characters = new CharactersImpl(content, false, true, false);
        characters.setLocation(this);
        return characters;
    }

    public Characters createIgnorableSpace(String content) {
        final CharactersImpl characters = new CharactersImpl(content, false, true, true);
        characters.setLocation(this);
        return characters;
    }

    public StartDocument createStartDocument() {
        final StartDocumentImpl startDocument = new StartDocumentImpl();
        startDocument.setLocation(this);
        return startDocument;
    }

    public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        final StartDocumentImpl startDocument = new StartDocumentImpl(encoding, version, standalone);
        startDocument.setLocation(this);
        return startDocument;
    }

    public StartDocument createStartDocument(String encoding, String version) {
        final StartDocumentImpl startDocument = new StartDocumentImpl(encoding, version);
        startDocument.setLocation(this);
        return startDocument;
    }

    public StartDocument createStartDocument(String encoding) {
        final StartDocumentImpl startDocument = new StartDocumentImpl(encoding);
        startDocument.setLocation(this);
        return startDocument;
    }

    public EndDocument createEndDocument() {
        final EndDocumentImpl endDocument = new EndDocumentImpl();
        endDocument.setLocation(this);
        return endDocument;
    }

    public EntityReference createEntityReference(String name, EntityDeclaration declaration) {
        throw new UnsupportedOperationException("todo");
    }

    public Comment createComment(String text) {
        final CommentImpl comment = new CommentImpl(text);
        comment.setLocation(this);
        return comment;
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        throw new UnsupportedOperationException("todo");
    }

    public DTD createDTD(String dtd) {
        throw new UnsupportedOperationException("todo");
    }
}
