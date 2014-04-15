package de.hpi.krestel.mySearchEngine.processing;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractEachElementProcessor extends AbstractProcessor {
    @Override
    public List<String> process(List<String> input) {
        List<String> output = new ArrayList<String>();
        for (String item : input) {
            output.add(handleItem(item));
        }
        return output;
    }

    public abstract String handleItem(String item);
}
