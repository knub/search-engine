package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;

public class OccurrenceMap extends TIntObjectHashMap<DocumentEntry> {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!this.isEmpty()) {
			int[] keys = this.keys();
			Arrays.sort(keys);
			int key0 = keys[0];
			sb.append("ID:" + key0 + "-->");
			sb.append(this.get(key0).toString());
		}
		return sb.toString();
	}

}
