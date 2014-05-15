package de.hpi.krestel.mySearchEngine.processing.tokenization;

import de.hpi.krestel.mySearchEngine.processing.Processor;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import org.apache.commons.collections.IteratorUtils;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class StanfordTokenizeProcessor extends Processor {

	final String tokenizerOptions = "normalizeParentheses=false,tokenizeNLs=false,normalizeAmpersandEntity=true," +
			"normalizeFractions=true,normalizeOtherBrackets=false,asciiQuotes=true,untokenizable=noneDelete,ptb3Dashes=false";

	@Override
	public List<CoreLabel> process(List<CoreLabel> input) {
		String inputText = input.get(0).value();
		PTBTokenizer tokenizer = new PTBTokenizer(new StringReader(input.get(0).value()), new CoreLabelTokenFactory(), tokenizerOptions);
		List<CoreLabel> tokens = IteratorUtils.toList(tokenizer);

		int offset = 0;
		int lastEnd = 0;
		for (CoreLabel token : tokens) {
			try {
//				int length = token.originalText().getBytes("UTF-8").length; // originalText does not always give the original text :(
				token.setOriginalText(inputText.substring(token.beginPosition(), token.endPosition()));
				int length = token.originalText().getBytes("UTF-8").length;
				int difference = token.beginPosition() - lastEnd;
//				int difference = inputText.substring(lastEnd, token.beginPosition()).getBytes().length;
				lastEnd = token.endPosition();
				offset += difference;
//				System.out.println("Token: " + token.originalText() + ", Length: " + length + ", LastEnd: " + lastEnd + ", Now: " + token.beginPosition() + ", Offset: " + offset);
				token.setBeginPosition(offset);
				token.setEndPosition(offset + length);
//				System.out.println("Input: >" + new String(inputBytes, offset, length) + "<, Text: >" + token.originalText() + "<");
				offset += length;
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return tokens;
	}
}
