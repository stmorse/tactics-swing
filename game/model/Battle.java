package tactics.model;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.view.Edge;

public class Battle {
	private ArrayList<Unit> redUnits, blueUnits;
	private ArrayList<Edge> boundingPolyLarge, boundingPolySmall;

	private int attacker;

	private int redTotalStrength, blueTotalStrength;
	private int odds;		

	public static int ONE_TO_SIX = 0, ONE_TO_FIVE = 1, ONE_TO_FOUR = 2,
					  ONE_TO_THREE = 3, ONE_TO_TWO = 4, ONE_TO_ONE = 5,
					  TWO_TO_ONE = 6, THREE_TO_ONE = 7, FOUR_TO_ONE = 8,
					  FIVE_TO_ONE = 9, SIX_TO_ONE = 10;

	public static final int A_ELIM = 0, D_ELIM = 1, EXCHANGE = 2, A_BACK_2 = 3, D_BACK_2 = 4;

	public static int[][] oddsMx = {
		{A_ELIM,   A_ELIM,   A_BACK_2, A_ELIM,   A_ELIM,   A_ELIM},		// 1 : 6
		{A_ELIM,   A_ELIM,   A_BACK_2, A_BACK_2, A_ELIM,   A_ELIM},		// 1 : 5
		{A_BACK_2, A_ELIM,   A_BACK_2, A_BACK_2, A_ELIM,   A_ELIM},		// 1 : 4
		{A_BACK_2, A_BACK_2, A_BACK_2, A_BACK_2, A_ELIM,   A_ELIM},		// 1 : 3
		{D_BACK_2, EXCHANGE, A_BACK_2, A_BACK_2, A_ELIM,   A_ELIM},		// 1 : 2
		{D_ELIM,   EXCHANGE, D_BACK_2, A_BACK_2, A_ELIM,   A_ELIM},		// 1 : 1
		{D_ELIM,   EXCHANGE, D_BACK_2, A_BACK_2, EXCHANGE, A_ELIM},		// 2 : 1
		{D_ELIM,   EXCHANGE, D_BACK_2, D_BACK_2, EXCHANGE, D_ELIM},		// 3 : 1
		{D_ELIM,   EXCHANGE, D_ELIM,   D_BACK_2, D_BACK_2, D_ELIM},		// 4 : 1
		{D_ELIM,   D_BACK_2, D_ELIM,   D_BACK_2, D_ELIM,   D_ELIM},		// 5 : 1
		{D_ELIM,   D_BACK_2, D_ELIM,   D_ELIM,   D_ELIM,   D_ELIM}		// 6 : 1
	};

	public Battle(int a) {
		this.redUnits = new ArrayList<Unit>();
		this.blueUnits = new ArrayList<Unit>();

		this.boundingPolyLarge = new ArrayList<Edge>();
		this.boundingPolySmall = new ArrayList<Edge>();

		this.attacker = a;

		this.redTotalStrength = 0;
		this.blueTotalStrength = 0;
		this.odds = 0;
	}

	public Battle(Unit u, int a) {
		this.redUnits = new ArrayList<Unit>();
		this.blueUnits = new ArrayList<Unit>();

		this.boundingPolyLarge = new ArrayList<Edge>();
		this.boundingPolySmall = new ArrayList<Edge>();

		this.attacker = a;

		this.redTotalStrength = 0;
		this.blueTotalStrength = 0;
		this.odds = 0;

		add(u);
		updateOdds();
	}

	public void add(Unit u) {
		if (u.getArmy() == GameConstants.RED_ARMY && !redUnits.contains(u))
			redUnits.add(u);
		else if (u.getArmy() == GameConstants.BLUE_ARMY && !blueUnits.contains(u))
			blueUnits.add(u);

		updatePolygon();
		updateOdds();
	}

	public void remove(Unit u) {
		if (u.getArmy() == GameConstants.RED_ARMY && redUnits.contains(u))
			redUnits.remove(u);
		else if (u.getArmy() == GameConstants.BLUE_ARMY && blueUnits.contains(u))
			blueUnits.remove(u);
		else
			System.out.println("wtf");

		updatePolygon();
		updateOdds();
	}

	public boolean contains(Unit u) {
		if (redUnits.contains(u) || blueUnits.contains(u))
			return true;
		else
			return false;
	}

