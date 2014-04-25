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

    public void merge(WordMap otherMap) throws Exception {
        if ( ! this.firstEntry().getKey().equals(otherMap.firstEntry().getKey())) {
            throw new Exception("Merging word maps: first entries must have the same keys.");
        }

        OccurrenceMap map1 = this.firstEntry().getValue();
        OccurrenceMap map2 = otherMap.firstEntry().getValue();

        if (map1.keySet().removeAll(map2.keySet())) {
            throw new Exception("Can only merge occurrence maps with distinct key sets.");
        }

        map1.putAll(map2);
        map2.clear();
        otherMap.clear();
    }

}
