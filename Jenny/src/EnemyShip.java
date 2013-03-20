import java.util.ArrayList;
import java.util.List;


public class EnemyShip {
	
	public enum Type {
		SHIP_UNKNOWN(5),
		SHIP_2x1(2),
		SHIP_3x1(3),
		SHIP_4x1(4),
		SHIP_5x1(5),
		SHIP_2x3(3);
		
		private int length;
		private Type(int length) {
			this.length = length;
		}
		public int getLength() {
			return this.length;
		}
	}
	
	
	private static int longestUnknown = 5;
	private static boolean unknown3x2;
	
	private Integer[] borders = new Integer[4];
	private Type type;
	
	private List<Sector> position = new ArrayList<Sector>();
	
	public EnemyShip(Sector firstSector) {
		this.addPosition(firstSector);
		type = Type.SHIP_UNKNOWN;
	}
	
	public int getSize() {
		return this.position.size();
	}
	
	public void calculateSize() {
		int[] size = {0, 0};
		
		if ((borders[0] != null) && (borders[2] != null)) {
			size[1] = Math.abs(borders[0] - borders[2]) -1;
		}
		if ((borders[1] != null) && (borders[3] != null)) {
			size[0] = Math.abs(borders[1] - borders[3]) - 1;
		}
		int[] temp;
		for (int i = 0; i<position.size()-1; i++) {
			for (int j=i+1; j<position.size(); j++ ) {
				temp = ActualStatus.getSectorDistances(position.get(i),position.get(j));
				if (temp[0]>longestUnknown) size[0] = temp[0];
				if (temp[1]>longestUnknown) size[1] = temp[1];
			}
		}
		if ((size[0] == 5) || (size[1] == 5)) {
			type = Type.SHIP_5x1;
		} else if((size[0] == 4) || (size[1] == 4)) {
			type = Type.SHIP_4x1;
		} else if ( ((size[0] == 3) && (size[1] == 1)) || ((size[0] == 1) && (size[1] == 3)) ) {
			type = Type.SHIP_3x1;
		} else if ( ((size[0] == 3) && (size[1] == 2)) || ((size[0] == 2) && (size[1] == 3)) ) {
			type = Type.SHIP_2x3;
		}
		
		if (this.type == Type.SHIP_UNKNOWN) {
		
		}
	}
		
	public static int getMaxUnknownLength() {
		return longestUnknown;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public static boolean is3x2Unknown() {
		return unknown3x2;
	}
		
	public void addPosition(Sector sector) {
		this.position.add(sector);
	}
	
	public void borderNorth(Sector border) {
		if (borders[0] == null) borders[0] = border.getYPos();
	}
	public void borderEast(Sector border) {
		if (borders[1] == null) borders[1] = border.getXPos(); 		
	}
	public void borderSouth(Sector border) {
		if (borders[2] == null) borders[2] = border.getYPos(); 
	}
	public void borderWest(Sector border) {
		if (borders[3] == null) borders[3] = border.getXPos(); 
	}

	public boolean havePartOn(Sector sector) {
		for (Sector shipPosition: position) {
			if (shipPosition.equals(sector)) return true;
		}
		return false;
	}
	
}
