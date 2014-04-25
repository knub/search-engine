package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class OccurrenceMap extends TIntObjectHashMap<DocumentEntry> {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int[] keys = this.keys();
		Arrays.sort(keys);
		for (int key : keys) {
			sb.append("ID:" + key + "-->");
			sb.append(this.get(key).toString());
		}
		return sb.toString();
	}

}
