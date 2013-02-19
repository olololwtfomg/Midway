
public class Sector {
	int xPos = 0;
	int yPos = 0;
	int condition = 0;
	boolean highPriority = false;
	public Sector(int condition) {
		this.condition = condition;

	}
	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}

}
