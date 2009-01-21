package org.codehaus.xevpp;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 21:37:57
 */
final class SecurityHelper {

    private SecurityHelper() {
    }

    static ClassLoader getContextClassLoader() throws SecurityException {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ClassLoader classLoader = null;
                try {
                    classLoader = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                    // ignore
                }

                if (classLoader == null)
                    classLoader = ClassLoader.getSystemClassLoader();

                return classLoader;
            }
        });
    }


}
