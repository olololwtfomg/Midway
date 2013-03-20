import java.util.ArrayList;
import java.util.List;

import usedConsts.Const;
import usedConsts.StatusConsts;

public class ActualStatus {
	
	enum Grid {
		STATIC3, STATIC4, EVERY_SECOND
	}
	
	private static Grid grid = Grid.STATIC3;
	
	public static int side=0;
	public static int round = 1;
	public static int roundsToEnd = 0;
	public static int specialShots = 10;
	private static String lastAction;
	
	private static Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	protected static List<EnemyShip> enemyShipsList = new ArrayList<EnemyShip>();
	public static EnemyShip[] enemyShipsComplete = new EnemyShip[7];
	
	public static void enemyShipFinalize(EnemyShip ship) {
		switch (ship.getType()) {
		case SHIP_2x1:
			if (enemyShipsComplete[0] != null) enemyShipsComplete[0] = ship;
			else enemyShipsComplete[1] = ship;
			break;
		case SHIP_3x1:
			if (enemyShipsComplete[2] != null) enemyShipsComplete[2] = ship;
			else enemyShipsComplete[3] = ship;
			break;
		case SHIP_4x1:
			enemyShipsComplete[4] = ship;
			break;
		case SHIP_5x1:
			enemyShipsComplete[5] = ship;
			break;
		case SHIP_2x3:
			enemyShipsComplete[6] = ship;
			break;
		default:
			break;
		}
	}
	
	public static boolean enemyShipIsMissing(EnemyShip.Type ship) {
		switch (ship) {
		case SHIP_2x1:
			return (enemyShipsComplete[0] == null) || (enemyShipsComplete[1] == null); 
		case SHIP_3x1:
			return (enemyShipsComplete[2] == null) || (enemyShipsComplete[3] == null);
		case SHIP_4x1:
			return (enemyShipsComplete[4] == null);
		case SHIP_5x1:
			return (enemyShipsComplete[5] == null);
		case SHIP_2x3:
			return (enemyShipsComplete[6] == null);
		default:
			return false;
		}
	}
	
	public static boolean enemyShipIsLongestMissing(EnemyShip.Type ship) {
		boolean temp = enemyShipIsMissing(ship);
		switch (ship) {
		case SHIP_2x1:
			temp = ( (enemyShipsComplete[2] != null) && (enemyShipsComplete[3] != null) );
		case SHIP_3x1:
			if (temp) temp = (enemyShipsComplete[4] != null);
		case SHIP_4x1:
			if (temp) temp = (enemyShipsComplete[5] != null);
		case SHIP_5x1:
			break;
		case SHIP_2x3:
		default:
			return false;
		}
		return temp;
	}

	public static int[] findAirstrikePos(){
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
	public static Sector getSector(int x, int y) {
		return battlefield[x][y];
	}
	public static void setSector(State logCondition, int column, int battlefieldRow) {
		battlefield[column][battlefieldRow] = new Sector(logCondition, column, battlefieldRow);
	}
	
	public static void setGrid(Grid newGrid) { grid = newGrid; }
	public static Grid getGrid() { return grid; }
	
	public static int[] getSectorDistances(Sector a, Sector b) {
		return new int[] { Math.abs(a.getXPos() - b.getXPos()) ,  Math.abs(a.getYPos() - b.getYPos()) };
	}
	
	public static List<Sector> getNeighbors(Sector home, int[][] neighborsRelative) {  //neighbors in format { { x,y } } - relative to home
		List<Sector> list = new ArrayList<Sector>();
		for (int[] pos : neighborsRelative) {
			int x = pos[0] + home.getXPos();
			int y = pos[1] + home.getYPos();
			if (x < StatusConsts.SECTOR_SIZE && x >= 0 && y < StatusConsts.SECTOR_SIZE && y >= 0) {
				list.add(getSector(x, y));
			}
		}
		return list.size() > 0 ? list : null;
	}
		
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
	
	public static EnemyShip addShip(Sector sector) {
		List<Sector> neighbors = getNeighbors(sector, Const.NEIGHBORS_BACKWARD);
		EnemyShip newShip = null;
		for (Sector neighbor: neighbors) {
			if (neighbor.getState() == State.ENEMY_SUNK) {
				EnemyShip temp = findEnemyShipBySector(neighbor);
				if (temp != null) {
					newShip = temp;
					temp.addPosition(sector);
				}

			}
		}
		if (newShip == null) {
			newShip = new EnemyShip(sector);
			enemyShipsList.add(newShip);
		}
		if (Const.HARD_DEBUG) System.err.println("adding ship at x" + sector.getXPos() + " y" + sector.getYPos());
		return newShip;
	}
	
	/**
	 * 
	 * @param sector - check if sector was added to ships list
	 */
	public static EnemyShip findEnemyShipBySector(Sector sector) {
		for (EnemyShip ship: enemyShipsList) {
			if (ship.havePartOn(sector)) return ship;
		}
		return null;
	}

	public static int getRound() {
		return round;
	}
	public static int getSpecialShots() {
		return specialShots;
	}
	public static int getSide() {
		return side;
	}

	public static boolean executeAction(Sector sector) {
		switch (sector.getAction()) {
		case Const.ACTION_BOMB:
			if (specialShots==0) return false;
			shotAll(getNeighbors(sector, Const.NEIGHBORS_BOMB));
			lastAction = String.format("%c %d %d", sector.getAction(), sector.getXPos(), sector.getYPos());
			return true;
		case Const.ACTION_SHOT:
			sector.shot();
			lastAction = String.format("%c %d %d", sector.getAction(), sector.getXPos(), sector.getYPos());
			return true;
		case Const.ACTION_TORPEDO:
			if (specialShots==0) return false;
		case Const.ACTION_FIREWORK:
			
			if (specialShots==0) return false;
		default:
			return false;
		}
		
		/*if (this.action == Const.ACTION_TORPEDO) {
			return String.format("%c %d %d %c", this.action, this.xPos, this.yPos, this.torpedoDir);
		}
		else return String.format("%c %d %d", this.action, this.xPos, this.yPos);*/

	}

	public static String getActionWord() {
		return lastAction;
	}


	public static void print_heuristics()
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
