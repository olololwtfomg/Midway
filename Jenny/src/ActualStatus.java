import java.util.ArrayList;
import java.util.List;

import usedConsts.Const;
import usedConsts.StatusConsts;

public class ActualStatus {

	public int side=0;
	public int round = 1;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	private int actionX;
	private int actionY;
	private char action;
	private char torpedoDir;
	private Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	private List<EnemyShip> enemyShipsList = new ArrayList<EnemyShip>();
	private int found2x1 = 0;
	private int found3x1 = 0;
	private int found4x1 = 0;
	private int found5x1 = 0;
	private int found3x2 = 0;


	public int[] findAirstrikePos(){
		int PosBest[]= {15,15};
		int best=StatusConsts.HEUR_THRESHOLD+1;
		int currHeurValue;

		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				currHeurValue=battlefield[xAxis][yAxis].getHeurValue();
				if(currHeurValue<=StatusConsts.HEUR_THRESHOLD)
				{
					if(currHeurValue<best)
					{
						PosBest[0]=xAxis;
						PosBest[1]=yAxis;
						best=currHeurValue;
					}

				}
			}
		}
		return PosBest;
	}
	public Sector getSector(int x, int y) {
		return this.battlefield[x][y];
	}
	public void setSector(int logCondition, int priorTemp, int column, int battlefieldRow) {
		this.battlefield[column][battlefieldRow] = new Sector(logCondition, priorTemp, column, battlefieldRow);
	}
	public void setSector(int logCondition, int column, int battlefieldRow) {
		this.battlefield[column][battlefieldRow] = new Sector(logCondition, column, battlefieldRow);
	}

	public List<Sector> getNeighbors(Sector home, int[][] neighborsRelative) {  //neighbors in format { { x,y } } - relative to home
		List<Sector> list = new ArrayList<Sector>();
		for (int[] pos : neighborsRelative) {
			int x = pos[0] + home.getXPos();
			int y = pos[1] + home.getYPos();
			if ( x < 14 && x >= 0 && y <14 && y >= 0) {
				list.add(this.getSector(x, y));
			}
		}
		return list.size() > 0 ? list : null;
	}
	public int founded2x1() { return this.found2x1; }
	public int founded3x1() { return this.found3x1; }
	public int founded4x1() { return this.found4x1; }
	public int founded5x1() { return this.found5x1; }
	public int founded3x2() { return this.found3x2; }
	
	public static void makeNextShot(List<Sector> list) {
		if (list == null) return;
		for (Sector actual: list) {
			if (actual.getCondition() == Const.CONDITION_UNKNOWN) {
				if (Const.HARD_DEBUG) System.err.println("Sector x" + actual.getXPos() + " y" + actual.getYPos() + " last condition: " + actual.getCondition() + " as next shot now."); 
				actual.setStats(Const.CONDITION_NEXT_SHOT, null);
			}
		}	
	}

	public static void makeBlank(List<Sector> list) {
		if (list == null) return;
		for (Sector actual: list) {
			switch (actual.getCondition()) {
			case Const.CONDITION_UNKNOWN:
			case Const.CONDITION_NEXT_SHOT:  //can by set while iterating
				if (Const.HARD_DEBUG) System.err.println("Sector x" + actual.getXPos() + " y" + actual.getYPos() + " last condition: " + actual.getCondition() + " as blank now.");
				actual.setStats(Const.CONDITION_BLANK, null);
			}
		}
	}

	public static boolean goodForBomb(List<Sector> list)
	{
		if (list == null) return false;
		int votes = 0;
		for (Sector actual: list) {
			switch(actual.getCondition()) {
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


	public int getRound() {
		return this.round;
	}
	public int getSpecialShots() {
		return this.specialShots;
	}
	public int getSide() {
		return this.side;
	}

	public boolean setAction(int x, int y, char shot) { return setAction(x, y, shot, '0'); }
	public boolean setAction(int x, int y, char shot, char dir) {
		this.actionX = x;
		this.actionY = y;
		this.action = shot;
		this.torpedoDir = dir;
		switch (shot) {
		case Const.ACTION_BOMB:
			if (this.specialShots==0) return false;
			this.battlefield[this.actionX+1][this.actionY].shot();
			this.battlefield[this.actionX][this.actionY+1].shot();
			this.battlefield[this.actionX+1][this.actionY+1].shot();
		case Const.ACTION_SHOT:
			this.battlefield[this.actionX][this.actionY].shot();
			return true;
		case Const.ACTION_TORPEDO:
			if (this.specialShots==0) return false;
		case Const.ACTION_FIREWORK:
			if (this.specialShots==0) return false;
		default:
			return false;
		}
	}
	public String getActionWord() {
		if (this.action == Const.ACTION_TORPEDO) {
			return String.format("%c %d %d %c", this.action, this.actionX, this.actionY, this.torpedoDir);
		}
		else return String.format("%c %d %d", this.action, this.actionX, this.actionY);
	}

	public Sector getActionPos() {
		return this.battlefield[this.actionX][this.actionY];
	}
	public char getActionType() {
		return this.action;
	}
	public char getTorpedoDirection() {
		return this.torpedoDir;
	}

	public void calculateHeuristics()
	{
		Sector currSector;
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				/*there's no point in calculating it over and over again
				 * if we know it's a bad location to do an air strike*/
				currSector=battlefield[xAxis][yAxis];
				if(currSector.getHeurValue()<StatusConsts.HEUR_THRESHOLD){
					currSector.setHeurValue(
							calculateSectorHeuristics(xAxis,yAxis));
				}
				battlefield[xAxis][yAxis]=currSector;
			}
		}
	}

	public void print_heuristics()
	{
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				System.out.printf("%4d",battlefield[xAxis][yAxis].getHeurValue());
			}
			System.out.println();
		}
	}

	private int calculateSectorHeuristics(int x, int y)
	{
		int value;
		int retval=0;
		for(int xAxis=(x-StatusConsts.HEUR_OFFSET);
				xAxis<(x+StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=y-StatusConsts.HEUR_OFFSET;
					yAxis<(y+StatusConsts.HEUR_OFFSET);
					yAxis++){
				if(retval>StatusConsts.HEUR_THRESHOLD)
				{
					return retval;
				}
				try{
					value=battlefield[xAxis][yAxis].getSpecialValue();
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					value=100;
				}
				retval+=value;
			}
		}
		return retval;
	}

}
