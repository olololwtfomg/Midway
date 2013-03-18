import java.util.ArrayList;
import java.util.List;


public class EnemyShip {
	
	public enum Type {
		SHIP_UNKNOWN,
		SHIP_2x1,
		SHIP_3x1,
		SHIP_4x1,
		SHIP_5x1,
		SHIP_2x3;
	}
	
	private enum Line {
		UNKNOWN, HORIZONTAL, VERTICAL;
	}

	private static Type longestUnknown;
	private static Type unknown3x2;
	
	private Line orientation;
	
	private List<Sector> position = new ArrayList<Sector>();
		
	public EnemyShip(Sector firstSector) {
		this.addPosition(firstSector);
		orientation = Line.UNKNOWN;
	}
	
	public int getSize() {
		return this.position.size();
	}
		
	public void addPosition(Sector sector) {
		this.position.add(sector);
	}
	
	public boolean havePartOn(Sector sector) {
		for (Sector shipPosition: position) {
			if (shipPosition.equals(sector)) return true;
		}
		return false;
	}
	
}
