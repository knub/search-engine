package de.hpi.krestel.mySearchEngine.domain;

import org.omg.CORBA.Environment;

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
}
