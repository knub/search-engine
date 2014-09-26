package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Documents implements Serializable {
    // this class stores a list of titles and lengths, each for one document
    private List<String> titles;
    private TIntList lengths;
    private long cumulatedLength;

    public Documents() {
        titles = new ArrayList<String>();
        lengths = new TIntArrayList();
        cumulatedLength = 0;
    }

    public void add(String title, int length)
    {
        //TODO: PLZ write me incrementally to a file PLZ!
        titles.add(title);
        lengths.add(length);
        cumulatedLength += length;
    }

    public String getTitle(int id)
    {
        return titles.get(id);
    }

    public int getLength(int id)
    {
        return lengths.get(id);
    }

    public long getCumulatedLength()
    {
        return cumulatedLength;
    }

    public int getCount()
    {
        return lengths.size();
    }

    public long getAverageLength()
    {
        return cumulatedLength / getCount();
    }
}
