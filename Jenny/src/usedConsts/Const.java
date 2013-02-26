package usedConsts;

public interface Const {
	public static final boolean DEBUG = false;
	public static final boolean HARD_DEBUG = false;
	public static final boolean TIMER = true;
	
	public static final int CONDITION_ALLY_SHIP=1;
	public static final int CONDITION_ENEMY_SHIP=2;  //status for max priority
	public static final int CONDITION_SOME_SHOT=3;
	public static final int CONDITION_OUR_SHOT=4;  //extends SOME_SHOT
	public static final int CONDITION_ENEMY_SHOT=5;  //extends SOME_SHOT
	public static final int CONDITION_ALLY_SUNK=6;
	public static final int CONDITION_ENEMY_SUNK=7;
	public static final int CONDITION_BLANK=8;  //lowest priority
	public static final int CONDITION_NEXT_SHOT=9;  //high priority
	public static final int CONDITION_UNKNOWN=0;  //possible for shot
	
	public static final int PRIORITY_DIFF = 5;
	public static final int PRIOR_MIN = 0;
	public static final int PRIOR_UNKNOWN = 50;
	public static final int PRIOR_SOON = 80;
	public static final int PRIOR_MAX = 100;
	public static final int PRIOR_FINESTLEVEL = PRIOR_UNKNOWN + PRIORITY_DIFF;  //grid that finds everything
	public static final int PRIOR_FOURTHLEVEL = PRIOR_FINESTLEVEL + PRIORITY_DIFF;
	public static final int PRIOR_THIRDLEVEL = PRIOR_FOURTHLEVEL + PRIORITY_DIFF;
	public static final int PRIOR_SECONDLEVEL = PRIOR_THIRDLEVEL + PRIORITY_DIFF;
	public static final int PRIOR_FIRSTLEVEL = PRIOR_SECONDLEVEL + PRIORITY_DIFF;
	
	
	public static final char ACTION_SHOT = 'm';
	public static final char ACTION_BOMB = 'b';
	public static final char ACTION_TORPEDO = 't';
	public static final char ACTION_FIREWORK = 'f';
	
	public static final char TORPEDO_UP = 'u';
	public static final char TORPEDO_RIGHT = 'r';
	public static final char TORPEDO_DOWN = 'd';
	public static final char TORPEDO_LEFT = 'l';
    
}
