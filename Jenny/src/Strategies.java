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
		//if there is something with priority from previous round shot at it:
		findSectorsByCondition(Const.CONDITION_NEXT_SHOT);
		if (list.size() > 0) {
			actionSector = selectRandomFromList(); 
			status.setAction(actionSector.getXPos(), actionSector.getYPos(),  action); 
			return;
		}

		//default grid filling
		gridFill();
		actionSector = selectRandomFromList();
		status.setAction(actionSector.getXPos(), actionSector.getYPos(), action);
	}

	private static void gridFill() {

		int minLevel = 6;  //cislo minimalnej urovne ktorej bunky boli najdene

		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while((actual = iterator.nextSector()) != null) {
			if (actual.isSectorKnown()) continue;

			int xDiff = actual.getXPos()%4;
			int yDiff = actual.getYPos()%4;

			if (xDiff == 3 && yDiff == 3) {
				if (minLevel>1) { list.clear(); minLevel = 1; }
				list.add(actual);
			}
			else if (minLevel>1) {
				if (xDiff == 1 && yDiff == 1) {
					if (minLevel>2) { list.clear(); minLevel = 2; }
					list.add(actual);
				}
				if (minLevel>2) {
					if (xDiff == 2 && yDiff == 2) {
						if (minLevel>3) { list.clear(); minLevel = 3; }
						list.add(actual);
					}
					if (minLevel>3) {
						if (xDiff == 0 && yDiff == 0) {
							if (minLevel>4) { list.clear(); minLevel = 4; }
							list.add(actual);
						}
						if (minLevel>4) {
							if ((Math.abs(actual.getXPos()-actual.getYPos()) % 4) == 2) {  //max 48
								if (minLevel>5) { list.clear(); minLevel = 5; }
								list.add(actual);
							} else if (minLevel > 5) {
								list.add(actual);
							}

						}
					}
				}
			}
		}
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

	private static void findSectorsByPriority(int prioLevel)
	{
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.getPriority() >= prioLevel) {
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
