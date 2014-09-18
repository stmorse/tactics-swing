package tactics.model;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import tactics.GameConstants;

public class Battlefield {
	private int[][] battlefield;	// holds indexes for units in play
	private Unit[] units;			// holds all unit objects 
	private ArrayList<Unit> deadUnits;

	public Battlefield() {
		this.units = new Unit[GameConstants.NUM_UNITS];
		this.battlefield = new int[GameConstants.N_COLS][GameConstants.N_ROWS];
		this.deadUnits = new ArrayList<Unit>();

		// init battlefield as all index -1
		for (int[] row : battlefield)
			Arrays.fill(row, -1);

		init();
	}

	public boolean move(Grid np, int cost, int i) {
		// store the old grid
		int ox = units[i].getGridX();
		int oy = units[i].getGridY();

		if (!units[i].move(np, cost))
			return false;
		else {
			// update battlefield grid
			battlefield[ox][oy] = -1;
			battlefield[np.x][np.y] = i;

			return true;
		}
	}

	public boolean resetUnitTo(Grid np, int i) {
		// store the old grid
		int ox = units[i].getGridX();
		int oy = units[i].getGridY();

		// resetTo will reset unitPath and moves
		if (!units[i].resetTo(np))
			return false;

		battlefield[ox][oy] = -1;
		battlefield[np.x][np.y] = i;

		return true;
	}

	public void resetForNewTurn() {
		for (int i = 0; i < units.length; i++) {
			units[i].resetForNewTurn();
		}
	}

	public void killUnit(Unit u) {
		int index = getUnitIndex(u);

		if (index < 0) {
			System.out.println("Index error in killUnit().");
			index = 0;
		}
		
		units[index].kill();
		battlefield[u.getGridX()][u.getGridY()] = -1;
		deadUnits.add(u);
	}

	public Unit getUnit(int index) {
		return units[index];
	}

	public int getUnitIndex(Unit u) {
		for (int i = 0; i < units.length; i++) {
			if (u.equals(units[i]))
				return i;
		}

		return -1;
	}

	public int getUnitIndexAtGrid(Grid g) {
		return battlefield[g.x][g.y];
	}

	//////////////

    public void init() {
		Scanner filescan, linescan;
		String line, nextToken;
		
		try {
			filescan = new Scanner(new File(GameConstants.unitDataPath));
			int j = 0;
			while (filescan.hasNextLine()) {
				line = filescan.nextLine();
				
				if (line.startsWith("%") || line.trim().length() < 2)
					continue;
				
				if (j >= GameConstants.NUM_UNITS) {
					System.out.println(GameConstants.unitDataPath + " has more unit info than expected: " + j);
					return;
				}

				linescan = new Scanner(line);
				linescan.useDelimiter(",");
				int[] atts = new int[5];
				for (int i=0;i<5;i++) {
					nextToken = linescan.next().trim();
					atts[i] = new Integer(nextToken).intValue();
				}

				units[j] = new Unit(atts[0], atts[1], 	// x, y
									atts[2],  			// type
									atts[3],	 		// army (color)
									atts[4] );			// ID
				
				battlefield[atts[0]][atts[1]] = j;

				j++;
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
    }
}