	public void updateOdds() {
		// reset red/blue total strength
		// loop through all units
		// if red unit, add to red, and vice versa
		// compute simplified ratio

		redTotalStrength = 0;
		blueTotalStrength = 0;

		int str;
		if (redUnits.size() > 0) {
			for (int i = 0; i < redUnits.size(); i++) {
				str = redUnits.get(i).getStrength();
				
				// if unit is a HQ, gets 1 pt on defense
				if (str == 0 && attacker == GameConstants.BLUE_ARMY)
					str = 1;

				redTotalStrength += str;
			}
		}

		if (blueUnits.size() > 0) {
			for (int i = 0; i < blueUnits.size(); i++) {
				str = blueUnits.get(i).getStrength();
				
				// if unit is a HQ, gets 1 pt on defense
				if (str == 0 && attacker == GameConstants.RED_ARMY)
					str = 1;

				blueTotalStrength += str;
			}
		}

		int attack = (attacker == GameConstants.RED_ARMY) ?
						redTotalStrength : blueTotalStrength;
		int defense = (attacker == GameConstants.RED_ARMY) ?
						blueTotalStrength : redTotalStrength;

		if (attack >= defense && defense > 0) {
			odds = (attack / defense) + 4;
			
			if (odds > SIX_TO_ONE)
				odds = SIX_TO_ONE;
		} else if (attack < defense && attack > 0) {
			odds = -(defense / attack) + 6;

			if (odds < ONE_TO_SIX)
				odds = ONE_TO_SIX;
		} else
			odds = -1;	// one or the other sides are missing.
	}

	public int getOdds() { return odds; }

	public boolean isValid() { 
		return (odds >= 0) ? true : false;
	}

	public CombatResult processBattle(int dieRoll) {
		// receives die roll from combat logic
		// computes outcome or type (A_ELIM, D_ELIM, etc)
		// computes all possible results of this outcome with given pieces

		if (dieRoll > 6 || dieRoll < 1)
			dieRoll = 1;

		dieRoll--;	// make range 0-5 from 1-6

		// make sure we have most updated odds
		updateOdds();

		// we cross-reference the outcome....
		int outcome = oddsMx[odds][dieRoll];

		ArrayList<Unit> attackers, defenders;
		if (attacker == GameConstants.RED_ARMY) {
			attackers = redUnits;
			defenders = blueUnits;
		} else {
			attackers = blueUnits;
			defenders = redUnits;
		}

		switch (outcome) {
		case A_ELIM :
			return new CombatResult(outcome, 
									attackers,
									null,
									null);

		case D_ELIM :
			return new CombatResult(outcome,
									null,
									defenders,
									null);

		case EXCHANGE :
			return new CombatResult(outcome,
									attackers,
									defenders,
									null);

		case A_BACK_2 :
			return new CombatResult(outcome, 
									attackers,
									null,
									null);

		case D_BACK_2 :
			return new CombatResult(outcome,
									null,
									defenders,
									null);

		default :
			break;
		}

		return null;
	}

	/////////////////////

	public ArrayList<Unit> getRedUnits() { return redUnits; }

	public ArrayList<Unit> getBlueUnits() { return blueUnits; }

	/////////////////////

	private ArrayList<Grid> getAllGrids() {
		ArrayList<Grid> all = new ArrayList<Grid>();

		Iterator ui; 
		if (redUnits != null) {
			ui = redUnits.iterator();
			while (ui.hasNext())
				all.add(((Unit)ui.next()).getGrid());
		}

		if (blueUnits != null) {
			ui = blueUnits.iterator();
			while (ui.hasNext())
				all.add(((Unit)ui.next()).getGrid());
		}

		return all;
	}

	private void updatePolygon() {
		// clear the old
		boundingPolyLarge = null;
		boundingPolySmall = null;
		
		// get the new
		boundingPolyLarge = BoardTools2.getBoundingPolygon(getAllGrids(), true);
		boundingPolySmall = BoardTools2.getBoundingPolygon(getAllGrids(), false);
	}

	public ArrayList<Edge> getBoundingPolygon(boolean isZoomOn) {
		return (isZoomOn) ? boundingPolyLarge : boundingPolySmall;
	}

	public String getString() {
		String s = "";

		if (attacker == GameConstants.RED_ARMY)
			s += redUnits.size() + " Red vs " + blueUnits.size() + " Blue.";
		else
			s += blueUnits.size() + " Blue vs " + redUnits.size() + " Red.";
		
		s += " (";

		if (odds >= 5)
			s += (odds - 4) + " : 1)";
		else if (odds < 5)
			s += "1 : " + (6 - odds) + ")";
		else
			s += "---)";

		return s;
		
	}
}

