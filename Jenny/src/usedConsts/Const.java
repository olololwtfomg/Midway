package usedConsts;

public interface Const {
	public static final boolean DEBUG = true;
	public static final boolean HARD_DEBUG = false;
	
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
	public static final int[][] NEIGHBORS_LINEAR_STEP = { {0, -2}, {0, 2}, {-2, 0}, {2, 0} };
	public static final int[][] NEIGHBORS_DIAGONAL = { {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_ARROUND = { {0, -1}, {0, 1}, { -1, 0}, {1, 0}, {1, -1}, {1, 1}, {-1, 1}, {-1, -1} };
	public static final int[][] NEIGHBORS_BOMB = { {0, 0}, {1, 0}, {0, 1}, {1, 1} };
	public static final int[][] NEIGHBORS_BACKWARD = { {0, -1}, {-1, 0} };
	
	
	public static final char ACTION_SHOT = 'm';
	public static final char ACTION_BOMB = 'b';
	public static final char ACTION_TORPEDO = 't';
	public static final char ACTION_FIREWORK = 'f';
	
	public static final char TORPEDO_UP = 'u';
	public static final char TORPEDO_RIGHT = 'r';
	public static final char TORPEDO_DOWN = 'd';
	public static final char TORPEDO_LEFT = 'l';
    
}
