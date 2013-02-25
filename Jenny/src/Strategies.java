import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usedConsts.Const;


public class Strategies {

	private static ActualStatus status;

	public static Sector doSomeLogic(ActualStatus stats) {
		status = stats;
		return defaultStrategy();
	}

	private static Sector defaultStrategy() {
		SectorIterator iterator = new SectorIterator(status);
		Sector actual;
		List<Sector> list = new ArrayList<Sector>();
		while ((actual = iterator.nextSector()) != null) {  //najdenie sektorov ktore maju vyssiu prioritu
			if (actual.priority >= Const.PRIOR_SOON) {
				list.add(actual);
			}
		}
		if (list.size()>0) return selectRandomFromList(list);
		iterator.reset();
		int minLevel = 5;  //cislo minimalnej urovne ktorej bunky boli najdene

		while((actual = iterator.nextSector()) != null) {
			if (isSectorKnown(actual)) continue;

			int xDiff = actual.xPos%3;
			int yDiff = actual.yPos%3;
			
			if (xDiff == 2 && yDiff == 2) {
				if (minLevel>1) {
					list.clear();
					minLevel = 1;
				}
				actual.priority = Const.PRIOR_FIRSTLEVEL;  //first level - max 16 shots
				list.add(actual);
			}
			else if (minLevel>1) {
				if (xDiff == 0 && yDiff == 0) {
					if (minLevel>2) {
						list.clear();
						minLevel = 2;
					}
					actual.priority = Const.PRIOR_SECONDLEVEL; //second level - max 25 shots
					list.add(actual);

				}
				if (minLevel>2) {
					if (xDiff == 1 && yDiff == 1) {
						if (minLevel>3) {
							list.clear();
							minLevel = 3;
						}
						actual.priority = Const.PRIOR_THIRDLEVEL; //third level - max 25 shots
						list.add(actual);
					}
					if (minLevel>3) {
						if ((Math.abs(actual.xPos-actual.yPos) % 3) == 2) {  //max 34 shots
							if (minLevel>4) {
								list.clear();
								minLevel = 4;
							}							
							actual.priority = Const.PRIOR_FINESTLEVEL;
							list.add(actual);
						} else {
							actual.priority = Const.PRIOR_LASTLEVEL;  //max 35
							list.add(actual);
						}
						
					}
				}
			}
		}
		return selectRandomFromList(list);
	}

	private static boolean isSectorKnown(Sector sector) {
		return sector.condition != Const.UNKNOWN && sector.condition != Const.NEXT_ROUND_SHOT && sector.condition != Const.PROBABLY_BLANK && sector.condition != Const.ENEMY_SHIP;
	}

	private static Sector selectRandomFromList(List<Sector> list) {
		Random rnd = new Random();
		return list.get( rnd.nextInt(list.size()) );
	}

}
