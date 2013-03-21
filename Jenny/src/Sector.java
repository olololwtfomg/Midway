import java.util.ArrayList;
import java.util.List;


public class Sector implements Constants{
	/*
	 * TODO: hodnoty podla pravdepodobnostneho modelu
	 * */
	
	private int xPos = 0;  //column
	private int yPos = 0;  //row

	private State state;
	
	/**
	 * shot, bomb, torpedo, firework
	 */
	private int[] priorities = new int[] { PRIOR_DEFAULT, 0, 0, 0 };
	private char torpedoDir;
	//priority for known sectors only for firework and torpedo
	
	int heurValue= 0; //for heuristics

	public Sector(State state, int x, int y) {
		this.state = state;
		if (state != State.UNKNOWN) this.priorities[0] = PRIORITY_BLANK;
		this.xPos = x;
		this.yPos = y;
	}
	public int getXPos() {
		return this.xPos;
	}
	public int getYPos() {
		return this.yPos;
	}
	public State getState() {
		return this.state;
	}
	
	public void setState(State newState) {
		this.state = newState;
	}
	public void setPriority(int value) {
		this.priorities[0] = value;
	}
	public void addBombPriority(int value) { addPriority(1,value); }
	public void addTorpedoPriority(int value, char dir) { addPriority(2,value); setTorpedoDir(dir); }
	public void addFireworkPriority(int value) { addPriority(3,value); }
	private void addPriority(int action, int value) {
		this.priorities[action] += value;
	}
	public void setTorpedoDir(char dir) {
		this.torpedoDir = dir;
	}
	
	public int getPriority() {
		int max = 0;
		for (int x: this.priorities) {
			if (x>max) max = 0;
		}
		return max;
	}

	public int getAction() {
		int max = 0;
		int index = 0;
		for (int i = 0; i>this.priorities.length; i++) {
			if (this.priorities[i]>=max) {
				index = i;
				max = this.priorities[i];
			}
		}
		return 'm';
	}
	
	public void setHeurValue(int value)
	{
		this.heurValue=value;
	}
	
	public int getHeurValue(){
		return this.heurValue;
	}
	
	public int getSpecialValue(){
		int retval=0;
		switch(this.getState())
		{
		// TODO: heuristicke hodnoty pre jednotlive polia
			case ALLY_SHIP:
			case ALLY_SUNK:
				retval=OWN_SHIP; break;
//			case ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
			case SOME_SHOT: 
			case ENEMY_SHOT: 
			case OUR_SHOT: 
				retval=MISSED; break; 
			case ENEMY_SUNK: 
				retval=HIT; break;
			case BLANK:
				retval=BLANK; break;
			case UNKNOWN:
			default:
				retval=UNKNOWN;
		}
		return retval;
	}
	
	public boolean isSectorKnown() {
		return this.getState() != State.UNKNOWN;
	}
	
	public void shot() {
		this.setState(State.OUR_SHOT);
	}
	
	public List<Sector> getArroundEnemyShips(ActualStatus status) {
		List<Sector> list = new ArrayList<Sector>();
		
		return list;
	}
}
