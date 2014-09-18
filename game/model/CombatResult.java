package tactics.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

// this class really just stores the result for a combat.
// no logic

public class CombatResult {
	int type;					// of type A_ELIM, D_ELIM, ...
	ArrayList<Unit> attackKills;
	ArrayList<Unit> defenseKills;
	ArrayList<LinkedList> retreatPaths;

	public CombatResult(int t, ArrayList<Unit> ak, ArrayList<Unit> dk, ArrayList<LinkedList> paths) {
		this.type = t;
		this.attackKills = ak;
		this.defenseKills = dk;
		this.retreatPaths = paths;
	}
}