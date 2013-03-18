import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usedConsts.Const;
import usedConsts.StatusConsts;


public class Strategies {

	private static ActualStatus status;

	public static void doSomeLogic(ActualStatus stats) {
		status = stats;
		Sector actionSector;

		setEnemyShips();
		setGridPriorities();  //gridMin - gridMin+5*gridDiff

		actionSector = getRandomSectorByPriority();
		status.executeAction(actionSector);
	}

	private static void setEnemyShips() {
		List<Sector> enemyShips = getSectorsByState(State.ENEMY_SUNK);
		List<Sector> neighbors;
		for (Sector sunken: enemyShips) {
			status.addShip(sunken);
			//basic finding
		}
		
		for (Sector sunken: enemyShips) {
			neighbors = status.getNeighbors(sunken, Const.NEIGHBORS_LINEAR);
			for (Sector neighbor: neighbors) {
				switch (neighbor.getState()) {
				case OUR_SHOT:
				case ENEMY_SHOT:
				case BLANK:
				case SOME_SHOT: //luck proof
					for (int i = -1; i<=1; i+=2) {
						Sector temp = status.getSector(
								(neighbor.getXPos() - sunken.getXPos()) == 0 ? (sunken.getXPos() + i) : neighbor.getXPos(), 
										(neighbor.getYPos() - sunken.getYPos()) == 0 ? (sunken.getYPos() + i) : neighbor.getYPos() 
								); //sektor kolmo na sunken a neighbor
						if (temp != null) ActualStatus.makeBlank(temp);
					}
					break;
				default:
					break;
				}
			}
			//advanced finding (neighbor one step away)
		}
	}

	public void calculateHeuristics()
	{
		Sector currSector;
		SectorIterator iterator = new SectorIterator(status);
		while ((currSector = iterator.nextSector()) != null) {
			/*there's no point in calculating it over and over again
			 * if we know it's a bad location to do an air strike*/
			if(currSector.getHeurValue()<StatusConsts.HEUR_THRESHOLD){
				currSector.setHeurValue(
						calculateSectorHeuristics(currSector.getXPos(),currSector.getXPos()));
			}			
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
					value=status.getSector(xAxis, yAxis).getSpecialValue();
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

	
	private static void setGridPriorities() {
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while((actual = iterator.nextSector()) != null) {
			if (actual.isSectorKnown()) continue;
			actual.setPriority((Const.PRIOR_GRID_MIN + getGridLvlForSector(actual)*Const.PRIORITY_DIFF) );
		}
	}

	public static int getGridLvlForSector(Sector sector) {
		if (status.getGrid() == 3) {
			int xDiff = sector.getXPos()%3;
			int yDiff = sector.getYPos()%3;
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
		}
		else if (status.getGrid() == 4) {
			int xDiff = sector.getXPos()%4;
			int yDiff = sector.getYPos()%4;
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
		}
		return 0;
	}

	private static List<Sector> getSectorsByState(State findState) {
		List<Sector> tempList = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator(status);
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
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		int priorMax = 0;
		int priorNoise = 0;
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
		SectorIterator iterator = new SectorIterator(status);
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
		if (Const.DEBUG) for (Sector x: shotSectors) System.err.println("ROUND " + status.getRound() + " Selecting from x" + x.getXPos() + " y" + x.getYPos());
		return shotSectors.get( rnd.nextInt(shotSectors.size()) );
	}

}
