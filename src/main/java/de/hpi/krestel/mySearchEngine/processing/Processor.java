package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

abstract public class Processor {
    abstract public List<CoreLabel> process(List<CoreLabel> input);
    public void finished() {
        // Overwrite in subclass
    }
}
