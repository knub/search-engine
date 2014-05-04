package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;

public class PhraseTag implements Comparable<PhraseTag> {

	public final int position;
	public final int offset;
	public final int length;
	public final int wordIndex;

	public PhraseTag(int position, int offset, int length, int wordIndex) {
		this.position = position;
		this.offset = offset;
		this.length = length;
		this.wordIndex = wordIndex;
	}

	@Override
	public int compareTo(PhraseTag other) {
		if (this.position == other.position) {
			return this.wordIndex - other.wordIndex;
		}
		return this.position - other.position;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof PhraseTag))
			return false;
		return this.position == ((PhraseTag) other).position && this.wordIndex == ((PhraseTag) other).wordIndex;
	}

	@Override
	public String toString() {
		return "[" + this.position + ", " + this.wordIndex + "]";
	}
}