package de.hpi.krestel.mySearchEngine.domain;

import java.util.Map;
import java.util.TreeMap;

public class WordMap extends TreeMap<String, OccurrenceMap> {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(System.lineSeparator());
		for (Map.Entry<String, OccurrenceMap> entry : this.entrySet()) {
			sb.append(" " + entry.getKey() + " ==> " + entry.getValue().toString());
		}
		sb.append("}");
		return sb.toString();
	}

    public void merge(WordMap otherMap, String word) throws Exception {
        if (this.keySet().removeAll(otherMap.keySet())) {
            throw new Exception("Can only merge occurrence maps with distinct key sets");
        }

        OccurrenceMap map1 = this.get(word);
        OccurrenceMap map2 = otherMap.get(word);

        map1.putAll(map2);
        map2.clear();
    }

}
