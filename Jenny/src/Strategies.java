import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usedConsts.Const;


public class Strategies {

	private static ActualStatus status;
	private static List<Sector> list = new ArrayList<Sector>();

	public static void doSomeLogic(ActualStatus stats) {
		status = stats;
		Sector actionSector;
		char action = Const.ACTION_SHOT;
		//if there is something with condition from previous round shot at it:
		findSectorsByCondition(Const.CONDITION_NEXT_SHOT);
		if (list.size() > 0) {
			actionSector = selectRandomFromList();
			status.setAction(actionSector.getXPos(), actionSector.getYPos(),  action); 
			return;
		}
		setGridPriorities();  //gridMin - gridMin+4*gridDiff
		
		selectHighestPriorSectors();
		actionSector = selectRandomFromList();
		status.setAction(actionSector.getXPos(), actionSector.getYPos(), action);
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
		return 0;
	}
	
	private static void findSectorsByCondition(int condition) {
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.getCondition() == condition) {
				list.add(actual);
			}
		}
	}

	private static void selectHighestPriorSectors() {
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		int max = 0;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.getPriority() >= max) {
				if (actual.getPriority() > max) { list.clear(); max = actual.getPriority(); }
				list.add(actual);
			}
		}
	}
	
	private static void findShips(boolean shipsFinall) {
		
	}
	
	private static Sector selectRandomFromList() {
		Random rnd = new Random();
		return list.get( rnd.nextInt(list.size()) );
	}

}
