package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;

public class LowerCaseProcessor extends AbstractEachElementProcessor {
    @Override
    public String handleItem(String item) {
        return item.toLowerCase();
    }
}
