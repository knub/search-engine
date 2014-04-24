package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Pipeline extends Vector<ProcessorInterface> {
    public List<CoreLabel> process(final String input) {

	    CoreLabel label = new CoreLabel() {{
	        setValue(input);
        }};
        List<CoreLabel> list = Arrays.asList(label);

        for (ProcessorInterface processor : this) {
            list = processor.process(list);
        }

        return list;
    }
}
