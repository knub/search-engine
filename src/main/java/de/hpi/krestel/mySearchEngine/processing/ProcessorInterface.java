package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public interface ProcessorInterface {
    public List<CoreLabel> process(List<CoreLabel> input);
}
