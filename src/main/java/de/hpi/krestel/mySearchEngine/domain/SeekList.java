package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;

import java.util.List;
import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> {
	long averageDocLength = -1;
	long documentCount = -1;
    private List<String> titleMap;
    private TIntList docLengths;

	public long getAverageDocumentLength() {
		return this.averageDocLength;
	}

	public void setAverageDocumentLength(long v) {
		this.averageDocLength = v;
	}

	public TIntList getDocLengths() {
		return docLengths;
	}

	public void setDocLengths(TIntList docLengths) {
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
