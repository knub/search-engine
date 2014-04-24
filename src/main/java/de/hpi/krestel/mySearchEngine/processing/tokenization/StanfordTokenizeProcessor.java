package de.hpi.krestel.mySearchEngine.processing.tokenization;

import de.hpi.krestel.mySearchEngine.processing.ProcessorInterface;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import org.apache.commons.collections.IteratorUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StanfordTokenizeProcessor implements ProcessorInterface {

	final String tokenizerOptions = "normalizeParentheses=false,tokenizeNLs=false,normalizeAmpersandEntity=true," +
			"normalizeFractions=true,normalizeOtherBrackets=false,asciiQuotes=true,untokenizable=noneDelete";

	@Override
	public List<CoreLabel> process(List<CoreLabel> input) {
		PTBTokenizer tokenizer = new PTBTokenizer(new StringReader(input.get(0).value()), new CoreLabelTokenFactory(), tokenizerOptions);
		return IteratorUtils.toList(tokenizer);
	}
}
