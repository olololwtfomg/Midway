import usedConsts.Heuristic;
import usedConsts.ConditionConstants;

public class Sector {
	
	/*
	 * TODO: hodnoty podla pravdepodobnostneho modelu
	 * */
	
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
	
	public int getHeurValue(){
		return this.heurValue;
	}
	public int getSpecialValue(){
		int retval=0;
		switch(this.condition)
		{
		// TODO: heuristicke hodnoty pre jednotlive polia
			case ConditionConstants.ALLY_SHIP:
			case ConditionConstants.ALLY_SUNK: 
				retval=Heuristic.OWN_SHIP; break;
			case ConditionConstants.ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
			case ConditionConstants.NOTHING_HIT: 
			case ConditionConstants.ENEMY_NOTHING: 
			case ConditionConstants.OUR_NOTHING: 
				retval=Heuristic.MISSED; break; 
			case ConditionConstants.ENEMY_SUNK: 
				retval=Heuristic.HIT; break;
			case ConditionConstants.PROBABLY_BLANK:
			case ConditionConstants.UNKNOWN:
			default:
				retval=Heuristic.UNKNOWN;
		}
		return retval;
	}

}
