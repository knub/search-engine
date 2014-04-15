package de.hpi.krestel.mySearchEngine.processing;

import java.util.List;
import java.util.ArrayList;

abstract public class AbstractProcessor implements ProcessorInterface {
    @Override
    public List<String> process(String input) {
        List<String> arrayInput = new ArrayList<String>();
        arrayInput.add(input);
        return process(arrayInput);
    }
}
