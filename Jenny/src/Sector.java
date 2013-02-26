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
	private EnemyShip enemyShip;
	private int priority = Const.PRIOR_UNKNOWN;  //0 - 100 ... 0 for blank, 50 standard shot, 80 high priority
	
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
			case Const.CONDITION_ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
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
		return this.getCondition() != Const.CONDITION_UNKNOWN && this.getCondition() != Const.CONDITION_NEXT_SHOT && this.getCondition() != Const.CONDITION_ENEMY_SHIP;
	}
	public void shot() {
		if (!this.isSectorKnown() || this.condition == Const.CONDITION_BLANK) { this.setStats(Const.CONDITION_OUR_SHOT, null); }
	}
	
	public void setEnemyShip(EnemyShip enemyShip) {
		this.enemyShip = enemyShip;
	}
	
	public boolean isEnemyShip() {
		return this.enemyShip != null;
	}
	
	public List<Sector> getArroundEnemyShips(ActualStatus status) {
		List<Sector> list = new ArrayList<Sector>();
		
		return list;
	}
	
	public void makeNearestBlank(ActualStatus status) {
		int x = this.xPos, y = this.yPos;
		Sector temp;  //        north      south     west       east
		int[][] nearest = { { x,y-1 }, { x,y+1 }, { x-1,y }, { x+1,y }, { x-1,y+1 }, { x+1, y+1 }, { x+1, y-1 }, {x-1, y-1} }; 
		for (int i = 0; i<nearest.length; i++) {
			x = nearest[i][0];
			y = nearest[i][1];
			if (x<14 && y<14 && x>=0 && y>=0) {
				temp = status.battlefield[x][y];
				switch (temp.condition) {
				case Const.CONDITION_UNKNOWN: 
					temp.condition = Const.CONDITION_BLANK; 
					temp.priority = Const.PRIOR_MIN;
					break;  //unknown from system input
				}
			}
		}
	}
	
	public boolean goodForBomb(Sector targeted, ActualStatus status)
	{
		Sector currSector;
		int x=targeted.getXPos();
		int y=targeted.getYPos();
		int votes=0;
		int[][] nearest = { { x,y }, { x,y+1 }, { x,y+1 }, { x+1,y+1 } }; 
		for (int i = 0; i<nearest.length; i++) {
			x = nearest[i][0];
			y = nearest[i][1];
			try{
				currSector=status.battlefield[x][y];
			}
			catch (IndexOutOfBoundsException e)
			{
				continue;
			}
			switch(currSector.getCondition())
			{
				//proste ani za boha v tychto pripadoch
				case Const.CONDITION_ALLY_SHIP:
				case Const.CONDITION_ALLY_SUNK:
					return false;
				case Const.CONDITION_UNKNOWN:
				case Const.CONDITION_BLANK:
				case Const.CONDITION_ENEMY_SHIP:
					votes++;
				default:
					votes+=0;
			}
		}
		if (votes>=2){
			return true;
		}
		else{
			return false;
		}
	}

}
