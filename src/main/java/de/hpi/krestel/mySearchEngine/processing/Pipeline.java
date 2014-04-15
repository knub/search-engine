package de.hpi.krestel.mySearchEngine.processing;

import java.util.ArrayList;
import java.util.Vector;

public class Pipeline extends Vector<ProcessorInterface> {
    public ArrayList<String> process(String input) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(input);

        for (ProcessorInterface processor : this) {
            list = processor.process(list);
        }

        return list;
    }
}
