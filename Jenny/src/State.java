/**
 * 
 */
public enum State {
	ALLY_SHIP(1), 
	BLANK(2), 
	SOME_SHOT(3), 
	OUR_SHOT(4),  //extends SOME_SHOT
	ENEMY_SHOT(5),  //extends SOME_SHOT
	ALLY_SUNK(6), 
	ENEMY_SUNK(7), 
	UNKNOWN(0);
	
	private int logValue;
	private State(int logValue) {
		this.logValue = logValue;
	}
	
	public int getLogValue() {
		return this.logValue;
	}
	
	public static State getState(char value) {
		switch (value) {
		case '1':
			return ALLY_SHIP; //ally ship, floating
		case '2':
			return BLANK;  //probably blank sector
		case '3':          //bad sector in log
		case '.':
			return SOME_SHOT;  //nothing, hit
		case '4':
			return OUR_SHOT;  //our shot on nothing  /extends 3
		case '5':
			return ENEMY_SHOT;  //enemy shot on nothing  /extends 3
		case '6':
		case '*':
			return ALLY_SUNK;  //ally ship, sunk
		case '7':
		case '+':
			return ENEMY_SUNK;  //enemy ship, sunk
		case '0':
		case ' ':
		default:
			return UNKNOWN;  //unknown
		}
	}
}
