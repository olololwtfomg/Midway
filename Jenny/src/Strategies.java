import java.util.Random;


public class Strategies {
	public static Sector doSomeLogic(ActualStatus status) {
		return null;
	}
	
	
	
	private static Sector selectRandom(Sector[] array) {
		Random rnd = new Random();
		return array[rnd.nextInt()];
	}

}
