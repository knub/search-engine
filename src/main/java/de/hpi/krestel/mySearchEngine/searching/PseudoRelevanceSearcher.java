package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.operators.RankedWord;
import de.hpi.krestel.mySearchEngine.searching.query.operators.Word;
import edu.stanford.nlp.ling.CoreLabel;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import org.javatuples.Pair;

import java.util.List;

public class PseudoRelevanceSearcher {
	SnippetReader snippetReader;
	ResultList resultList;
	int snippetSize;
	Pipeline pipeline = Pipeline.createSearchPipeline();
	TObjectIntMap<String> wordCount = new TObjectIntHashMap<String>();

	final int SNIPPET_WORD_COUNT = 3;


	public PseudoRelevanceSearcher(ResultList resultList, int snippetSize) {
		this.resultList = resultList;
		this.snippetSize = snippetSize;
		snippetReader = new SnippetReader(snippetSize);
		snippetReader.setStartFoundSequence("");
		snippetReader.setEndFoundSequence("");
	}

	public Operator buildNewSearchOperator() {
		wordCount.clear();
		// loop over the top results
		for (Pair<Integer, DocumentEntry> result : resultList) {
			int docId = result.getValue0();
			DocumentEntry docEntry = result.getValue1();
			// loop over all found words - maybe we should not do this loop and instead just pick one word
			for (int i = 0; i < docEntry.size(); i++) {
				String snippet = snippetReader.readSnippet(docId, docEntry.offsets.get(i), docEntry.lengths.get(i));
				List<CoreLabel> words = pipeline.start(snippet);
				// loop over all indices
				for (CoreLabel word : words) {
					String wordText = word.value();
					Integer count = wordCount.get(wordText);
					if (count == null)
						wordCount.put(wordText, 1);
					else
						wordCount.put(wordText, count + 1);
				}
			}
		}

		RankedWord rankedWord = new RankedWord();
		for (int i = 0; i < SNIPPET_WORD_COUNT; i++) {
			Pair<String, Integer> maxWord = getMaxKey();
			for (int j = 0; j < maxWord.getValue1(); j++)
				rankedWord.add(new Word(maxWord.getValue0()));
			wordCount.remove(maxWord.getValue0());
		}

		return rankedWord;
	}

	public Pair<String, Integer> getMaxKey() {
		String maxWord = "";
		int maxCount = -1;
		TObjectIntIterator<String> it = wordCount.iterator();
		while (it.hasNext()) {
			it.advance();
			if (it.value() > maxCount) {
				maxWord = it.key();
				maxCount = it.value();
			}
		}
		System.out.println("Max word is " + maxWord + " with " + maxCount + " counts.");
		return Pair.with(maxWord, maxCount);

	}

}
