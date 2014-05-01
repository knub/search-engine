package de.hpi.krestel.mySearchEngine.domain;

import java.util.Map;
import java.util.TreeMap;

public class WordMap extends TreeMap<String, OccurrenceMap> implements  Comparable<WordMap> {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(System.lineSeparator());
		for (Map.Entry<String, OccurrenceMap> entry : this.entrySet()) {
			sb.append(" " + entry.getKey() + " ==> " + entry.getValue().toString());
		}
		sb.append(System.lineSeparator());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * This merges to WordMaps, which come from a part index:
	 * Precondition:
	 * Both maps must have exactly one entry. The key of these entries (the String)
	 * must be the same.
	 * The values (OccurrenceMaps) must have distinct key sets.
	 * The result is stored in this WordMap.
	 * @param otherMap The map to merge with.
	 */
    public void partIndexMerge(WordMap otherMap) {
	    if (this.size() == 0) {
		    this.putAll(otherMap);
		    return;
	    }
        if ( ! this.firstEntry().getKey().equals(otherMap.firstEntry().getKey())) {
            throw new RuntimeException("Merging word maps: first entries must have the same keys.");
        }

        OccurrenceMap map1 = this.firstEntry().getValue();
        OccurrenceMap map2 = otherMap.firstEntry().getValue();

        if (map1.keySet().removeAll(map2.keySet())) {
            throw new RuntimeException("Can only partIndexMerge occurrence maps with distinct key sets.");
        }

        map1.putAll(map2);
        map2.clear();
        otherMap.clear();
    }

	@Override
	public int compareTo(WordMap wordMap) {
		if (this.size() != 1 || wordMap.size() != 1)
			throw new RuntimeException("Cannot compare WordMaps with more than one element.");
		return this.firstKey().compareTo(wordMap.firstKey());
	}
}
