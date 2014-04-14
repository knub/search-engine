package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;

public class Indexer implements TextCompletedListener {

	public Indexer(String directory) {

	}

	public void run() {
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);
		reader.readWikiFile();
	}

	@Override
	public void onTextCompleted(String text) {
		System.out.println(text);
	}
}
