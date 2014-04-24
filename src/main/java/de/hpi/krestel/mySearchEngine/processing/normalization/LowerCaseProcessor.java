package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;
import edu.stanford.nlp.ling.CoreLabel;

public class LowerCaseProcessor extends AbstractEachElementProcessor {
    @Override
    public CoreLabel handleItem(final CoreLabel item) {
	    return new CoreLabel() {{
		    setValue(item.value().toLowerCase());
		    setBeginPosition(item.beginPosition());
		    setEndPosition(item.endPosition());
	    }};
    }
}
