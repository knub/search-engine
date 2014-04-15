package de.hpi.krestel.mySearchEngine.processing.tokenization;

import de.hpi.krestel.mySearchEngine.processing.AbstractProcessor;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StanfordTokenizeProcessor extends AbstractProcessor {

	@Override
	public List<String> process(List<String> input) {
		List<String> tokens = new ArrayList<String>();
		String tokenizerOptions = "normalizeParentheses=false,tokenizeNLs=false,normalizeAmpersandEntity=true," +
				"normalizeFractions=true,normalizeOtherBrackets=false,asciiQuotes=true,untokenizable=allKeep";
		PTBTokenizer tokenizer = new PTBTokenizer(new StringReader(input.get(0)), new CoreLabelTokenFactory(), tokenizerOptions);

		for (CoreLabel label; tokenizer.hasNext(); ) {
			label = (CoreLabel) tokenizer.next();
			tokens.add(label.toString());
			System.out.println(label);
		}
		return tokens;
	}

}
