import java.util.ArrayList;
import java.util.List;


public class EnemyShip {
	
	public static final int ORIENTATION_UNKNOWN = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	public static final int ORIENTATION_VERTICAL = 2;
	
	private static int maxLength;
	
	private List<Sector> position = new ArrayList<Sector>();
	private int orientation = ORIENTATION_UNKNOWN;
	
	public EnemyShip(Sector firstSector) {
		position.add(firstSector);
		// TODO Auto-generated constructor stub
	}
	
	public void addPosition(Sector sector) {
		
	}

}
