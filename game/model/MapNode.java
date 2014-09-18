package tactics.model;

//import java.awt.Point;

import tactics.GameConstants;

public class MapNode {
	int basetype;
	int x, y;
	int roadCode;
	int riverCode;
	boolean[] hasRoad = new boolean[8];
	boolean[] hasRiver = new boolean[8];
	boolean walkable;

	int[] MASKS = { 1, 2, 4, 8, 16, 32, 64, 128 };
	
	public MapNode(int tt, int xx, int yy) {
		this.basetype = tt;
		if (basetype == GameConstants.OCEAN_TILE || basetype == GameConstants.MOUNTAIN_TILE)
			this.walkable = false;
		else
			this.walkable = true;
		this.x = xx;
		this.y = yy;
	}
	
	//public Point getGrid() { return new Point(x, y); }
	//public int getX() { return x; }
	//public int getY() { return y; }

	public boolean isWalkable() { return walkable; }
	
	public int getBaseCost() {
		switch (basetype) {
		case 0 :
			return 100;
		case 1:
			return 3;
		case 2 :
			return 6;
		case 3 :
			return 90;
		case 4 :
			return 1;
		case 5 :
			return 3;
		default :
			return 3;
		}
	}
	
	public void setRoad(int r) {
		roadCode = r;
		
		// convert roadCode data to hasRoad (bool)
		int edgeVal;
		for (int i = 0; i < 8; i++) {
			edgeVal = (r & MASKS[i]) >> i;
			hasRoad[i] = (edgeVal==1)?true:false;
		}
	}
	
	public void setRiver(int riv) {
		riverCode = riv;
		
		// convert riverCode data to hasRiver (bool)
		int edgeVal;
		for (int i = 0; i < 8; i++) {
			edgeVal = (riv & MASKS[i]) >> i;
			hasRiver[i] = (edgeVal==1)?true:false;
		}
	}

	public String getString() {
		String s = "";

		s += GameConstants.terrainNames[basetype] + " (Cost: " + (getBaseCost() / 3) + ")";

		return s;
	}
}