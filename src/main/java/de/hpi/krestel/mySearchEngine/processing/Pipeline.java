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

public class Pipeline extends Vector<Processor> {

    static public Pipeline createPreprocessingPipeline(String directory) {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new WriteToPlainTextFileProcessor(directory));
        pipeline.add(new LowerCaseProcessor());
        pipeline.add(new StanfordTokenizeProcessor());
        pipeline.add(new StoppingProcessor());
        pipeline.add(new CompoundWordSplitProcessor());
        pipeline.add(new GermanStemmingProcessor());
//		pipeline.add(new PrintProcessor("Stemming"));

        return pipeline;
    }

    static public Pipeline createSearchPipeline() {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new LowerCaseProcessor());
        pipeline.add(new StanfordTokenizeProcessor());
        pipeline.add(new StoppingProcessor());
        pipeline.add(new CompoundWordSplitProcessor());
        pipeline.add(new GermanStemmingProcessor());

        return pipeline;
    }

	public List<CoreLabel> start(String text) {
		text = text.replace("[[", "").replace("]]", "").replace("[", " ").replace("]", " ");
		text = text.replace("|", " ").replace("#", " ").replace("<!--", "").replace("-->", "").replace("&nbsp;", " ");
		Matcher matcher = Pattern.compile("<.*?>").matcher(text);
//		while (matcher.find())
//			System.out.println(matcher.group());
		text = matcher.replaceAll(" ");
		return this.process(text);
	}

	public String processForQuery(String queryToken) {
        List<CoreLabel> labels = start(queryToken);
        if (labels.size() == 0) {
            return null;
        } else {
            return labels.get(0).value();
        }
	}

	public String[] processPhraseForQuery(String queryToken) {
		List<CoreLabel> processedPhrase = start(queryToken);
		String[] phrase = new String[processedPhrase.size()];
		int i = 0;
		for (CoreLabel label : processedPhrase) {
            phrase[i++] = label.value();
        }
		return phrase;
	}

	public void finished() {
        for (Processor processor : this) {
            processor.finished();
        }
	}


    private List<CoreLabel> process(final String input) {

	    CoreLabel label = new CoreLabel() {{
	        setValue(input);
        }};
        List<CoreLabel> list = Arrays.asList(label);

        for (Processor processor : this) {
            list = processor.process(list);
        }

        return list;
    }

}
