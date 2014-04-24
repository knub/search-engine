package de.hpi.krestel.mySearchEngine.processing;

import de.hpi.krestel.mySearchEngine.processing.normalization.CompoundWordSplitProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.LowerCaseProcessor;
import de.hpi.krestel.mySearchEngine.processing.normalization.StoppingProcessor;
import de.hpi.krestel.mySearchEngine.processing.stemming.GermanStemmingProcessor;
import de.hpi.krestel.mySearchEngine.processing.tokenization.StanfordTokenizeProcessor;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pipeline extends Vector<ProcessorInterface> {

	private final WriteToPlainTextFileProcessor plainTextWriter = new WriteToPlainTextFileProcessor();
	private final LowerCaseProcessor lowerCaseProcessor = new LowerCaseProcessor();
	private final StanfordTokenizeProcessor tokenizerProcessor = new StanfordTokenizeProcessor();
	private final StoppingProcessor stopwordProcessor = new StoppingProcessor();
	private final CompoundWordSplitProcessor compoundWordSplitProcessor = new CompoundWordSplitProcessor();
	private final GermanStemmingProcessor stemmingProcessor = new GermanStemmingProcessor();

	public List<CoreLabel> start(String text) {
		text = text.replace("[[", "").replace("]]", "").replace("[", " ").replace("]", " ");
		text = text.replace("|", " ").replace("#", " ").replace("<!--", "").replace("-->", "").replace("&nbsp;", " ");
		Matcher matcher = Pattern.compile("<.*?>").matcher(text);
//		while (matcher.find())
//			System.out.println(matcher.group());
		text = matcher.replaceAll(" ");

		Pipeline pipeline = new Pipeline();
		pipeline.add(plainTextWriter);
		pipeline.add(lowerCaseProcessor);
		pipeline.add(tokenizerProcessor);
		pipeline.add(stopwordProcessor);
		pipeline.add(compoundWordSplitProcessor);
		pipeline.add(stemmingProcessor);
		pipeline.add(new PrintProcessor("Stemming"));

		return pipeline.process(text);
	}

	public void finished() {
		plainTextWriter.flush();
	}


    private List<CoreLabel> process(final String input) {

	    CoreLabel label = new CoreLabel() {{
	        setValue(input);
        }};
        List<CoreLabel> list = Arrays.asList(label);

        for (ProcessorInterface processor : this) {
            list = processor.process(list);
        }

        return list;
    }
}
