package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class DocumentEntry {
    public TIntList positions = new TIntArrayList();
    public TIntList offsets = new TIntArrayList();
    public TIntList lengths = new TIntArrayList();

	double rank = -1;

	public DocumentEntry() {}
    public DocumentEntry(int position, int offset, int length) {
        positions.add(position);
        offsets.add(offset);
        lengths.add(length);
    }

	public DocumentEntry(int[] position, int offset[], int length[]) {
		positions.add(position);
		offsets.add(offset);
		lengths.add(length);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(positions.size() + ",");
		for (int i = 0; i < positions.size(); i++) {
			sb.append("[" + positions.get(i) + "," + offsets.get(i) + "," + lengths.get(i) + "]");
		}
		return sb.toString();
	}

	public int size() {
		return positions.size();
	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}

}
