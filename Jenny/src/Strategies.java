import java.util.Random;

import usedConsts.Const;


public class Strategies {
	public static Sector doSomeLogic(ActualStatus status) {
		status.battlefield[0][13].action = Const.SHOT;
		return status.battlefield[0][13];
	}
	
	
	
	private static Sector selectRandom(Sector[] array) {
		Random rnd = new Random();
		return array[rnd.nextInt()];
	}

}
