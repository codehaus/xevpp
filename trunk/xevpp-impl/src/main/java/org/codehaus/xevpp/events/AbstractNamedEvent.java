package org.codehaus.xevpp.events;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 23:41:06
 */
public abstract class AbstractNamedEvent extends XMLEventImpl {
    protected final QName name;

    public AbstractNamedEvent(QName name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return name;
    }

    
}
