package org.codehaus.xevpp;

import org.codehaus.xevpp.events.CharactersImpl;
import org.codehaus.xevpp.events.CommentImpl;
import org.codehaus.xevpp.events.EndElementImpl;
import org.codehaus.xevpp.events.StartElementImpl;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 08-Aug-2008 21:14:57
 */
public class XMLEventReaderImpl
    implements XMLEventReader
{

    private static final String WHITESPACE_REGEX = "[ \n\r\t]+";

    private static final String NAME_START_CHAR_REGEX =
        "[" + ":" + "[A-Z]" + "[a-z]" + "[\u00c0-\u00d6]" + "[\u00d8-\u00f6]" + "[\u00f8-\u02ff]" + "[\u0370-\u037d]"
            + "[\u037f-\u1fff]" + "[\u200c-\u200d]" + "[\u2070-\u218f]" + "[\u2c00-\u2fef]" + "[\u3001-\ud7ff]"
            + "[\uf900-\ufdcf]" + "[\ufdf0-\ufffd]"+"[\u10000-\ueffff]"+"]";

    private static final String NAME_CHAR_REGEX =
        "[" + ":" + "\\-" + "\\." + "[0-9]" + "\u00b7" + "[\u0300-\u036f]" + "[\u203f-\u2040]" + "[A-Z]" + "[a-z]"
            + "[\u00c0-\u00d6]" + "[\u00d8-\u00f6]" + "[\u00f8-\u02ff]" + "[\u0370-\u037d]" + "[\u037f-\u1fff]"
            + "[\u200c-\u200d]" + "[\u2070-\u218f]" + "[\u2c00-\u2fef]" + "[\u3001-\ud7ff]" + "[\uf900-\ufdcf]"
            + "[\ufdf0-\ufffd]" + "[\u10000-\ueffff]" + "]";

    private static final String NAME_REGEX = NAME_START_CHAR_REGEX + NAME_CHAR_REGEX + "*";
    
    private static final String NAMES_REGEX = NAME_REGEX + "(" + WHITESPACE_REGEX + "+" + NAME_REGEX + ")+";

    private static final String NMTOKEN_REGEX = NAME_CHAR_REGEX + "+";

    private static final String NMTOKENS_REGEX = NMTOKEN_REGEX + "(" + WHITESPACE_REGEX + "+" + NMTOKEN_REGEX + ")+";
    
    private static final String ELEMENT_NAME_REGEX = "[a-zA-Z0-9-_]+";

    private static final String ATTRIBUTE_NAME_REGEX = "[a-zA-Z0-9-_]+";

    private StringBuffer buf = new StringBuffer();

    private XMLEvent next = null;

    private final Reader input;

    private boolean eof = false;

    private TokenMatcher[] matchers = {new CDataTokenMatcher(), new CommentTokenMatcher(), new WhitespaceTokenMatcher(),
        new StartElementTokenMatcher(), new EndElementTokenMatcher(),};

    private final Pattern prolog = Pattern.compile( "(\\Q<?xml\\E" + "(\\s+version\\s*=\\s*('1.0')|(\"1.0\"))"
        + "(\\s+encoding\\s*=\\s*('[a-zA-Z0-9\\-_]+')|(\"[a-zA-Z0-9\\-_]+\"))?"
        + "(\\s+standalone\\s*=\\s*('(yes)|(no)')|(\"(yes)|(no)\"))?" + "\\s*\\Q?>\\E)?" );

    private final Pattern pi = Pattern.compile(
        "(\\Q<?\\E(" + "([^xX][^\\s]*\\s)|" + "([xX][^mM][^\\s]*\\s)|" + "([xX][mM][^lL]\\s)|"
            + "([xX][mM][lL][^\\s]+\\s))" + "\\s*(([^\\?]*)|(\\?[^>]))*\\Q?>\\E)" );

    private final Pattern comment = Pattern.compile( "(\\Q<!--\\E" + "(([^-]*)|(-[^-]*))*" + "\\Q-->\\E)" );

    private final Pattern startElement = Pattern.compile(
        "<\\s*(" + ELEMENT_NAME_REGEX + ")(\\s+(" + ATTRIBUTE_NAME_REGEX
            + ")\\s*=\\s*(\"([^\"]*)\"|\'([^\']*)\'))*(/?)\\s*>" );

    private final Pattern endElement = Pattern.compile( "<\\s*/\\s*(" + ELEMENT_NAME_REGEX + ")\\s*>" );

    public XMLEventReaderImpl( Reader input )
    {
        this.input = input;
    }

    private static interface TokenMatcher
    {
        int haveMatch( CharSequence buf );

        XMLEvent parse( CharSequence buf );

        public static final int NEED_MORE_CHARACTERS = 0;

        public static final int NO_MATCH = Integer.MIN_VALUE;


    }

    private static class CommentTokenMatcher
        implements TokenMatcher
    {

        public int haveMatch( CharSequence buf )
        {
            if ( buf.length() < 7 )
            {
                // too soon
                return NEED_MORE_CHARACTERS;
            }
            if ( !buf.subSequence( 0, 4 ).equals( "<!--" ) )
            {
                // definately a miss
                return NO_MATCH;
            }
            char c1 = buf.charAt( 4 ), c2 = buf.charAt( 5 ), c3 = buf.charAt( 6 );
            int i = 6;
            while ( true )
            {
                if ( c1 == '-' && c2 == '-' && c3 == '>' )
                {
                    return i;
                }
                i++;
                if ( i >= buf.length() )
                {
                    return NEED_MORE_CHARACTERS;
                }
                c1 = c2;
                c2 = c3;
                c3 = buf.charAt( i );
            }
        }

        public XMLEvent parse( CharSequence buf )
        {
            assert buf.subSequence( 0, 4 ).equals( "<!--" );
            assert buf.subSequence( buf.length() - 3, buf.length() ).equals( "-->" );
            return new CommentImpl( buf.subSequence( 4, buf.length() - 3 ).toString() );
        }
    }

    private static class CDataTokenMatcher
        implements TokenMatcher
    {

        public int haveMatch( CharSequence buf )
        {
            if ( buf.length() < 12 )
            {
                // too soon
                return NEED_MORE_CHARACTERS;
            }
            if ( !buf.subSequence( 0, 9 ).equals( "<![CDATA[" ) )
            {
                // definately a miss
                return NO_MATCH;
            }
            char c1 = buf.charAt( 9 ), c2 = buf.charAt( 10 ), c3 = buf.charAt( 11 );
            int i = 11;
            while ( true )
            {
                if ( c1 == ']' && c2 == ']' && c3 == '>' )
                {
                    return i;
                }
                i++;
                if ( i >= buf.length() )
                {
                    return NEED_MORE_CHARACTERS;
                }
                c1 = c2;
                c2 = c3;
                c3 = buf.charAt( i );
            }
        }

        public XMLEvent parse( CharSequence buf )
        {
            assert buf.subSequence( 0, 9 ).equals( "<[CDATA[" );
            assert buf.subSequence( buf.length() - 3, buf.length() ).equals( "]]>" );
            return new CharactersImpl( buf.subSequence( 9, buf.length() - 3 ).toString(), true );
        }
    }

    private static class WhitespaceTokenMatcher
        implements TokenMatcher
    {

        public int haveMatch( CharSequence buf )
        {
            if ( buf.length() < 1 )
            {
                // too soon
                return NEED_MORE_CHARACTERS;
            }
            int i = 0;
            while ( i < buf.length() && Character.isWhitespace( buf.charAt( i ) ) )
            {
                i++;
            }
            if ( i > 0 && i < buf.length() && !Character.isWhitespace( buf.charAt( i ) ) )
            {
                return i - 1;
            }
            if ( i == 0 )
            {
                // definately a miss
                return NO_MATCH;
            }
            return NEED_MORE_CHARACTERS;
        }

        public XMLEvent parse( CharSequence buf )
        {
            final String asString = buf.toString();
            assert asString.trim().length() == 0;
            return new CharactersImpl( asString, false, true, false );
        }
    }

    private static class EndElementTokenMatcher
        implements TokenMatcher
    {

        private final Pattern endElement = Pattern.compile( "<\\s*/\\s*(" + ELEMENT_NAME_REGEX + ")\\s*>" );

        public int haveMatch( CharSequence buf )
        {
            if ( buf.length() < 3 )
            {
                return NEED_MORE_CHARACTERS;
            }
            if ( buf.charAt( 0 ) != '<' )
            {
                // definately a miss
                return NO_MATCH;
            }
            int i = 1;
            while ( i < buf.length() && buf.charAt( i ) != '>' )
            {
                i++;
            }
            if ( i < buf.length() && buf.charAt( i ) == '>' )
            {
                return endElement.matcher( buf.subSequence( 0, i ).toString() ).matches() ? i : NO_MATCH;
            }
            return NEED_MORE_CHARACTERS;
        }

        public XMLEvent parse( CharSequence buf )
        {
            final Matcher matcher = endElement.matcher( buf.toString() );
            assert matcher.matches();
            return new EndElementImpl( null, null, matcher.group( 1 ), Collections.emptySet().iterator() );
        }
    }

    private static class StartElementTokenMatcher
        implements TokenMatcher
    {

        private final Pattern startElement = Pattern.compile(
            "<\\s*(" + ELEMENT_NAME_REGEX + ")(\\s+(" + ATTRIBUTE_NAME_REGEX
                + ")\\s*=\\s*(\"([^\"]*)\"|\'([^\']*)\'))*(/?)\\s*>" );

        public int haveMatch( CharSequence buf )
        {
            if ( buf.length() < 3 )
            {
                return NEED_MORE_CHARACTERS;
            }
            if ( buf.charAt( 0 ) != '<' )
            {
                // definately a miss
                return NO_MATCH;
            }
            int i = 1;
            while ( i < buf.length() && buf.charAt( i ) != '>' )
            {
                i++;
            }
            if ( i < buf.length() && buf.charAt( i ) == '>' )
            {
                return startElement.matcher( buf.subSequence( 0, i ).toString() ).matches() ? i : NO_MATCH;
            }
            return NEED_MORE_CHARACTERS;
        }

        public XMLEvent parse( CharSequence buf )
        {
            final Matcher matcher = startElement.matcher( buf.toString() );
            assert matcher.matches();
            return new StartElementImpl( null, null, matcher.group( 1 ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    public XMLEvent nextEvent()
        throws XMLStreamException
    {
        return (XMLEvent) next();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext()
    {
        if ( next != null )
        {
            return true;
        }
        if ( eof && buf.length() == 0 )
        {
            return false;
        }
        Set matchers = new HashSet( this.matchers.length );
        matchers.addAll( Arrays.asList( this.matchers ) );
        if ( buf.length() > 0 ) {
            for ( Iterator i = matchers.iterator(); i.hasNext(); )
            {
                TokenMatcher matcher = (TokenMatcher) i.next();
                int matchLen = matcher.haveMatch( buf );
                if ( matchLen < 0 )
                {
                    i.remove();
                }
                if ( matchLen > 0 )
                {
                    next = matcher.parse( buf.subSequence( 0, matchLen ) );
                    buf.delete( 0, matchLen );
                    return true;
                }
            }
        }
        if (eof) {
            return false;
        }
        char[] chunk = new char[80];

        int i;
        try
        {
            while ( -1 != ( i = input.read( chunk ) ) && !matchers.isEmpty())
            {
                buf.append( chunk, 0, i );
                for ( Iterator j = matchers.iterator(); j.hasNext(); )
                {
                    TokenMatcher matcher = (TokenMatcher) j.next();
                    int matchLen = matcher.haveMatch( buf );
                    if ( matchLen < 0 )
                    {
                        j.remove();
                    }
                    if ( matchLen > 0 )
                    {
                        next = matcher.parse( buf.subSequence( 0, matchLen ) );
                        buf.delete( 0, matchLen );
                        return true;
                    }
                }
            }
        }
        catch ( IOException e )
        {
            eof = true;
            return hasNext();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Object next()
    {
        do
        {
            if ( next != null )
            {
                try
                {
                    return next;
                }
                finally
                {
                    next = null;
                }
            }
        }
        while ( hasNext() );
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        throw new UnsupportedOperationException( "todo" );
    }

    /**
     * {@inheritDoc}
     */
    public XMLEvent peek()
        throws XMLStreamException
    {
        do
        {
            if ( next != null )
            {
                return next;
            }
        }
        while ( hasNext() );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getElementText()
        throws XMLStreamException
    {
        throw new UnsupportedOperationException( "todo" );
    }

    /**
     * {@inheritDoc}
     */
    public XMLEvent nextTag()
        throws XMLStreamException
    {
        throw new UnsupportedOperationException( "todo" );
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty( String name )
        throws IllegalArgumentException
    {
        throw new UnsupportedOperationException( "todo" );
    }

    /**
     * {@inheritDoc}
     */
    public void close()
        throws XMLStreamException
    {
        try
        {
            input.close();
        }
        catch ( IOException e )
        {
            throw new XMLStreamException( e.getMessage(), e );
        }
    }
}
