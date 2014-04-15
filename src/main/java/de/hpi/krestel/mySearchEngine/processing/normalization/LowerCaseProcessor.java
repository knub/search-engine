package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.hpi.krestel.mySearchEngine.processing.AbstractProcessor;

import java.util.ArrayList;
import java.util.List;

public class LowerCaseProcessor extends AbstractProcessor {
    @Override
    public List<String> process(List<String> input) {
        List<String> output = new ArrayList<String>();
        for (String item : input) {
            output.add(item.toLowerCase());
        }
        return output;
    }
}
