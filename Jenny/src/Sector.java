

public class Sector {
	
	public interface SectorConsts{
		public static final int OWN_SHIP=100;
		public static final int ENEMY_SHIP=10;
		public static final int MISSED=25;
		public static final int UNKNOWN=0;
	}
	
	int xPos = 0;
	int yPos = 0;
	int bomb = 0; //0 for unexplored, 1 for blend, 2 for hit, 3 for self hit, 4 for enemy hit
	int ship = 0; //0 for unknown, 1 for clear, 2 for self, 3 for enemy
	int heurValue= 0; //for heuristics
	int specialValue=0;
	public Sector(int ship, int bomb) {
		this.bomb = bomb;
		this.ship = ship;
		switch(this.ship)
		{
		case 3: this.specialValue=SectorConsts.OWN_SHIP; break;
		case 2: this.specialValue=SectorConsts.ENEMY_SHIP; break;
		case 1: this.specialValue=SectorConsts.MISSED; break;
		case 0:
		default:
			this.specialValue=SectorConsts.UNKNOWN;
		}
	}
	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	public void setHeurValue(int value)
	{
		this.heurValue=value;
	}
	public int getSpecialValue(){
		return this.specialValue; 
	}

}
