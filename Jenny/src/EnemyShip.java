import java.util.ArrayList;
import java.util.List;


public class EnemyShip {
	
	public static final int ORIENTATION_UNKNOWN = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	public static final int ORIENTATION_VERTICAL = 2;

	public static final int SIZE_UNKNOWN = 1;
	public static final int SIZE_2x1 = 2;
	public static final int SIZE_3x1 = 3;
	public static final int SIZE_4x1 = 4;
	public static final int SIZE_5x1 = 5;
	public static final int SIZE_2x3 = 6;
	
	
	private int size = SIZE_UNKNOWN;
	private int orientation = ORIENTATION_UNKNOWN;
	
	private List<Sector> position = new ArrayList<Sector>();
	
	public EnemyShip(Sector firstSector) {
		position.add(firstSector);
		// TODO Auto-generated constructor stub
	}
	
	public void addSector(Sector sector) {
		this.position.add(sector);
	}
	
	public boolean havePartOn(Sector sector) {
		for (Sector shipPosition: position) {
			if (shipPosition == sector) return true;
		}
		return false;
	}
	
	public void addPosition(Sector sector) {
		
	}

}
