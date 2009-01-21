package org.codehaus.xevpp.events;

import javax.xml.stream.events.Namespace;
import javax.xml.XMLConstants;

/**
 * Implementation of {@linkplain Namespace}
 *
 * @author Stephen Connolly
 * @since 0.0.1
 */
public class NamespaceImpl extends AttributeImpl implements Namespace {
    public NamespaceImpl(String namespaceURI) {
        super(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI, "xmlns", namespaceURI);
    }

    public NamespaceImpl(String prefix, String namespaceURI) {
        super("xmlns", XMLConstants.NULL_NS_URI, prefix, namespaceURI);
    }

    public int getEventType() {
        return NAMESPACE;
    }

    public boolean isNamespace() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefix() {
        return getName().getPrefix();
    }

    /**
     * {@inheritDoc}
     */
    public String getNamespaceURI() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDefaultNamespaceDeclaration() {
        return XMLConstants.DEFAULT_NS_PREFIX.equals(getPrefix());
    }
}
