package tactics.model;

public class Grid {
	public int x, y;
	public int num;		// holder for generic other number

	public Grid(int a, int b) {
		this.x = a;
		this.y = b;
		this.num = 0;
	}

	public Grid(int a, int b, int c) {
		this.x = a;
		this.y = b;
		this.num = c;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public int getNum() { return num; }
	public void setX(int a) { this.x = a; }
	public void setY(int b) { this.y = b; }
	public void setNum(int n) { this.num = n; }

	public String getString() {
		return "(" + x + ", " + y + ")";
	}
}