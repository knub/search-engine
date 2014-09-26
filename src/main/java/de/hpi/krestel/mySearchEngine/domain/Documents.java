package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Documents implements Serializable {
    // this class stores a list of titles and lengths, each for one document
    private List<String> titles;
    private TIntList lengths;
    private long cumulatedLength;
    private boolean writeFileMode = false;
    private FileWriter fileWriter;
    private int count = 0;

    static public Documents readFromFile(String filename)
    {
        Documents documents = new Documents();
        FileReader fileReader;

        //open file
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open documents file");
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // read each line; parse length and title; add them to lists
        String line;
        String[] splitted;
        int length;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                // line has format "12length34 this is da title"
                splitted = line.split(" ", 2);
                documents.add(splitted[1], Integer.valueOf(splitted[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot read from documents file");
        }

        // close file
        try {
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Cannot close documents file... anyway.");
        }

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

        // open writer
        try {
            fileWriter = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open documents file");
        }
    }

    public void add(String title, int length)
    {
        count += 1;
        if(writeFileMode) {
            // format line to "12length34 this is the title"
            try {
                fileWriter.write("" + length + " " + title);
                fileWriter.append(System.getProperty("line.separator"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("cannot write to documents file");
            }
        } else {
            titles.add(title);
            lengths.add(length);
            cumulatedLength += length;
        }
    }

    public void finalize()
    {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Cannot close documents file... anyway.");
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
        return count;
    }

    public long getAverageLength()
    {
        return cumulatedLength / getCount();
    }
}
