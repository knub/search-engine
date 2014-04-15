package de.hpi.krestel.mySearchEngine.processing;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Pipeline extends Vector<ProcessorInterface> {
    public List<String> process(String input) {
        List<String> list = Arrays.asList(input);

        for (ProcessorInterface processor : this) {
            list = processor.process(list);
        }

        return list;
    }
}
