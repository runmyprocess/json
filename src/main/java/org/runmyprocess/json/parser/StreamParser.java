package org.runmyprocess.json.parser;

import org.runmyprocess.json.JSON;
import org.runmyprocess.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * User: sgaide & dboulay
 * Date: 26/03/13
 * Time: 17:13
 */
public class StreamParser extends DefaultParser {

    private InputStreamReader reader;
    private char current;

    public StreamParser( InputStream stream, String charset, JSON.Context context, JSON.Factory factory, long maxSize ) throws UnsupportedEncodingException {
        super( null, context, factory, maxSize );
        setInputStreamReader(new InputStreamReader(stream, charset));
    }
    public StreamParser( InputStream stream, String charset, JSON.Context context, JSON.Factory factory) throws UnsupportedEncodingException {
        this(stream, charset, context, factory, 0);
    }
    public StreamParser( InputStream stream, String charset ) throws UnsupportedEncodingException {
        this( stream, charset, null, null );
    }
    public StreamParser( InputStream stream, String charset, long maxSize ) throws UnsupportedEncodingException {
        this( stream, charset, null, null, maxSize );
    }
    public StreamParser( InputStream stream, String charset, JSON.Context context )  throws UnsupportedEncodingException {
        this( stream, charset, context, null);
    }
    public StreamParser( InputStream stream, String charset, JSON.Factory factory )  throws UnsupportedEncodingException {
        this(stream, charset, null, factory);
    }
    public StreamParser( InputStream stream, String charset, JSON.Factory factory, long masSize )  throws UnsupportedEncodingException {
        this(stream, charset, null, factory, masSize);
    }

    @Override
    protected char current() {
        if( current == 0 ) {
            try {
                int iCurrent = getInputStreamReader().read();
                if( iCurrent >= 0 ) current = (char)iCurrent;
            } catch (IOException e) {
                throw new JSONException(e);
            }
        }
        return current;
    }

    @Override
    protected void incIndex() {
        super.incIndex();
        current = 0;
    }

    @Override
    protected String acceptStringInternal( char endString ) {
        StringBuilder str = new StringBuilder();
        char c;
        while ((c = current()) != endString ) {
            incIndex();
            if( c == JSON.ESCAPE ) c = getEscapedChar(endString);
            str.append(c);
        }
        return str.toString();
    }

    @Override
    protected boolean acceptKeyword() throws JSONException {
        StringBuilder str = new StringBuilder();
        char c;
        while (Character.isLowerCase(c = current())) {
            str.append(c);
            incIndex();
        }
        String kw = str.toString();
        if( kw.length()>0 ) {
            JSON.JSONKeyword keyword = JSON.JSONKeyword.valueOf(kw.toUpperCase());
            return getContext().accept(keyword.toObject());
        } else {
            return false;
        }
    }

    @Override
    protected String formatMessage( String message ) {
        return String.format( "%s at %d", message, getIndex());
    }

    protected InputStreamReader getInputStreamReader() {
        return this.reader;
    }
    protected void setInputStreamReader( InputStreamReader reader ) {
        this.reader = reader;
    }
}
