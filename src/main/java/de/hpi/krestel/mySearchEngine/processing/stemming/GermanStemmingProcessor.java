package de.hpi.krestel.mySearchEngine.processing.stemming;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;
import org.tartarus.snowball.ext.germanStemmer;

public class GermanStemmingProcessor extends AbstractEachElementProcessor {
    private germanStemmer stemmer;

    public GermanStemmingProcessor() {
        stemmer = new germanStemmer();
    }

    @Override
    public String handleItem(String item) {
        stemmer.setCurrent(item);
        if ( ! stemmer.stem()) {
            throw new RuntimeException("Stemming not successful for " + item);
        }

        return stemmer.getCurrent();
    }
}
