import usedConsts.Const;
import usedConsts.Heuristic;

public class Sector {
	
	/*
	 * TODO: hodnoty podla pravdepodobnostneho modelu
	 * */
	
	int xPos = 0;
	int yPos = 0;
	char action = '0'; //for trading to main
	char torpedoDir = '0';

	int condition = 0;
	int priority = 50;  //0 - 100 ... 0 for blank, 50 standard shot, 80 high priority
	
	int heurValue= 0; //for heuristics

	public Sector(int condition) {
		this.condition = condition;
	}
	public Sector(int condition, int x, int y) {
		this.condition = condition;
		this.xPos = x;
		this.yPos = y;
	}
	public Sector(int condition, int priority, int x, int y) {
		
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
			case Const.ALLY_SHIP:
			case Const.ALLY_SUNK: 
				retval=Heuristic.OWN_SHIP; break;
			case Const.ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
			case Const.SOME_SHOT: 
			case Const.ENEMY_SHOT: 
			case Const.OUR_SHOT: 
				retval=Heuristic.MISSED; break; 
			case Const.ENEMY_SUNK: 
				retval=Heuristic.HIT; break;
			case Const.PROBABLY_BLANK:
			case Const.UNKNOWN:
			default:
				retval=Heuristic.UNKNOWN;
		}
		return retval;
	}

}
