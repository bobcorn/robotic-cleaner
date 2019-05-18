package it.unibo.cleaner;

import java.util.ArrayList;

public class MapBuilder {

	private ArrayList<ArrayList<Character>> map = new ArrayList<>();

	public void addCell(int x, int y, char status) {
		while (map.size() <= x)
			map.add(new ArrayList<>());

		ArrayList<Character> row = map.get(x);
		while (row.size() <= y)
			row.add(Map.INVALID);

		row.set(y, (Character) status);
	}

	public Map getMap() {
		ArrayList<char[]> tmpData = new ArrayList<>();
		for (ArrayList<Character> row : map) {
			char[] rowAr = new char[row.size()];
			for (int y = 0; y < row.size(); y++)
				rowAr[y] = row.get(y);
			
			tmpData.add(rowAr);
		}

		return new Map(tmpData.toArray(new char[0][]));
	}
}
