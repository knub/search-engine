package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class DocumentEntry {
    public TIntList position = new TIntArrayList();
    public TIntList offset = new TIntArrayList();
    public TIntList length = new TIntArrayList();
}
