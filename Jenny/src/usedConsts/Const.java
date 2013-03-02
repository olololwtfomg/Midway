package usedConsts;

public interface Const {
	public static final boolean DEBUG = true;
	public static final boolean HARD_DEBUG = false;
	public static final boolean TIMER = true;
	
	//final stats are conditions ... others are priority
	public static final int CONDITION_ALLY_SHIP=1;
	public static final int CONDITION_BLANK=2;  //lowest priority
	public static final int CONDITION_SOME_SHOT=3;
	public static final int CONDITION_OUR_SHOT=4;  //extends SOME_SHOT
	public static final int CONDITION_ENEMY_SHOT=5;  //extends SOME_SHOT
	public static final int CONDITION_ALLY_SUNK=6;
	public static final int CONDITION_ENEMY_SUNK=7;
	public static final int CONDITION_NEXT_SHOT=8;  //high priority
	public static final int CONDITION_UNKNOWN=0;  //possible for shot
	
	public static final int PRIORITY_BLANK = 0;
	public static final int PRIOR_ENEMY_SHIP = 100; //status for known ships positions
	public static final int PRIOR_DEFAULT = 5;
	public static final int PRIORITY_DIFF = 5;
	public static final int PRIOR_GRID_MIN = 10;  //max = min + (grid+1)*diff; 10+(4+1)*5 == 35 
	
	public static final int[][] NEIGHBORS_LINEAR = { {0, -1}, {0, 1}, { -1, 0}, {1, 0} };
	public static final int[][] NEIGHBORS_LINEAR_STEP = { {0, -2}, {0, 2}, {-2, 0}, {2, 0} };
	public static final int[][] NEIGHBORS_DIAGONAL = { {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_ARROUND = { {0, -1}, {0, 1}, { -1, 0}, {1, 0}, {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_BOMB = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };
	public static final int[][] NEIGHBORS_BACKWARD = { { } };
	
	public static final char ACTION_SHOT = 'm';
	public static final char ACTION_BOMB = 'b';
	public static final char ACTION_TORPEDO = 't';
	public static final char ACTION_FIREWORK = 'f';
	
	public static final char TORPEDO_UP = 'u';
	public static final char TORPEDO_RIGHT = 'r';
	public static final char TORPEDO_DOWN = 'd';
	public static final char TORPEDO_LEFT = 'l';
    
}
