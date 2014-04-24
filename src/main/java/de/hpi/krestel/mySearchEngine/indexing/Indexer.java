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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer implements TextCompletedListener {

	private final WriteToPlainTextFileProcessor plainTextWriter = new WriteToPlainTextFileProcessor();
	private final LowerCaseProcessor lowerCaseProcessor = new LowerCaseProcessor();
	private final StanfordTokenizeProcessor tokenizerProcessor = new StanfordTokenizeProcessor();
	private final StoppingProcessor stopwordProcessor = new StoppingProcessor();
	private final CompoundWordSplitProcessor compoundWordSplitProcessor = new CompoundWordSplitProcessor();
	private final GermanStemmingProcessor stemmingProcessor = new GermanStemmingProcessor();

	public Indexer(String directory) {
	}

	public void run() {
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);
		reader.readWikiFile();
		plainTextWriter.flush();
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
		pipeline.add(plainTextWriter);
//        pipeline.add(new PrintProcessor("Start"));
		pipeline.add(lowerCaseProcessor);
//        pipeline.add(new PrintProcessor("LowerCase"));
		pipeline.add(tokenizerProcessor);
//        pipeline.add(new PrintProcessor("Tokenize"));
		pipeline.add(stopwordProcessor);
//        pipeline.add(new PrintProcessor("Stopping"));
        pipeline.add(compoundWordSplitProcessor);
//        pipeline.add(new PrintProcessor("CompoundWord"));
        pipeline.add(stemmingProcessor);
        pipeline.add(new PrintProcessor("Stemming"));

        pipeline.process(text);
	}
}
