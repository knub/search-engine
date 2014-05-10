package de.hpi.krestel.mySearchEngine.domain;

import de.hpi.krestel.mySearchEngine.searching.ResultSet;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.javatuples.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

	public void merge(OccurrenceMap other) {
		TIntSet thisKeys = new TIntHashSet(this.keySet());
		TIntSet otherKeys = new TIntHashSet(other.keySet());
		thisKeys.retainAll(otherKeys);

		// merge the ones which exist in both results
		for (int key : thisKeys.toArray()) {
			DocumentEntry thisEntry = this.get(key);
			DocumentEntry otherEntry = other.get(key);

			thisEntry.lengths.addAll(otherEntry.lengths);
			thisEntry.positions.addAll(otherEntry.positions);
			thisEntry.offsets.addAll(otherEntry.offsets);
			thisEntry.lengths.sort();
			thisEntry.positions.sort();
			thisEntry.offsets.sort();
		}

		// now add the ones, which only exist in the other result
		otherKeys.removeAll(this.keySet());

		for (int key : otherKeys.toArray()) {
			this.put(key, other.get(key));
		}
	}

	public void retain(OccurrenceMap other) {
		TIntSet otherKeys = other.keySet();
		int[] thisKeys = this.keys();
		for (int key : thisKeys) {
			if (!otherKeys.contains(key))
				this.remove(key);
		}

	}

	public ResultSet toResultSet() {
		final ResultSet rs = new ResultSet();
		this.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
			@Override
			public boolean execute(int docId, DocumentEntry docEntry) {
				rs.add(Pair.with(docId, docEntry.getRank()));
				return true;
			}
		});
		Collections.sort(rs, new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(Pair<Integer, Double> s1, Pair<Integer, Double> s2) {
				return s2.getValue1().compareTo(s1.getValue1());
			}
		});
		return rs;
	}

	public void removeResults(OccurrenceMap other) {
		TIntSet otherKeys = other.keySet();
		for (int key : otherKeys.toArray()) {
			this.remove(key);
		}
	}
}
