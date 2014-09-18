package tactics;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Iterator;

import tactics.model.Grid;
import tactics.view.Edge;

public class BoardTools2 {
	public BoardTools2() {}

	public static int[] coords(int x, int y, int m) {
		int[][] coords = {  {x-1, y-1}, {x, y-1}, {x+1, y-1},	// NW, N, NE
							{x+1, y}, 							// E
							{x+1, y+1}, {x, y+1}, {x-1, y+1}, 	// SE, S, SW
							{x-1, y} };							// W
		
		return coords[m];
	}

	public static ArrayList<Grid> getNeighbors(Grid g) {
		ArrayList<Grid> neighbors = new ArrayList<Grid>();

		int nx, ny;
		for (int i = 0; i < 8; i++) {
			nx = BoardTools2.coords(g.x, g.y, i)[0];
			ny = BoardTools2.coords(g.x, g.y, i)[1];
			if (nx > -1 && ny > -1 && nx < GameConstants.N_COLS && ny < GameConstants.N_ROWS)
				neighbors.add(new Grid(nx, ny));
		}
		
		return neighbors;
	}

	public static ArrayList<Edge> getBoundingPolygon(ArrayList<Grid> grids, boolean isZoomOn) {

		int ss = (isZoomOn) ? GameConstants.LARGE_SQUARE_SIZE : GameConstants.SMALL_SQUARE_SIZE;

		// create an arraylist of lines from grids.
		// create another arraylist of "pure" lines that do not repeat
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		Iterator gi = grids.iterator();
		Grid g;
		while (gi.hasNext()) {
			g = (Grid) gi.next();
			// North
			edges.add(new Edge(new Point(g.x * ss, g.y * ss), 
							   new Point(g.x * ss + ss, g.y * ss)));
			// East
			edges.add(new Edge(new Point(g.x * ss + ss, g.y * ss), 
							   new Point(g.x * ss + ss, g.y * ss + ss)));
			// South
			edges.add(new Edge(new Point(g.x * ss + ss, g.y * ss + ss), 
							   new Point(g.x * ss, g.y * ss + ss)));
			// West
			edges.add(new Edge(new Point(g.x * ss, g.y * ss + ss), 
							   new Point(g.x * ss, g.y * ss)));
		}

		ArrayList<Edge> clean = new ArrayList<Edge>();
		Edge e, f;
		boolean hasDouble = false;;
		while (!edges.isEmpty()) {
			// remove an edge
			// compare it to each other edge
			// if there is an equals, remove the other one too
			// if there is no equals, add to clean

			e = (Edge) edges.remove(0);		// will continue to "pop" the first one off the arraylist
			for (int k = 1; k < edges.size(); k++) {
				f = (Edge) edges.get(k);
				if (e.compare(f)) {
					edges.remove(f);
					hasDouble = true;
				}
			}

			if (!hasDouble)
				clean.add(e);

			hasDouble = false;
		}

		return clean;
	}

	public static int convertDirection(String dir) {
		switch (dir) {
			case "NW" : return 0;
			case "N"  : return 1;
			case "NE" : return 2;
			case "E"  : return 3;
			case "SE" : return 4;
			case "S"  : return 5;
			case "SW" : return 6;
			case "W"  : return 7;
			default : return 8;
		}
	}

	public static Point centerBoard(Grid centerGrid, int panelWidth, int panelHeight, boolean isZoomOn) {
		Point newOffset = new Point();
		int ss = (isZoomOn) ? GameConstants.LARGE_SQUARE_SIZE : GameConstants.SMALL_SQUARE_SIZE;
		newOffset.x = (centerGrid.x * ss) - (panelWidth / 2);
		newOffset.y = (centerGrid.y * ss) - (panelHeight / 2);

		if (newOffset.x < 0)
			newOffset.x = 0;
		if (newOffset.y < 0)
			newOffset.y = 0;

		return newOffset;
	}

	public static Grid[] getGridBoundaries(Rectangle viewRect, boolean isZoomOn) {
		// returns NW and SE grids visible
		int ss = (isZoomOn) ? GameConstants.LARGE_SQUARE_SIZE : GameConstants.SMALL_SQUARE_SIZE;
		Grid nw = getGridFromReal(new Point(viewRect.x, viewRect.y), isZoomOn);
		Grid se = getGridFromReal(new Point(viewRect.x + viewRect.width, 
											viewRect.y + viewRect.height), isZoomOn);
		Grid[] b = {nw, se};

		return b;
	}

	public static Grid getGridFromReal(Point real, boolean isZoomOn) {
		int ss = (isZoomOn) ? GameConstants.LARGE_SQUARE_SIZE : GameConstants.SMALL_SQUARE_SIZE;
		int gx = real.x / ss;
		int gy = real.y / ss;
		return new Grid(gx, gy);
	}

	public static boolean isRealWithinGrid(Point real, Grid grid, boolean isZoomOn) {
		Grid g = getGridFromReal(real, isZoomOn);
		
		if (g.x==grid.x && g.y==grid.y)
			return true;
		else
			return false;
	}

	public static Grid computeDirToGrid(int x, int y, int dir) {
		int nx = coords(x, y, dir)[0];
		int ny = coords(x, y, dir)[1];

		return new Grid(nx, ny);
	}

	public static int computeGridToDir(int sx, int sy, int fx, int fy) {
		// assuming this is an adjacent grid
		for (int i = 0; i < 8; i++) {
			if (coords(sx, sy, i)[0] == fx && coords(sx, sy, i)[1] == fy)
				return i;
		}

		// possible not an adjacent grid, get approx dir
		// 

		// for now...
		System.out.println("Direction error!!");
		return 8;
	}

}