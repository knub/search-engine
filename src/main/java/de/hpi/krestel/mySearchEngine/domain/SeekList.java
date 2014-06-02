package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.List;
import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> {
	long averageDocLength = -1;
	long documentCount = -1;
    List<String> titleMap;

	private TIntIntMap docLengths = new TIntIntHashMap();

	public long getAverageDocumentLength() {
		return this.averageDocLength;
	}

	public void setAverageDocumentLength(long v) {
		this.averageDocLength = v;
	}

	public TIntIntMap getDocLengths() {
		return docLengths;
	}

	public void setDocLengths(TIntIntMap docLengths) {
		this.docLengths = docLengths;
	}

	public long getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(long documentCount) {
		this.documentCount = documentCount;
	}

    public void setTitleMap(List<String> list) { this.titleMap = list; }

    public List<String> getTitleMap() { return this.titleMap; }

}
