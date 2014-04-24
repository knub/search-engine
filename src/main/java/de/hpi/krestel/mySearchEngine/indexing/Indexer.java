package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Indexer implements TextCompletedListener {

	private final Pipeline preprocessingPipeline = new Pipeline();
    private final Map<String, OccurrenceMap> partIndex = new HashMap<String, OccurrenceMap>();

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
