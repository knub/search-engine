package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;

public class PunctuationProcessor extends AbstractEachElementProcessor {
    @Override
    public String handleItem(String item) {
        String intermediate = item.replaceAll("(^[.,]+)|([.,]+$)", "").trim();

        if ( ! intermediate.isEmpty()) {
            return intermediate;
        }

        return null;
    }
}
