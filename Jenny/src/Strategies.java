import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usedConsts.Const;
import usedConsts.StatusConsts;


public class Strategies {


	public static void doSomeLogic() {
		Sector actionSector;

		setEnemyShips();
		setGridPriorities();  //gridMin - gridMin+5*gridDiff

		actionSector = getRandomSectorByPriority();
		ActualStatus.executeAction(actionSector);
	}

	private static void setEnemyShips() {
		List<Sector> enemySectors = getSectorsByState(State.ENEMY_SUNK);
		List<Sector> neighbors;
		for (Sector sunken: enemySectors) {  //find borders of sector
			
			EnemyShip actualShip = ActualStatus.addShip(sunken);
			
			neighbors = ActualStatus.getNeighbors(sunken, Const.NEIGHBORS_LINEAR);
			for (Sector neighbor: neighbors) {
				switch (neighbor.getState()) {
				case SOME_SHOT: //luck proof (log bad loaded)
					System.err.println("Found SOME_SHOT at x" + neighbor.getXPos() + " y" + neighbor.getYPos());
					break;
				case ALLY_SHIP:
				case ALLY_SUNK:
					System.err.println("Error in loaded status - ally ship neighbor of enemy ship at x" + sunken.getXPos() + " y" + sunken.getYPos());
					break;
				case ENEMY_SUNK:
				case UNKNOWN:
					break;
				default:
					for (int i = -1; i<=1; i+=2) {
						Sector temp = ActualStatus.getSector(
								(neighbor.getXPos() - sunken.getXPos()) == 0 ? (sunken.getXPos() + i) : neighbor.getXPos(), 
										(neighbor.getYPos() - sunken.getYPos()) == 0 ? (sunken.getYPos() + i) : neighbor.getYPos() 
								); //sektor kolmo na sunken a neighbor
						if (temp != null) ActualStatus.makeBlank(temp);
					}
					//set borders
					switch (neighbor.getXPos() - sunken.getXPos()) {
					case -1:
						actualShip.borderWest(neighbor);
						break;
					case 0:
						switch (neighbor.getYPos() - sunken.getYPos()) {
						case -1:
							actualShip.borderNorth(neighbor);
							break;
						case 1:
							actualShip.borderSouth(neighbor);
							break;
						default: System.err.println("Error while finding neighbor");
						}
					case 1:
						actualShip.borderEast(neighbor);
						break;
					default: System.err.println("Error while finding neighbor");
					}
					if ((neighbor.getXPos() - sunken.getXPos()) == 0)
					break;
				}
			}
			//advanced finding (neighbor one step away)
		}
	}
	
	public void calculateHeuristics()
	{
		Sector currSector;
		SectorIterator iterator = new SectorIterator();
		while ((currSector = iterator.nextSector()) != null) {
			/*there's no point in calculating it over and over again
			 * if we know it's a bad location to do an air strike*/
			if(currSector.getHeurValue()<StatusConsts.HEUR_THRESHOLD){
				currSector.setHeurValue(
						calculateSectorHeuristics(currSector,Const.NEIGHBORS_ARROUND_NEXT));
			}			
		}
	}
	private int calculateSectorHeuristics(Sector sector, int[][] neighborsRelative)
	{
		int retval=0;
		List<Sector> neighbors = ActualStatus.getNeighbors(sector, neighborsRelative);
		for (Sector neighbor: neighbors) {
			if (retval>=StatusConsts.HEUR_THRESHOLD) return retval;
			retval += neighbor.getSpecialValue();
		}
		return retval;
	}

	
	private static void setGridPriorities() {
		SectorIterator iterator = new SectorIterator();
		Sector actual;
		while((actual = iterator.nextSector()) != null) {
			if (actual.isSectorKnown()) continue;
			actual.setPriority((Const.PRIOR_GRID_MIN + getGridLvlForSector(actual)*Const.PRIORITY_DIFF) );
		}
	}

