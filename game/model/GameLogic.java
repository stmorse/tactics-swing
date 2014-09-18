package tactics.model;

import java.util.ArrayList;

import tactics.BoardTools2;
import tactics.GameConstants;

public class GameLogic {
	private CombatLogic combatLogic;
	private Battlefield bfield;
	private MapBoard mboard;
	private Player redPlayer, bluePlayer, curPlayer;
	private int cui;
	private Grid cg;

	private int gameState;

	public GameLogic() {
		this.combatLogic = new CombatLogic(this);
		this.bfield = new Battlefield();
		this.mboard = new MapBoard();
		this.redPlayer = new Player(GameConstants.RED_ARMY);
		this.bluePlayer = new Player(GameConstants.BLUE_ARMY);
		this.curPlayer = redPlayer;
		this.cui = -1;
		this.cg = new Grid(2, 2);
		this.gameState = GameConstants.RED_MOVE;
	}

	////////////////

	public boolean endMovementPhase() {
		if (gameState == GameConstants.RED_MOVE) {
			// end red move
			gameState = GameConstants.RED_COMBAT;
			deselectAllUnits();
			combatLogic.beginCombatPhase();
			return true;
		} else if (gameState == GameConstants.BLUE_MOVE) {
			// end blue move
			gameState = GameConstants.BLUE_COMBAT;
			deselectAllUnits();
			combatLogic.beginCombatPhase();
			return true;
		} else
			return false;
	}

	public boolean resolveCombat() {
		// ensure we are in the combat phase
		if (gameState == GameConstants.RED_MOVE ||
			gameState == GameConstants.BLUE_MOVE ||
			gameState == GameConstants.GAME_OVER)
			return false;

		// attempt to resolve all battles
		if (combatLogic.resolveCombat())
			return true;
		else
			return false;
	}

	public boolean endPlayerTurn() {
		if (gameState == GameConstants.RED_MOVE || gameState == GameConstants.RED_COMBAT) {
			gameState = GameConstants.BLUE_MOVE;
			curPlayer = bluePlayer;
			bfield.resetForNewTurn();
			return true;
		} else if (gameState == GameConstants.BLUE_MOVE || gameState == GameConstants.BLUE_COMBAT) {
			gameState = GameConstants.RED_MOVE;
			curPlayer = redPlayer;
			bfield.resetForNewTurn();
			return true;
		} else
			return false;
	}

	////////////////

	public boolean moveCurrentUnit(Grid g, int dir) {
		// check a unit is selected
		if (cui < 0)
			return false;

		// check that g is a single square away from cg
		Grid check = BoardTools2.computeDirToGrid(cg.x, cg.y, dir);
		if (g.x != check.x || g.y != check.y)
			return false;

		// check if we can move here
		if (!mboard.getNodeAt(g).isWalkable())
			return false;	
			
		// compute edge cost
		//int dir = BoardTools2.computeGridToDir(cg.x, cg.y, g.x, g.y);
		int cost = mboard.getEdgeCost(bfield.getUnit(cui).getGridX(), bfield.getUnit(cui).getGridY(), dir);

		// check that current unit can afford the move, 
		// .move will update unitPath and unitGrid
		if (!bfield.move(g, cost, cui))
			return false;

		return true;
	}

	public boolean resetCurrentUnitAlongPath(Grid g) {
		// reset the current unit to a grid along its path
		if (cui < 0)
			return false;

		return bfield.resetUnitTo(g, cui);
	}

	public void deselectAllUnits() {
		this.cui = -1;
	}

	public void killUnit(Unit u) {
		bfield.killUnit(u);
	}

	///////////

	public CombatLogic getCombatLogic() { return this.combatLogic; }

	public ArrayList<Unit> getUnitsInCombat() { return combatLogic.getUnitsInCombat(); }

	public Unit getUnit(int index) { 
		Unit u = bfield.getUnit(index); 
		if (u.isAlive() || u.isJustKilled())
			return u;
		else
			return null;
	}

	public Unit getCurrentUnit() { 
		if (cui > -1)
			return bfield.getUnit(cui);
		else
			return null;
	}
	
	public int getCurrentUnitIndex() 	{ return cui; }
	
	public void setCurrentUnit(int i) 	{ this.cui = i; }
	
	public Grid getCurrentGrid() { return cg; }
	
	public void setCurrentGrid(Grid g) { this.cg = g; }

	public MapNode getCurrentMapNode() { return mboard.getNodeAt(cg.x, cg.y); }

	public int getCurrentPlayerArmy() { return curPlayer.getArmy(); }

	public int getGameState() { return gameState; }

	public int getUnitIndexAtGrid(Grid g) {
		return bfield.getUnitIndexAtGrid(g);
	}
}