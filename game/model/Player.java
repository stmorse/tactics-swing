package tactics.model;

public class Player {
	private int army;
	//private boolean isMyTurn;

	public Player(int a) {
		this.army = a;
		//this.isMyTurn = false;
	}

	public int getArmy() { return army; }
	public void setArmy(int a) { this.army = a; }
}