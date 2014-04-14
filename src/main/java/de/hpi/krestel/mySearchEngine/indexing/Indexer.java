package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		text = text.replace("[[", "").replace("]]", "").replace("[", " ").replace("]", " ");
		text = text.replace("|", " ").replace("#", " ").replace("<!--", "").replace("-->", "").replace("&nbsp;", " ");
		Matcher matcher = Pattern.compile("<.*?>").matcher(text);
//		while (matcher.find())
//			System.out.println(matcher.group());
		text = matcher.replaceAll(" ");

		System.out.println(text);
	}
}
