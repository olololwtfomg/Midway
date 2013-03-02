import java.util.ArrayList;
import java.util.List;

import usedConsts.Const;
import usedConsts.Heuristic;

public class Sector {
	
	/*
	 * TODO: hodnoty podla pravdepodobnostneho modelu
	 * */
	
	private int xPos = 0;  //column
	private int yPos = 0;  //row

	private int condition = 0;
	private boolean enemyShip = false;
	private int priority = Const.PRIOR_DEFAULT;  //0 - 100 ... 0 for blank, 50 standard shot, 80 high priority
	//set priority only to unknown sectors ... condition is superior else (not secured in setstats)
	
	int heurValue= 0; //for heuristics

	public Sector(int condition, int x, int y) {
		this.condition = condition;
		this.xPos = x;
		this.yPos = y;
	}
	public Sector(int condition, int priority, int x, int y) {
		this.condition = condition;
		this.priority = priority;
		this.xPos = x;
		this.yPos = y;
	}
	public int getXPos() {
		return this.xPos;
	}
	public int getYPos() {
		return this.yPos;
	}
	public int getCondition() {
		return this.condition;
	}
	
	public void setEnemyShip() {
		this.enemyShip = true;
	}
	public boolean isEnemyShip() {
		return this.enemyShip;
	}
	public void setStats(Integer newCondition, Integer newPriority) {
		if (newCondition != null) this.condition = newCondition;
		if (newPriority != null) this.priority = newPriority;
	}
	public int getPriority() {
		return this.priority;
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
		switch(this.getCondition())
		{
		// TODO: heuristicke hodnoty pre jednotlive polia
			case Const.CONDITION_ALLY_SHIP:
			case Const.CONDITION_ALLY_SUNK: 
				retval=Heuristic.OWN_SHIP; break;
//			case Const.CONDITION_ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
			case Const.CONDITION_SOME_SHOT: 
			case Const.CONDITION_ENEMY_SHOT: 
			case Const.CONDITION_OUR_SHOT: 
				retval=Heuristic.MISSED; break; 
			case Const.CONDITION_ENEMY_SUNK: 
				retval=Heuristic.HIT; break;
			case Const.CONDITION_BLANK:
			case Const.CONDITION_UNKNOWN:
			default:
				retval=Heuristic.UNKNOWN;
		}
		return retval;
	}
	
	public boolean isSectorKnown() {
		return this.getCondition() != Const.CONDITION_UNKNOWN;
	}
	public void shot() {
		this.setStats(Const.CONDITION_OUR_SHOT, Const.PRIOR_DEFAULT);
	}
		
	public List<Sector> getArroundEnemyShips(ActualStatus status) {
		List<Sector> list = new ArrayList<Sector>();
		
		return list;
	}
}
