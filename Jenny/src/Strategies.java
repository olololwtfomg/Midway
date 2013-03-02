import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usedConsts.Const;


public class Strategies {

	private static ActualStatus status;

	public static void doSomeLogic(ActualStatus stats) {
		status = stats;
		Sector actionSector;

		findShips();

		//if there is something with condition from previous round shot at it:
		setGridPriorities();  //gridMin - gridMin+5*gridDiff

		actionSector = selectRandomFromList(getHighestSectors(true));
		status.setAction(actionSector.getXPos(), actionSector.getYPos(), Const.ACTION_SHOT);
	}

	private static void findShips() {
		List<Sector> enemyShips = getSectors(Const.CONDITION_ENEMY_SUNK, false);
		List<Sector> neighbors;
		List<Sector> blanks = new ArrayList<Sector>();
		EnemyShip temp;
		for (Sector sunken: enemyShips) {
			if (!sunken.isEnemyShip()) temp = new EnemyShip(sunken);
			else temp = null;
			//basic finding
			neighbors = status.getNeighbors(sunken, Const.NEIGHBORS_LINEAR);
			for (Sector neighbor: neighbors) {
				switch (neighbor.getCondition()) {
				case Const.CONDITION_OUR_SHOT:
				case Const.CONDITION_ENEMY_SHOT:
				case Const.CONDITION_BLANK:
				case Const.CONDITION_SOME_SHOT: //luck proof
					for (int i = -1; i<=1; i+=2) {
						try {
							blanks.add(status.getSector(
									(neighbor.getXPos() - sunken.getXPos()) == 0 ? (sunken.getXPos() + i) : neighbor.getXPos(), 
											(neighbor.getYPos() - sunken.getYPos()) == 0 ? (sunken.getYPos() + i) : neighbor.getYPos() 
									));
							ActualStatus.makeBlank(blanks);
						} catch (ArrayIndexOutOfBoundsException e) {
							continue;
						}
					}
					break;
				}
			}
			neighbors = status.getNeighbors(sunken,  Const.NEIGHBORS_LINEAR);
			for (Sector neighbor : neighbors) {
				switch (neighbor.getCondition()) {
				case Const.CONDITION_ENEMY_SUNK:
				}
			}
			//advanced finding (neighbor one step away)
			
		}
//		enemyShips = getSectorsByCondition(Const.CONDITION_ENEMY_SHIP);

	}

	private static void setGridPriorities() {
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while((actual = iterator.nextSector()) != null) {
			if (actual.isSectorKnown()) continue;
			actual.setStats(null, (Const.PRIOR_GRID_MIN + getGridLvlForSector(actual)*Const.PRIORITY_DIFF ) );
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

	private static List<Sector> getSectors(int findValue, boolean findingPriority) {
		List<Sector> tempList = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while ((actual = iterator.nextSector()) != null) {
			if ((findingPriority ? actual.getPriority() : actual.getCondition()) == findValue) {
				tempList.add(actual);
			}
		}
		return tempList;
	}
	

	private static List<Sector> getHighestSectors(boolean includeNextShot) {
		List<Sector> tempList = new ArrayList<Sector>();
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		boolean foundNextShot = false;
		int priorMax = 0;
		while ((actual = iterator.nextSector()) != null) {
			if ( includeNextShot && (actual.getCondition() == Const.CONDITION_NEXT_SHOT)) {
				if (!foundNextShot) { tempList.clear(); foundNextShot = true; }
				tempList.add(actual);
			} else if ((actual.getPriority() >= priorMax) && !foundNextShot) {
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
