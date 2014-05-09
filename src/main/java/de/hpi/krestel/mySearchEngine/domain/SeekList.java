package de.hpi.krestel.mySearchEngine.domain;

import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> {
	long averageDocLength = -1;

	public long getAverageDocumentLength() {
		return this.averageDocLength;
	}

	public void setAverageDocumentLength(long v) {
		this.averageDocLength = v;
	}
}
