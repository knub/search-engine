package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.processing.PrintProcessor;
import de.hpi.krestel.mySearchEngine.processing.WriteToPlainTextFileProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.CompoundWordSplitProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.LowerCaseProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.StoppingProcessor;
import de.hpi.krestel.mySearchEngine.processing.stemming.GermanStemmingProcessor;
import de.hpi.krestel.mySearchEngine.processing.tokenization.StanfordTokenizeProcessor;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer implements TextCompletedListener {

	Pipeline preprocessingPipeline = new Pipeline();
	public Indexer(String directory) {
	}

	public void run() {
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);
		reader.readWikiFile();
		preprocessingPipeline.finished();
	}

	@Override
	public void onTextCompleted(String text) {
		List<CoreLabel> labels = preprocessingPipeline.start(text);
		indexText(labels);
	}

	public void indexText(List<CoreLabel> labels) {
		// TODO
	}
}
