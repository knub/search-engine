package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractEachElementProcessor extends Processor {
    @Override
    public List<CoreLabel> process(List<CoreLabel> input) {
        List<CoreLabel> output = new ArrayList<CoreLabel>();
        for (CoreLabel item : input) {
            CoreLabel intermediate = handleItem(item);
            if (null != intermediate) {
                output.add(handleItem(item));
            }
        }
        return output;
    }

    public abstract CoreLabel handleItem(CoreLabel item);
}
