package de.hpi.krestel.mySearchEngine.domain;

import java.io.Serializable;
import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> implements Serializable
{
    //TODO: PLZ write me incrementally to a file, read me from there
    // see Documents

    // remove document handling from here; it should use own file
    private Documents documents;

    public Documents getDocuments()
    {
        return documents;
    }

    public void setDocuments(Documents documents)
    {
        this.documents = documents;
    }


}