	public static int getGridLvlForSector(Sector sector) {
		int xDiff, yDiff;
		List<Sector> neighbors;
		switch (ActualStatus.getGrid()) {
		case STATIC3:
			xDiff = sector.getXPos()%3;
			yDiff = sector.getYPos()%3;
			if (xDiff == 2 && yDiff == 2) {
				return 4;
			} 
			if (xDiff == 0 && yDiff == 0) {
				return 3;
			} 
			if (xDiff == 1 && yDiff == 1) {
				return 2;
			} 
			if ((Math.abs(sector.getXPos()-sector.getYPos()) % 3) == 2) {
				return 1;
			}
			break;
		case STATIC4:
			xDiff = sector.getXPos()%4;
			yDiff = sector.getYPos()%4;
			if (xDiff == 3 && yDiff == 3) {
				return 5;
			} 
			if (xDiff == 1 && yDiff == 1) {
				return 4;
			} 
			if (xDiff == 2 && yDiff == 2) {
				return 3;
			} 
			if (xDiff == 0 && yDiff == 0) {
				return 2;
			} 
			if ((Math.abs(sector.getXPos()-sector.getYPos()) % 4) == 2) {
				return 1;
			}
			break;
		case EVERY_SECOND:
			int integer = 0;
			neighbors = ActualStatus.getNeighbors(sector, Const.NEIGHBORS_LINEAR);
			for (Sector neighbor: neighbors) {
				if (neighbor.getState() == State.UNKNOWN) {
					integer = 1;
				}
			}
			if (integer == 1) {
				neighbors = ActualStatus.getNeighbors(sector, Const.NEIGHBORS_LASTTHREE);
				for (Sector neighbor: neighbors) {
					if ((integer == 1) && (neighbor.getPriority() < (Const.PRIOR_GRID_MIN + (2*Const.PRIORITY_DIFF))) ) {
						
						switch (neighbor.getState()) {
						case UNKNOWN: break;
						default: integer++;
						}
					}
				}
			}
			return integer;
		}
		return 0;
	}

	private static List<Sector> getSectorsByState(State findState) {
		List<Sector> tempList = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator();
		Sector actual;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.getState() == findState) {
				tempList.add(actual);
			}
		}
		return tempList;
	}
	
	private static Sector getRandomSectorByPriority() {
		List<Sector> highest = new ArrayList<Sector>();
		List<Sector> noise = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator();
		Sector actual;
		int priorMax = Const.PRIORITY_BLANK;
		int priorNoise = Const.PRIORITY_BLANK;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.getPriority() >= priorMax && actual.getState() == State.UNKNOWN) {
				if (actual.getPriority() > priorMax) { highest.clear(); priorMax = actual.getPriority(); System.err.println("High reseted"); }
				highest.add(actual);
			}
		}
		iterator.reset();
		while ((actual=iterator.nextSector()) != null) {
			if (actual.getPriority() >= priorNoise && actual.getPriority() < priorMax && actual.getState() == State.UNKNOWN) {
				if (actual.getPriority() > priorNoise) { noise.clear(); priorNoise = actual.getPriority(); }
				noise.add(actual);
			}
		}
		return selectRandomFromList(( (new Random().nextInt(100)) >= Const.NOISE_CHANCE) ? highest : noise);
	}

	/**
	 * can return even known sectors
	 */
	public static List<Sector> getHighestSectors() {
		List<Sector> tempList = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator();
		Sector actual;
		int priorMax = 0;
		while ((actual = iterator.nextSector()) != null) {
			if ((actual.getPriority() >= priorMax)) {
				if (actual.getPriority() > priorMax) { tempList.clear(); priorMax = actual.getPriority(); }
				tempList.add(actual);
			}
		}
		return tempList;
	}

	private static Sector selectRandomFromList(List<Sector> shotSectors) {
		Random rnd = new Random();
		if (Const.DEBUG) for (Sector x: shotSectors) System.err.println("ROUND " + ActualStatus.getRound() + " Selecting from x" + x.getXPos() + " y" + x.getYPos());
		return shotSectors.get( rnd.nextInt(shotSectors.size()) );
	}

}
