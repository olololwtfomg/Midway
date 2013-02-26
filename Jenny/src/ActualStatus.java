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
	public Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];

	//status variables:


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

	public List<Sector> getNeighbors(Sector home, int mode) {  //1 for linear, 2 linear+diagonal
		if (!(mode == 1 || mode == 2)) {
			System.err.println("ROUND " + this.getRound() + " Bad parameter for getNeighbors: " + mode);
			return null;
		}
		List<Sector> list = new ArrayList<Sector>();
		//                                  north                                  south                                west                                  east        
		int[][] nearestPos = { { home.getXPos(),home.getYPos()-1 }, { home.getXPos(),home.getYPos()+1 }, { home.getXPos()-1,home.getYPos() }, { home.getXPos()+1,home.getYPos() }
		//        northeast                                   southeast                                southwest                               northwest
		, {home.getXPos()+1,home.getYPos()-1}, { home.getXPos()+1, home.getYPos()+1 }, { home.getXPos()-1, home.getYPos()+1 }, { home.getXPos()-1, home.getYPos()+1 } };
		for (int i = 0; i<mode*4; i++) {
			if ( nearestPos[i][0] < 14 && nearestPos[i][0] >= 0 && nearestPos[i][1] <14 && nearestPos[i][1] >= 0) {
				list.add(this.battlefield[nearestPos[i][0] ] [ nearestPos[i][1] ]);
			}
		}
		return list.size() > 0 ? list : null;
	}

	public static void makeNextShot(List<Sector> list) {
		if (list == null) return;
		for (Sector actual: list) {
			if (actual.getCondition() == Const.CONDITION_UNKNOWN) {
				actual.setStats(Const.CONDITION_NEXT_SHOT, Const.PRIOR_SOON);
				if (Const.DEBUG) System.err.println("Sector as next shot: x" + actual.getXPos() + " y" + actual.getYPos() + " set to priority " + actual.getPriority()); 
			}
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
