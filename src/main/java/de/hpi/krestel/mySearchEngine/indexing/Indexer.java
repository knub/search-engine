package de.hpi.krestel.mySearchEngine.indexing;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;
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
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer implements TextCompletedListener {

	private WriteToPlainTextFileProcessor plainTextWriter;

	public Indexer(String directory) {
		plainTextWriter = new WriteToPlainTextFileProcessor();
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
		pipeline.add(new LowerCaseProcessor());
//        pipeline.add(new PrintProcessor("LowerCase"));
		pipeline.add(new StanfordTokenizeProcessor());
//        pipeline.add(new PrintProcessor("Tokenize"));
		pipeline.add(new StoppingProcessor());
//        pipeline.add(new PrintProcessor("Stopping"));
        pipeline.add(new CompoundWordSplitProcessor());
//        pipeline.add(new PrintProcessor("CompoundWord"));
        pipeline.add(new GermanStemmingProcessor());
        pipeline.add(new PrintProcessor("Stemming"));

        pipeline.process(text);
	}
}
