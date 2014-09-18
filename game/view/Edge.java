package tactics.view;

import java.awt.Point;

public class Edge {
	public Point start, end;
	
	public Edge(Point a, Point b) {
		start = a;
		end = b;
	}

	public boolean compare(Edge other) {
		if ((this.start.equals(other.start) && 
			this.end.equals(other.end)) ||
			(this.start.equals(other.end) &&
			this.end.equals(other.start))) {
			return true;
		} else {
			return false;
		}
	}
}