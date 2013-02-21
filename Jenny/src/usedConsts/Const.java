package usedConsts;

public interface Const {
	public static final int ALLY_SHIP=1;
	public static final int ENEMY_SHIP=2;
	public static final int NOTHING_HIT=3;
	public static final int OUR_SHOT=4;
	public static final int ENEMY_SHOT=5;
	public static final int ALLY_SUNK=6;
	public static final int ENEMY_SUNK=7;
	public static final int PROBABLY_BLANK=8;
	public static final int NEXT_ROUND_SHOT=9;
	public static final int UNKNOWN=0;
	
	public static final int PRIORITY_DIFF = 10;
	public static final int PRIOR_MIN = 0;
	public static final int PRIOR_UNKNOWN = 50;
	public static final int PRIOR_SOON = 80;
	public static final int PRIOR_MAX = 100;
}
