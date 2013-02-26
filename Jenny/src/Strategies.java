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
		findSectors(Const.PRIOR_SOON);
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
		
		int minLevel = 5;  //cislo minimalnej urovne ktorej bunky boli najdene
		
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		while((actual = iterator.nextSector()) != null) {
			if (actual.isSectorKnown()) continue;

			int xDiff = actual.getXPos()%3;
			int yDiff = actual.getYPos()%3;
			
			if (xDiff == 2 && yDiff == 2) {
				if (minLevel>1) {
					list.clear();
					minLevel = 1;
				}
//				actual.priority = Const.PRIOR_FIRSTLEVEL;  //first level - max 16 shots
				list.add(actual);
			}
			else if (minLevel>1) {
				if (xDiff == 0 && yDiff == 0) {
					if (minLevel>2) {
						list.clear();
						minLevel = 2;
					}
//					actual.priority = Const.PRIOR_SECONDLEVEL; //second level - max 25 shots
					list.add(actual);

				}
				if (minLevel>2) {
					if (xDiff == 1 && yDiff == 1) {
						if (minLevel>3) {
							list.clear();
							minLevel = 3;
						}
//						actual.priority = Const.PRIOR_THIRDLEVEL; //third level - max 25 shots
						list.add(actual);
					}
					if (minLevel>3) {
						if ((Math.abs(actual.getXPos()-actual.getYPos()) % 3) == 2) {  //max 34 shots
							if (minLevel>4) {
								list.clear();
								minLevel = 4;
							}							
//							actual.priority = Const.PRIOR_FINESTLEVEL;
							list.add(actual);
						} else {
//							actual.priority = Const.PRIOR_LASTLEVEL;  //max 35
							list.add(actual);
						}
						
					}
				}
			}
		}
	}

	private static void findSectors(int prioLevel)
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
