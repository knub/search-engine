package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;
import edu.stanford.nlp.ling.CoreLabel;

public class PunctuationProcessor extends AbstractEachElementProcessor {

    @Override
    public CoreLabel handleItem(final CoreLabel item) {
        CoreLabel intermediate = new CoreLabel() {{
	        setValue(item.value().replaceAll("(^[.,]+)|([.,]+$)", "").trim());
	        setBeginPosition(item.beginPosition());
	        setEndPosition(item.endPosition());
        }};

        if (!intermediate.value().isEmpty()) {
            return intermediate;
        }
        return null;
    }
}
