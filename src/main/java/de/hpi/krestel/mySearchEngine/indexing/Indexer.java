package de.hpi.krestel.mySearchEngine.indexing;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.processing.normalization.LowerCaseProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.StoppingProcessor;
import de.hpi.krestel.mySearchEngine.processing.stemming.GermanStemmingProcessor;
import de.hpi.krestel.mySearchEngine.processing.tokenization.StanfordTokenizeProcessor;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import java.util.List;
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


		Pipeline pipeline = new Pipeline();
		pipeline.add(new LowerCaseProcessor());
		pipeline.add(new StanfordTokenizeProcessor());
		pipeline.add(new StoppingProcessor());
//		pipeline.add(new PunctuationProcessor());
        pipeline.add(new GermanStemmingProcessor());

        List<String> results = pipeline.process(text);
        for (String result : results) {
            System.out.println(result);
        }

//		try {
//			AbstractWordSplitter splitter = new GermanWordSplitter();
//			splitter.setStrictMode(true);
////			System.out.println(splitter.splitWord("Donaudampfschifffahrtskapitänsmützenständer"));
////			System.out.println(splitter.splitWord("Hasenhaus"));
////			System.out.println(splitter.splitWord("Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz"));
//		} catch (Exception e) {}
		System.exit(0);
	}
}
