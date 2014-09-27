package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class DocumentEntry
{
    public TIntList positions = new TIntArrayList();
    public TIntList offsets = new TIntArrayList();
    public TIntList lengths = new TIntArrayList();

	double rank = -1;

    public DocumentEntry() {}

    public DocumentEntry(int position, int offset, int length)
    {
        this.positions.add(position);
        this.offsets.add(offset);
        this.lengths.add(length);
    }

	public DocumentEntry(int[] position, int offset[], int length[])
    {
		this.positions.add(position);
		this.offsets.add(offset);
		this.lengths.add(length);
	}

	@Override
	public String toString()
    {
		StringBuilder sb = new StringBuilder();

		sb.append(this.positions.size()).append(",");
		for (int i = 0; i < this.positions.size(); i++) {
			sb.append("[")
              .append(this.positions.get(i))
              .append(",")
              .append(this.offsets.get(i))
              .append(",")
              .append(this.lengths.get(i))
              .append("]");
		}

		return sb.toString();
	}

	public int size()
    {
		return this.positions.size();
	}

	public Double getRank()
    {
		return this.rank;
	}

	public void setRank(double rank)
    {
		this.rank = rank;
	}
}
