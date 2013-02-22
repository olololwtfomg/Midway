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
		Sector[] nextShot = null; //needs to be collection
		int nextShotIndex = 0;
		while ((actual = iterator.nextSector()) != null) {
			if (actual.priority >= Const.PRIOR_SOON) {
				if (nextShot == null) nextShot = new Sector[20];  //convert to collection
				nextShot[nextShotIndex] = actual;
			}
		}
		if (nextShot != null) return selectRandom(nextShot);
		iterator.reset();
		Sector[] firstLevel = null;
		Sector[] secondLevel = null;
		Sector[] thirdLevel = null;
		Sector[] finestLevel = null;
		Sector[] lastLevel = null;
		
		while((actual = iterator.nextSector()) != null) {
			if (isSectorKnown(actual)) continue;
			
			int xDiff = actual.xPos%3;
			int yDiff = actual.yPos%3;
			
			if (xDiff == 2 && yDiff == 2) {
				if (firstLevel == null) firstLevel = new Sector[16];
				actual.priority = Const.PRIOR_FIRSTLEVEL;  //first level - max 16 shots
			}
			else if (xDiff == 0 && yDiff == 0) {
				if (firstLevel != null) continue;
				if (secondLevel == null) secondLevel = new Sector[25];
				actual.priority = Const.PRIOR_SECONDLEVEL; //second level - max 25 shots
				
			}
			else if (xDiff == 1 && yDiff == 1) {
				if (firstLevel != null || secondLevel != null) continue;
				if (thirdLevel == null) thirdLevel = new Sector[25];
				actual.priority = Const.PRIOR_THIRDLEVEL; //third level - max 25 shots
			}
			else if ((Math.abs(actual.xPos-actual.yPos) % 3) == 2) {  //max 34 shots
				if (firstLevel != null && secondLevel != null && thirdLevel != null) continue;
				if (finestLevel == null) finestLevel = new Sector[34];
				actual.priority = Const.PRIOR_FINESTLEVEL;
			}
			else {
				if (firstLevel != null && secondLevel != null && thirdLevel != null && finestLevel == null) continue;
				if (lastLevel == null) lastLevel = new Sector[35];
				actual.priority = Const.PRIOR_LASTLEVEL;  //max 35
			}
		}
		return selectRandom(firstLevel != null ? firstLevel : secondLevel != null ? secondLevel : thirdLevel != null ? thirdLevel : finestLevel != null ? finestLevel : lastLevel);
	}
	
	private static boolean isSectorKnown(Sector sector) {
		return sector.condition != Const.UNKNOWN && sector.condition != Const.NEXT_ROUND_SHOT && sector.condition != Const.PROBABLY_BLANK && sector.condition != Const.ENEMY_SHIP;
	}
	
	private static Sector selectRandom(Sector[] array) {
		Random rnd = new Random();
		return array[rnd.nextInt()];
	}

}
