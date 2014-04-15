package de.hpi.krestel.mySearchEngine.processing;

import java.util.List;

public interface ProcessorInterface {

    public List<String> process(String input);
    public List<String> process(List<String> input);

}
