package de.hpi.krestel.mySearchEngine.processing.normalization;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;
import de.hpi.krestel.mySearchEngine.processing.AbstractProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompoundWordSplitProcessor extends AbstractProcessor {

	@Override
	public List<String> process(List<String> input) {
		try {
			AbstractWordSplitter splitter = new GermanWordSplitter();
			splitter.setStrictMode(true);
			List<String> elements = new ArrayList<String>();
			for (String el : input) {
				Collection<String> splitWordResult = splitter.splitWord(el);
				System.out.println(el + "-->" + splitWordResult);
				elements.addAll(splitWordResult);
			}
			return elements;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
