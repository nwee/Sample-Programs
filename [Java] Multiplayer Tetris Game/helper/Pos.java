package helper;

public class Pos {
	public int x;
	public int y;
	
	@SuppressWarnings("unused")
	private Pos() { }
	
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
}
