package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SeekList
{
    private List<String> wordVector;
    private TLongList offsetVector;

    public static SeekList createFromFile(String filename) {
        SeekList seekList = new SeekList();
        FileReader fileReader;

        //open file
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            System.out.println("cannot open seekList file - it probably does not exist");
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // read each line; parse offest and word; add them to map
        String line;
        String[] splitted;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if(line.length() == 0) continue;
                // line has format "12offset34 word"
                try {
                    splitted = line.split(" ", 2);
                    seekList.put(splitted[1], Long.valueOf(splitted[0]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print("splitting failed for: ");
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("cannot read from seekList file");
            return null;
        }

        // close file
        try {
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Cannot close seekList file... anyway.");
        }

        return seekList;
    }


    public SeekList()
    {
        this.wordVector = new ArrayList<String>();
        this.offsetVector = new TLongArrayList();
    }

    public void put(String word, long offset)
    {
        this.wordVector.add(word);
        this.offsetVector.add(offset);
    }

    public long getOffsetFor(String word)
    {
        int index = this.findNearestWordIndex(word);
        return offsetVector.get(index);
    }

    private int findNearestWordIndex(String word)
    {
        int lower = 0;
        int upper = wordVector.size();
        int current;
        String cur_word;
        int compared;
        // binary search
        do {
            current = (upper - lower) / 2 + lower;
            cur_word = wordVector.get(current);
            compared = word.compareTo(cur_word);
            if (compared == 0) {
                return current;
            } else if (compared < 0) {
                upper = current - 1;
            } else {
                lower = current;
            }
        } while ((upper - lower) > 1);
        return lower;
    }
}
