package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class DocumentEntry {
    public TIntList positions = new TIntArrayList();
    public TIntList offsets = new TIntArrayList();
    public TIntList lengths = new TIntArrayList();

    public DocumentEntry(int position, int offset, int length) {
        positions.add(position);
        offsets.add(offset);
        lengths.add(length);
    }
}
