package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.array.TIntArrayList;

public class DocumentEntry {
    public int document_id;
    public TIntArrayList position = new TIntArrayList();
    public TIntArrayList offset = new TIntArrayList();
    public TIntArrayList length = new TIntArrayList();
}
