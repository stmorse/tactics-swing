package tactics.model;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import tactics.BoardTools2;
import tactics.GameConstants;

public class MapBoard {
	private int nCols, nRows;
	private MapNode[][] mapNodes;
	
	public MapBoard() {
		this.nCols = GameConstants.N_COLS;
		this.nRows = GameConstants.N_ROWS;
		this.mapNodes = new MapNode[nCols][nRows];

		init();
	}

	public int getBaseCostAt(int x, int y) {
		return mapNodes[y][x].getBaseCost();
	}
	
	public MapNode getNodeAt(int x, int y) {
		return mapNodes[x][y];
	}
	
	public MapNode getNodeAt(Grid g) {
		return getNodeAt(g.x, g.y);
	}

	public ArrayList<MapNode> getNeighbors(int x, int y) {
		ArrayList<MapNode> neighbors = new ArrayList<MapNode>();

		int nx, ny;
		for (int i = 0; i < 8; i++) {
			nx = BoardTools2.coords(x, y, i)[0];
			ny = BoardTools2.coords(x, y, i)[1];
			if (nx > -1 && ny > -1 && nx < nCols && ny < nRows)
				neighbors.add(mapNodes[nx][ny]);
		}
		
		return neighbors;
	}
	
	public int getEdgeCost(int x, int y, int dir) {
		if (mapNodes[x][y].hasRoad[dir])
			return 1;
		
		int nx = BoardTools2.coords(x, y, dir)[0];
		int ny = BoardTools2.coords(x, y, dir)[1];
		int cost = 0;
		
		if (nx > -1 && ny > -1 && nx < nCols && ny < nRows) {
			cost = mapNodes[nx][ny].getBaseCost();
			if (mapNodes[x][y].hasRiver[dir])
				cost += 3;
			return cost;
		}
		else
			return 0;
	}

	/////////////////////////////////
	
	private enum LoadType {
		BASE_DATA,
		ROAD_DATA,
		RIVER_DATA
	}

	private void init() {
		loadDataFromFile(GameConstants.mapDataPath, LoadType.BASE_DATA);
		loadDataFromFile(GameConstants.roadDataPath, LoadType.ROAD_DATA);
		loadDataFromFile(GameConstants.riverDataPath, LoadType.RIVER_DATA);
	}
	
	private void loadDataFromFile(String path, LoadType action) {
		Scanner filescan, linescan;
		String line, nextToken;
		
		try {
			filescan = new Scanner(new File(path));
			int j = 0;
			while (filescan.hasNextLine()) {
				line = filescan.nextLine();
				
				if (line.startsWith("%") || line.trim().length() < 2)
					continue;
				
				linescan = new Scanner(line);
				linescan.useDelimiter(",");
				int n;
				for (int i = 0; i < nCols; i++) {
					nextToken = linescan.next().trim();
					if (action == LoadType.BASE_DATA) {
						n = new Integer(nextToken).intValue();
						mapNodes[i][j] = new MapNode(n, i, j);
					} else if (action == LoadType.ROAD_DATA) {
						if (!(nextToken.startsWith("-"))) {
							n = new Integer(nextToken).intValue();
							mapNodes[i][j].setRoad(n);
						}
					} else if (action == LoadType.RIVER_DATA) {
						if (!(nextToken.startsWith("-"))) {
							n = new Integer(nextToken).intValue();
							mapNodes[i][j].setRiver(n);
						}
					}
					else
						return;
				}
				
				j++;
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
	}
}