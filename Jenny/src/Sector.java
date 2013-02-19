
public class Sector {
	int xPos = 0;
	int yPos = 0;
	
	//1 for own ship, 2 for enemy ship
	//3 for unknown shot, 4 for own shot, 5 for enemy shot
	//6 for ally ship hit, 7 for enemy ship hit,
	//8 for lowest priority, 9 for high priority, 0 for unknown
	int condition = 0;
	
	boolean highPriority = false;
	public Sector(int condition) {
		this.condition = condition;
	}
	public Sector(int condition, int x, int y) {
		this.condition = condition;
		this.xPos = x;
		this.yPos = y;
	}

	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}

}
