package de.hpi.krestel.mySearchEngine.processing.stemming;

import de.hpi.krestel.mySearchEngine.processing.AbstractEachElementProcessor;
import edu.stanford.nlp.ling.CoreLabel;
import org.tartarus.snowball.ext.germanStemmer;

public class GermanStemmingProcessor extends AbstractEachElementProcessor {
    private germanStemmer stemmer;

    public GermanStemmingProcessor() {
        stemmer = new germanStemmer();
    }

    @Override
    public CoreLabel handleItem(final CoreLabel item) {
        stemmer.setCurrent(item.value());
        if (!stemmer.stem()) {
            throw new RuntimeException("Stemming not successful for " + item);
        }

	    return new CoreLabel() {{
		    setValue(stemmer.getCurrent());
		    setBeginPosition(item.beginPosition());
		    setEndPosition(item.endPosition());
		    setOriginalText(item.originalText());
	    }};
    }
}
