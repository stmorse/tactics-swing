package tactics.model;

import java.awt.Polygon;
import java.util.*;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.model.Unit;
import tactics.view.Edge;

public class CombatLogic {
	private GameLogic gl;
	private ArrayList<Unit> unitsInCombat;
	private ArrayList<Battle> openBattles;
	private Battle curBattle;

	public CombatLogic(GameLogic logic) {
		this.gl = logic;
		this.unitsInCombat = new ArrayList<Unit>();
		this.openBattles = new ArrayList<Battle>();
		//this.closedBattles = new ArrayList<Battle>();
		this.curBattle = null;
	}

	public void beginCombatPhase() {
		// identify all units in combat:
		// 	loop units
		// 		get neighbors, loop neighbors
		// 			if neighbors is enemy unit add to unitsInCombat

		Unit u, adj;
		int index;
		ArrayList<Grid> neighbors;
		Iterator ni;
		boolean hasEny = false;
		for (int i = 0; i < GameConstants.NUM_UNITS; i++) {
			u = gl.getUnit(i);

			if (u == null)
				continue;
			
			neighbors = BoardTools2.getNeighbors(u.getGrid());
			ni = neighbors.iterator();
			while (ni.hasNext()) {
				index = gl.getUnitIndexAtGrid((Grid)ni.next());
				
				if (index > -1) {
					adj = gl.getUnit(index);
					if (u.getArmy() != adj.getArmy()) {
						// enemy unit adjacent
						if (!unitsInCombat.contains(adj))
							unitsInCombat.add(adj);
						hasEny = true;
					}
				}
			}

			if (hasEny) {
				if (!unitsInCombat.contains(u))
					unitsInCombat.add(u);
			}

			hasEny = false;
			neighbors.clear();
		}
	}

	public boolean resolveCombat() {
		// check that all unitsInCombat are in an open battle
		// check that each open battle contains both armies
		//    (update odds, check isValid)
		// loop battles, process combat and change statuses of units

		// a cheat...
		int total = 0;
		Battle b;
		Iterator bi = openBattles.iterator();
		while (bi.hasNext()) {
			b = (Battle) bi.next();
			total += b.getRedUnits().size();
			total += b.getBlueUnits().size();

			b.updateOdds();
			if (!b.isValid())
				return false;
		}
		
		if (total != unitsInCombat.size()) 
			return false;

		// for now... (battle is half-ass too)
		bi = openBattles.iterator();
		CombatResult cr;
		Random die = new Random();
		while (bi.hasNext()) {
			cr = ((Battle) bi.next()).processBattle(die.nextInt(6) + 1);
			executeCombatResult(cr);
		}

		unitsInCombat.clear();
		openBattles.clear();
		curBattle = null;

		return true;
	}


	private void executeCombatResult(CombatResult cr) {
		Iterator ui;

		// first attackers
		if (cr.attackKills != null && !cr.attackKills.isEmpty()) {
			ui = cr.attackKills.iterator();
			while (ui.hasNext()) {
				//((Unit) ui.next()).kill();
				gl.killUnit((Unit) ui.next());
			}
		}

		if (cr.defenseKills != null && !cr.defenseKills.isEmpty()) {
			ui = cr.defenseKills.iterator();
			while (ui.hasNext()) {
				//((Unit) ui.next()).kill();
				gl.killUnit((Unit) ui.next());
			}
		}		
	}

	
	public void handleUnitToggle(Unit u) {
		// user has ctr-clicked (attempted to add/remove a unit from battle)

		// check this unit is in combat
		if (!unitsInCombat.contains(u))
			return;

		// check there is an open battle
		if (curBattle == null)
			return;

		// if this unit is in the battle, remove it
		if (curBattle.contains(u)) {
			curBattle.remove(u);
			return;
		}

		// otherwise, add it
		curBattle.add(u);

		// check if it's in any other battles, remove if so
		if (openBattles != null) {			
			Iterator bi = openBattles.iterator();
			Battle b;
			while (bi.hasNext()) {
				b = (Battle) bi.next();
				if (b.contains(u) && !b.equals(curBattle)) {
					b.remove(u);
					return;
				}
			}
		}
	}

	public void handleBattleToggle(Unit u) {
		// user has double-clicked (attempted to open a battle)

		// check this unit is in combat
		if (!unitsInCombat.contains(u))
			return;

		// if there's already a cur battle and we double clicked inside it, return
		if (curBattle != null && curBattle.contains(u))
			return;

		// check openBattles for this unit
		if (openBattles != null) {			
			Iterator bi = openBattles.iterator();
			Battle b;
			while (bi.hasNext()) {
				b = (Battle) bi.next();
				if (b.contains(u)) {
					curBattle = b;
					return;
				}
			}
		}

		// not already in any open battles,
		// create new battle, add to openBattles, and make it current
		Battle nb = new Battle(u, gl.getCurrentPlayerArmy());
		openBattles.add(nb);
		curBattle = nb;
	}

	public Battle getCurrentBattle() { return curBattle; }

	public ArrayList<Battle> getOpenBattles() { return openBattles; }

	public ArrayList<Edge> getBoundingPolygons(Battle b, boolean isZoomOn) { 
		if (openBattles.contains(b)) {
			return b.getBoundingPolygon(isZoomOn);
		} else
			return null;
	}

	public ArrayList<Unit> getUnitsInCombat() { return unitsInCombat; }

}