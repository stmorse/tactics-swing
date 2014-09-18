package tactics.model;

import java.util.LinkedList;

import tactics.GameConstants;

public class Unit {
	private Grid unitStartGrid;		// board coords x,y
	private Grid unitGrid;
	public LinkedList<Grid> unitPath;

	private int movesTotal,
				movesLeft,
				strength,			
				uID;

	private int type;
	private int army;

	private boolean isAlive, justKilled;
	
	public Unit(int sx, int sy, int t, int a, int i) {
		unitStartGrid = new Grid(sx, sy);
		unitGrid = unitStartGrid;
		unitPath = new LinkedList<Grid>();

		if (t == GameConstants.INFANTRY || t == GameConstants.AIRBORNE ||
			t == GameConstants.AMPHIB || t == GameConstants.MOUNTAIN) {
			movesTotal = 15;
			strength = 1;
		} else if (t == GameConstants.ARMOR) {
			movesTotal = 21;
			strength = 2;
		} else if (t == GameConstants.CORPS_HQ || t == GameConstants.ARMY_HQ ||
					t == GameConstants.ARMYGROUP_HQ) {
			movesTotal = 15;
			strength = 0;
		}

		movesLeft = movesTotal;
		type = t;
		army = a;
		uID = i; 
		isAlive = true;
		justKilled = false;
	}

	public boolean move(Grid n, int cost) {
		// check that unit can afford move
		if (cost > movesLeft) 
			return false;

		// before we update, add old square to path with moves taken at that point
		unitPath.add(new Grid(unitGrid.x, unitGrid.y, (movesTotal - movesLeft)));

		// update moves left
		movesLeft -= cost;

		// update grid
		unitGrid = n;

		return true;
	}

	public void kill() {
		isAlive = false;
		justKilled = true;
	}

	public boolean resetTo(Grid b) {
		// reset unit to a point along unitPath if the point exists
		// reset grid and moves

		LinkedList<Grid> temp = unitPath;
		Grid t;
		while (unitPath.size() > 0) {
			t = (Grid) unitPath.removeLast();
			if (t.x == b.x && t.y == b.y) {
				unitGrid = b;
				movesLeft = movesTotal - t.num;
				return true;
			}			
		}

		// no matches, restore unit path and restore false
		unitPath = temp;
		return false;
	}
	
	public void resetForNewTurn() {
		movesLeft = movesTotal;
		unitPath.clear();
		if (!isAlive) {
			justKilled = false;
			unitGrid.x = 50;
			unitGrid.y = 50;
			movesLeft = 0;
		}
	}

	public void setGrid(int x, int y) {
		if (unitGrid == null)
			unitGrid = new Grid(x, y);
		else {
			unitGrid.x = x;
			unitGrid.y = y;
		}
	}
	
	public void setGrid(Grid g) {
		unitGrid = g;
	}
	
	public Grid getStartGrid() { return unitStartGrid; }
	public Grid getGrid() { return unitGrid; }
	public int getGridX() { return unitGrid.x; }
	public int getGridY() { return unitGrid.y; }
	public int getArmy() { return army; }
	public int getType() { return type; }
	public int getStrength() { return strength; }

	public boolean isAlive() { return isAlive; }
	public boolean isJustKilled() { return justKilled; }

	public String getString() {
		String s = "";

		s += uID;
		s += (uID==1) ? "st" : (uID==2) ? "nd" : (uID==3) ? "rd" : "th"; 
		s += " " + GameConstants.unitNames[type];
		s += ", " + GameConstants.armyNames[army];

		return s;
	}
	
}