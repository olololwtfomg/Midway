

public class Sector {
	
	public interface SectorConsts{
		public static final int OWN_SHIP=100;
		public static final int ENEMY_SHIP=10;
		public static final int MISSED=25;
		public static final int UNKNOWN=0;
	}
	
	int xPos = 0;
	int yPos = 0;
	
	//1 for own ship, 2 for enemy ship
	//3 for unknown shot, 4 for own shot, 5 for enemy shot
	//6 for ally ship hit, 7 for enemy ship hit,
	//8 for lowest priority, 9 for high priority, 0 for unknown
	int condition = 0;
	
	int heurValue= 0; //for heuristics
	
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
	public void setHeurValue(int value)
	{
		this.heurValue=value;
	}
	public int getSpecialValue(){
		int retval=0;
		switch(this.condition)
		{
		// TODO: heuristicke hodnoty pre jednotlive polia
			case 3: retval=SectorConsts.OWN_SHIP; break;
			case 2: retval=SectorConsts.ENEMY_SHIP; break;
			case 1: retval=SectorConsts.MISSED; break;
			case 0:
			default:
				retval=SectorConsts.UNKNOWN;
		}
		return retval;
	}

}
