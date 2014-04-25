package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;
import de.hpi.krestel.mySearchEngine.processing.Processor;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompoundWordSplitProcessor extends Processor {

	private final GermanWordSplitter splitter;

	public CompoundWordSplitProcessor() {
		try {
			splitter = new GermanWordSplitter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		splitter.setStrictMode(true);
	}
	@Override
	public List<CoreLabel> process(List<CoreLabel> input) {
		try {
			List<CoreLabel> elements = new ArrayList<CoreLabel>();
			for (final CoreLabel el : input) {
				final Collection<String> splitWordResult = splitter.splitWord(el.value());
				for (final String splitWord : splitWordResult)
					elements.add(new CoreLabel() {{
						setValue(splitWord);
						setBeginPosition(el.beginPosition());
						setEndPosition(el.endPosition());
					}});
			}
			return elements;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
