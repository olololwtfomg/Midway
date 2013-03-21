
public interface Constants {
	
	public static final boolean DEBUG = true;
	public static final boolean HARD_DEBUG = false;
	public static final boolean TIMER = true;
	//StatusConstants
	
	public static final int SECTOR_SIZE=14;

	//Priority Constants
	public static final int PRIORITY_BLANK = 0;
	public static final int PRIOR_NEXT_SHOT = 80;
	public static final int PRIOR_DEFAULT = 5;
	public static final int PRIORITY_DIFF = 5;
	public static final int PRIOR_GRID_MIN = 10;  //max = min + (grid+1)*diff; 10+(4+1)*5 == 35
	
	/**
	 * in percent
	 */
	public static final int NOISE_CHANCE = 15;
	
	public static final int[][] NEIGHBORS_LINEAR = { {0, -1}, {0, 1}, { -1, 0}, {1, 0} };
//	public static final int[][] NEIGHBORS_LINEAR_STEP = { {0, -2}, {0, 2}, {-2, 0}, {2, 0} };
	public static final int[][] NEIGHBORS_DIAGONAL = { {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_ARROUND = { {0, -1}, {0, 1}, { -1, 0}, {1, 0}, {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_ARROUND_NEXT = { {2,-1}, {2,0}, {2,1}, {2,2}, {1,2}, {0,2}, {-1,2}, {-2,2}, {-2,1}, {-2,0}, {-2,-1}, {-2,-2}, {-1,-2}, {0,-2}, {1,-2}, {2,-2} };
	public static final int[][] NEIGHBORS_BOMB = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };
	public static final int[][] NEIGHBORS_BACKWARD = { {0, -1}, {-1, 0} };
	public static final int[][] NEIGHBORS_LASTTHREE = { {-1,0}, {-2,0}, {-3,0}, {0,-1}, {0,-2}, {0,-3} };
	
	
	public static final char ACTION_SHOT = 'm';
	public static final char ACTION_BOMB = 'b';
	public static final char ACTION_TORPEDO = 't';
	public static final char ACTION_FIREWORK = 'f';
	
	public static final char TORPEDO_UP = 'u';
	public static final char TORPEDO_RIGHT = 'r';
	public static final char TORPEDO_DOWN = 'd';
	public static final char TORPEDO_LEFT = 'l';

	//Heuristics
	public static final int OWN_SHIP=100; //this is a very bad situation
	public static final int BLANK = 100;
	public static final int MISSED=20;
	public static final int HIT=MISSED+10;		
	public static final int UNKNOWN=0;
	public static final int HEUR_OFFSET=2; 
	public static final int HEUR_THRESHOLD=OWN_SHIP;

	
}
