package org.projectodd.stilts.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.projectodd.stilts.spi.Headers;

public class DefaultHeaders extends HashMap<String, String> implements Headers {

    private static final long serialVersionUID = 1L;

    @Override
    public String get(String headerName) {
        return super.get( headerName );
    }

    public void putAll(Headers headers) {
        for (String name : headers.getHeaderNames()) {
            put( name, headers.get( name ) );
        }
    }
    
    public void remove(String headerName) {
        super.remove( headerName );
    }

    @Override
    public Set<String> getHeaderNames() {
        return keySet();
    }

    @Override
    public Headers duplicate() {
        DefaultHeaders dupe = new DefaultHeaders();
        dupe.putAll(  (Map<String,String>) this  );
        return dupe;
    }

}