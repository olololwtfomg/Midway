package usedConsts;

public interface Const {
	public static final int ALLY_SHIP=1;
	public static final int ENEMY_SHIP=2;  //status for max priority
	public static final int SOME_SHOT=3;
	public static final int OUR_SHOT=4;  //extends SOME_SHOT
	public static final int ENEMY_SHOT=5;  //extends SOME_SHOT
	public static final int ALLY_SUNK=6;
	public static final int ENEMY_SUNK=7;
	public static final int PROBABLY_BLANK=8;  //lowest priority
	public static final int NEXT_ROUND_SHOT=9;  //high priority
	public static final int UNKNOWN=0;  //possible for shot
	
	public static final int PRIORITY_DIFF = 10;
	public static final int PRIOR_MIN = 0;
	public static final int PRIOR_UNKNOWN = 50;
	public static final int PRIOR_SOON = 80;
	public static final int PRIOR_MAX = 100;
}
