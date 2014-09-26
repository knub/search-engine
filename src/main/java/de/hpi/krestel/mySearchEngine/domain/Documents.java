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
    private boolean writeFileMode = false;
    // private FileWrite fileWriter;

    static public Documents readFromFile(String filename)
    {
        Documents documents = new Documents();

        //TODO open FileReader and parse each line
        // FileReader fileReader = new fileReader(filename)
        // while (line = fileReader .nextLine()) {
        //      length, title = parse(line)
        //      add(title, length)
        // }
        return documents;
    }

    public Documents() {
        // for reading mode
        titles = new ArrayList<String>();
        lengths = new TIntArrayList();
        cumulatedLength = 0;
    }

    public Documents(String filename) {
        // for inserting mode
        writeFileMode = true;
        // TODO: open new fileWriter(filename)
    }

    public void add(String title, int length)
    {
        //TODO: PLZ write me incrementally to a file PLZ!
        if(writeFileMode)
        {
            //TODO: implement me
            // something like:
            // fileWriter.write("" + length + " " + title)
        }
        else {
            titles.add(title);
            lengths.add(length);
            cumulatedLength += length;
        }
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
