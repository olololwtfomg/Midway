
public class Sector {
	int xPos = 0;
	int yPos = 0;
	int bomb = 0; //0 for unexplored, 1 for blend, 2 for hit, 3 for self hit, 4 for enemy hit
	int ship = 0; //0 for unknown, 1 for clear, 2 for self, 3 for enemy
	public Sector(int ship, int bomb) {
		this.bomb = bomb;
		this.ship = ship;
	}
	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}

}