import usedConsts.Const;
import usedConsts.Heuristic;

public class Sector {
	
	/*
	 * TODO: hodnoty podla pravdepodobnostneho modelu
	 * */
	
	int xPos = 0;  //column
	int yPos = 0;  //row

	int condition = 0;
	int priority = Const.PRIOR_UNKNOWN;  //0 - 100 ... 0 for blank, 50 standard shot, 80 high priority
	
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

	public void setCondition(int newCondition) {
		this.condition = newCondition;
	}
	public int getCondition() {
		return this.condition;
	}
	public void setPriority(int newPriority) {
		this.priority = newPriority;
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
			case Const.ALLY_SUNK: 
				retval=Heuristic.OWN_SHIP; break;
			case Const.CONDITION_ENEMY_SHIP: retval=Heuristic.ENEMY_SHIP; break;
			case Const.CONDITION_SOME_SHOT: 
			case Const.ENEMY_SHOT: 
			case Const.OUR_SHOT: 
				retval=Heuristic.MISSED; break; 
			case Const.ENEMY_SUNK: 
				retval=Heuristic.HIT; break;
			case Const.CONDITION_BLANK:
			case Const.UNKNOWN:
			default:
				retval=Heuristic.UNKNOWN;
		}
		return retval;
	}
	
	public boolean isSectorKnown() {
		return this.condition != Const.UNKNOWN && this.condition != Const.NEXT_ROUND_SHOT && this.condition != Const.CONDITION_ENEMY_SHIP;
	}
	public void shot() {
		if (!this.isSectorKnown() || this.condition == Const.CONDITION_BLANK) { this.condition = Const.OUR_SHOT; }
	}
	public void makeNearestNextShot(ActualStatus status) {
		int x = this.xPos, y = this.yPos;
		Sector near;  //        north      south     west       east
		int[][] nearest = { { x,y-1 }, { x,y+1 }, { x-1,y }, { x+1,y } }; 
		for (int i = 0; i<nearest.length; i++) {
			x = nearest[i][0];
			y = nearest[i][1];
			if (x<14 && y<14 && x>=0 && y>=0) {
				near = status.battlefield[x][y];
				if (near.condition == Const.UNKNOWN) { //unknown from system input
					if (near.priority > Const.PRIOR_MIN) {
						near.priority = Const.PRIOR_SOON;
						near.condition = Const.NEXT_ROUND_SHOT;
					}
					//case Const.NEXT_ROUND_SHOT: temp.condition = Const.PROBABLY_BLANK; break;
				}
			}
		}		
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
				case Const.UNKNOWN: 
					temp.condition = Const.CONDITION_BLANK; 
					temp.priority = Const.PRIOR_MIN;
					break;  //unknown from system input
				}
			}
		}
	}


}
