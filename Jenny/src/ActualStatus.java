import java.util.ArrayList;
import java.util.List;

import usedConsts.Const;
import usedConsts.StatusConsts;

public class ActualStatus {

	private int defaultGrid = 3;
	
	public int side=0;
	public int round = 1;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	private String lastAction;
	
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
	public void setSector(State logCondition, int column, int battlefieldRow) {
		this.battlefield[column][battlefieldRow] = new Sector(logCondition, column, battlefieldRow);
	}
	
	public void setGrid(int grid) { this.defaultGrid = grid; }
	public int getGrid() { return this.defaultGrid; }
	
	public List<Sector> getNeighbors(Sector home, int[][] neighborsRelative) {  //neighbors in format { { x,y } } - relative to home
		List<Sector> list = new ArrayList<Sector>();
		for (int[] pos : neighborsRelative) {
			int x = pos[0] + home.getXPos();
			int y = pos[1] + home.getYPos();
			if (x < StatusConsts.SECTOR_SIZE && x >= 0 && y < StatusConsts.SECTOR_SIZE && y >= 0) {
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
			if (actual.getState() == State.UNKNOWN) {
				if (Const.HARD_DEBUG) System.err.println("Sector x" + actual.getXPos() + " y" + actual.getYPos() + " as next shot now.");
				actual.setPriority(Const.PRIOR_NEXT_SHOT);
			}
		}	
	}
	
	public static void makeBlank(Sector sector) {
		if (sector == null) return;		
		if (sector.getState() == State.UNKNOWN) {
			if (Const.HARD_DEBUG) System.err.println("Sector x" + sector.getXPos() + " y" + sector.getYPos() + " last condition: " + sector.getState() + " as blank now.");
			sector.setState(State.BLANK);
			sector.setPriority(Const.PRIORITY_BLANK);
		}
	}
	
	public static void makeBlank(List<Sector> list) {
		if (list == null) return;
		for (Sector actual: list) {
			ActualStatus.makeBlank(actual);
		}
	}

	public static void shotAll(List<Sector> list) {
		if (list == null) return;
		for (Sector actual: list) {
			if (actual.getState() == State.UNKNOWN) {
				if (Const.DEBUG) System.err.println("Shooting at x" + actual.getXPos() + " y" + actual.getYPos() + " last condition: " + actual.getState() + ".");
				actual.setState(State.OUR_SHOT);
			}
		}
	}

	public static boolean goodForBomb(List<Sector> list)
	{
		if (list == null) return false;
		int votes = 0;
		for (Sector actual: list) {
			switch(actual.getState()) {
			//proste ani za boha v tychto pripadoch
			case ALLY_SHIP:
			case ALLY_SUNK:
				return false;
			case UNKNOWN:
			case BLANK:
//			case Const.CONDITION_ENEMY_SHIP:
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
	
	public void addShip(Sector sector) {
		List<Sector> neighbors = this.getNeighbors(sector, Const.NEIGHBORS_BACKWARD);
		boolean inserted = false;
		for (Sector neighbor: neighbors) {
			if (neighbor.getState() == State.ENEMY_SUNK) {
				this.findEnemyShipBySector(neighbor).addPosition(sector);
				inserted = true;
			}
		}
		if (!inserted) enemyShipsList.add(new EnemyShip(sector));
		if (Const.HARD_DEBUG) System.err.println("adding ship at x" + sector.getXPos() + " y" + sector.getYPos());
	}
	
	/**
	 * 
	 * @param sector - check if sector was added to ships list enemyShip
	 */
	public EnemyShip findEnemyShipBySector(Sector sector) {
		for (EnemyShip ship: enemyShipsList) {
			if (ship.havePartOn(sector)) return ship;
		}
		return null;
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

	public boolean executeAction(Sector sector) {
		switch (sector.getAction()) {
		case Const.ACTION_BOMB:
			if (this.specialShots==0) return false;
			shotAll(this.getNeighbors(sector, Const.NEIGHBORS_BOMB));
			this.lastAction = String.format("%c %d %d", sector.getAction(), sector.getXPos(), sector.getYPos());
			return true;
		case Const.ACTION_SHOT:
			sector.shot();
			this.lastAction = String.format("%c %d %d", sector.getAction(), sector.getXPos(), sector.getYPos());
			return true;
		case Const.ACTION_TORPEDO:
			if (this.specialShots==0) return false;
		case Const.ACTION_FIREWORK:
			
			if (this.specialShots==0) return false;
		default:
			return false;
		}
		
		/*if (this.action == Const.ACTION_TORPEDO) {
			return String.format("%c %d %d %c", this.action, this.xPos, this.yPos, this.torpedoDir);
		}
		else return String.format("%c %d %d", this.action, this.xPos, this.yPos);*/

	}

	public String getActionWord() {
		return this.lastAction;
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


}
