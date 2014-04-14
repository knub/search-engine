package de.hpi.krestel.mySearchEngine.indexing;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import org.tartarus.snowball.ext.germanStemmer;

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

		try {
			AbstractWordSplitter splitter = new GermanWordSplitter();
			splitter.setStrictMode(true);
			System.out.println(splitter.splitWord("Donaudampfschifffahrtskapitänsmützenständer"));
			System.out.println(splitter.splitWord("Hasenhaus"));
			System.out.println(splitter.splitWord("Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz"));
		} catch (Exception e) {}

//		System.out.println(text);
	}
}
