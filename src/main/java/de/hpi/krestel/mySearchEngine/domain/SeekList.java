package de.hpi.krestel.mySearchEngine.domain;

import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> {
    //TODO: PLZ write me incrementally to a file, read me from there

    private Documents documents;

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }


}
