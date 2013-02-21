import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import usedConsts.Const;

public class Jenny {
	static ActualStatus status;
	
	private static final String INPUT_FILE = "battlefield.txt";
	private static final String LOG_FILE = "log.txt";
	private static final String WIN_PATH = "\\src\\";
	private static final String UNIX_PATH = "/src/";

	private enum OS_type {
		WINDOWS,LINUX 
	}

	private static OS_type os_type=OS_type.LINUX;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		status = loadStatus();
		loadLog(status);
		Sector temp = selectRandom(status);
		System.out.println(status.battlefield[0][1].condition);
		/*
		status.calculateHeuristics();
		status.print_heuristics();
		int position[]=status.findAirstrikePos();
		System.out.println("launch airstrike on position x"+position[0]+"y"+position[1]);
		*/
		System.out.println("Nahodna pozicia: [" + temp.xPos + ","+ temp.yPos + "] " + temp.condition);
		
	}
	
	private static void loadLog(ActualStatus status) { loadLog(status, false); }
	private static void loadLog(ActualStatus status, boolean badInputFile) {
		BufferedReader br = null;
		String currentLine = "";
		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(".%s%s",WIN_PATH, LOG_FILE);
			break;
		default:
			logFileName= String.format(".%s%s",UNIX_PATH, LOG_FILE);
			break;
		}

		try {	//nacitanie zo suboru
			File file = new File(logFileName);
			if (!file.isFile()) {
				//log is broken ... create new based on system input
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine();
			int priorTemp = 0;
			
			int battlefieldRow = 0;
			int logCondition = 0;
			while ((currentLine = br.readLine()) != null) {
				if (battlefieldRow<15) {
					for (int column = 0; column< currentLine.length() && column<15; column++) {
						logCondition = parseStatus(currentLine.charAt(column));
						if (!badInputFile) {  
							compareBeforeNowSector(logCondition, status.battlefield[battlefieldRow][column]);
							
						} else {  //nebol spravne nacitany input ... ziadne nove informacie o battlefield
							switch (logCondition) {
							case Const.PROBABLY_BLANK: priorTemp = Const.PRIOR_MIN; break;
							case Const.UNKNOWN: priorTemp = Const.PRIOR_UNKNOWN; break;
							case Const.NEXT_ROUND_SHOT: priorTemp = Const.PRIOR_SOON; break;
							case Const.ENEMY_SHIP: priorTemp = Const.PRIOR_MAX; break;
							}
							status.battlefield[battlefieldRow][column] = new Sector(logCondition, priorTemp, battlefieldRow, column);
						}

					}
					battlefieldRow++;
				} else {
					System.err.println("Vstupny subor nema dost riadkov pre battlefield. Posledny riadok: " + (battlefieldRow - 1) + ", pocet znakov: " + currentLine.length());
					battlefieldRow++;
				}
			}			
		} catch (IOException e) {
			System.err.println("Nieje mozne citat subor.");
			//log is broken ... we are screwed
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie logu nebolo ukoncene.");
			}

		}
	}
	
	private static void compareBeforeNowSector(int input,
			Sector sector) {
		int log = sector.condition; //status from log
		if (log == 1) {  //its ally ship there
			makeNearestBlank(sector);
		} else if (log == 3) {  //someone shot, no hit
			switch (input) {
			case 2:  //our new shot
			case 8:
			case 9:  //our new shot
			case 0:  //
			case 3:  //check if we shot in last round or its enemy shot (special mode if torpedo or firework used)
				break;
			case 4:  //our shot, no hit
			case 5:  //old shot copy from log
				break;
			default: //copy from input
				break;
			}
		} else if (log == 6) {  //ally ship sunken
			switch (input) {
			case 1:  //enemy new hit
				break;
			case 6:  //old or expected (own hit)
				break;
			default: //copy from input
				break;
			}
		} else if (log == 7) {  //enemy ship hit
			switch (input) {
			case 2:  //cool logic - reapeat and won
				break;
			case 0:  //something destroyed enemy ship (special bomb/selfshot)
			case 4:  //last shot succeded - continue in shoting around
				break;
			case 7:  //old record
				break;
			case 8:  //bad logic (enemy destroyed own ship in sector that should not contain ship)
				break;
			case 9:  //special bomb succeded
				break;
			default:  //enemy destroyed own ship
				break;
			}
		} else if (log == 0) {  //unknown - in log is our logic
			switch (input) {
			case 2:  //copy from log
				break;
			case 8:  //copy from log
				break;
			case 9:  //copy from log
				break;
			default: //copy from now
				break;
			}
		}
		//1 for own ship, 2 for enemy ship
		//3 for unknown shot, 4 for own shot, 5 for enemy shot
		//6 for ally ship hit, 7 for enemy ship hit,
		//8 for lowest priority, 9 for high priority, 0 for unknown
	}
	public static void shootAt(Sector sector) {
		shootAt(sector.xPos, sector.yPos);
	}
	public static void shootAt(int x, int y) {
		status.battlefield[x][y].condition = Const.OUR_SHOT;
		int tryToSave = 0;
		boolean uncompleteSave = false;
		do {
			uncompleteSave = ! saveStatusToLog();
		} while (tryToSave<3 && uncompleteSave);
		System.out.format("m [%d] [%d]", x, y);
	}
	
	private static boolean saveStatusToLog() {
		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(".%s%s",WIN_PATH, LOG_FILE);
			break;
		default:
			logFileName= String.format(".%s%s",UNIX_PATH, LOG_FILE);
			break;
		}

		File file = new File(logFileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException | SecurityException e) {
			System.err.println("Nieje mozne vytvorit subor pre log.");
			return false;
		}
		if (!file.canWrite()) {
			System.err.println("Do suboru pre log sa neda zapisovat.");
			return false;
		}
		BufferedWriter bw = null;
		StringBuffer currLine = new StringBuffer("");
		try {
			bw = new BufferedWriter(new FileWriter(file));
			
			for (int row = 0; row<status.battlefield.length; row++) {
				for (int column = 0; column<status.battlefield[row].length; column++) {
					currLine.append(status.battlefield[row][column]);
				}
				bw.write(currLine.toString());
				bw.newLine();
				currLine.delete(0,currLine.length());
			}
		} catch (IOException e) {
			System.err.println("Problem so zapisovanim logu.");
			return false;
		} finally {
			try {
				if (bw != null) bw.close();
			} catch (IOException ex) {
				System.err.println("Log nebol ulozeny.");
				return false;
			}
		}
		//System.err.println("Subor uspesne vytvoreny.");
		return true;
	}

	private static void makeNearestBlank(Sector sector) {
		int x = sector.xPos, y = sector.yPos;
		Sector temp;  //        north      east       south      west      northeast   southeast     southwest    northwest
		int[][] nearest = { { x-1, y }, { x,y+1 }, { x+1,y }, { x,y-1 }, { x-1,y+1 }, { x+1, y+1 }, { x+1, y-1 }, {x-1, y-1} }; 
		for (int i = 0; i<nearest.length; i++) {
			x = nearest[i][0];
			y = nearest[i][1];
			if (x<14 && y<14 && x>=0 && y>=0) {
				temp = status.battlefield[x][y];
				switch (temp.condition) {
				case Const.UNKNOWN:
				case Const.ENEMY_SHIP:
				case Const.NEXT_ROUND_SHOT: temp.condition = Const.PROBABLY_BLANK; break;
				}
			}
		}
		
	}
	private static ActualStatus loadStatus() {
		BufferedReader br = null;
		ActualStatus status = new ActualStatus();
		String currentLine = "";
		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(".%s%s",WIN_PATH, INPUT_FILE);
			break;
		default:
			logFileName= String.format(".%s%s",UNIX_PATH, INPUT_FILE);
			break;
		}
		int index = 0;
		String[] splitLine;
		try {	//nacitanie zo suboru
			File file = new File(logFileName);
			if (!file.isFile()) {
				return null;
				////////////////////////////////////////////////
				////////////load status from log
				////////////////////////////////////////////////				
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine();
			if (currentLine != null) {  //status line
				splitLine = currentLine.split("[^0-9]+");
				status.side = Integer.parseInt(splitLine[0]);
				status.roundsToEnd = Integer.parseInt(splitLine[1]);
				status.specialShots = Integer.parseInt(splitLine[2]);
			}
			while ((currentLine = br.readLine()) != null) {
				if (index<15 || currentLine.length() ==14) {
					for (int column = 0; column<currentLine.length(); column++) {
						status.battlefield[index][column] = new Sector(parseStatus(currentLine.charAt(column)) , index, column);
					}
					index++;
				} else {
					System.err.println("Vstupny subor nema spravny format. Posledny riadok: " + (index - 1) + ", pocet znakov: " + currentLine.length());
					index++;
				}
			}
			if (index != 14) {
				System.err.println("Vstupny subor nebol nacitany spravne posledny nacitany riadok: " + index);
			}

		} catch (IOException e) {
			System.err.println("Nieje mozne citat subor.");

			////////////////////////////////////////////////
			////////////load status from log
			////////////////////////////////////////////////

		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie suboru nebolo ukoncene.");
			}

		}
		return status;
	}

	private static int parseStatus(char charAt) {
		int condition = 0;

		//1 for own ship, 2 for enemy ship
		//3 for unknown shot, 4 for own shot, 5 for enemy shot
		//6 for ally ship hit, 7 for enemy ship hit,
		//8 for lowest priority, 9 for high priority, 0 for unknown

		switch (charAt) {
		case '1':
			condition = Const.ALLY_SHIP; break;  //ally ship, floating
		case '2':
			condition = Const.ENEMY_SHIP; break;  //enemy ship = max priority
		case '3':          //bad sector in log
		case '.':
			condition = Const.NOTHING_HIT; break;  //nothing, hit
		case '4':
			condition = Const.OUR_SHOT; break;  //our shot on nothing  /extends 3
		case '5':
			condition = Const.ENEMY_SHOT; break;  //enemy shot on nothing  /extends 3
		case '6':
		case '*':
			condition = Const.ALLY_SUNK; break;  //ally ship, sunk
		case '7':
		case '+':
			condition = Const.ENEMY_SUNK; break;  //enemy ship, sunk
		case '8':
			condition = Const.PROBABLY_BLANK; break;  //probably blank sector
		case '9':
			condition = Const.NEXT_ROUND_SHOT; break;  //next round shot
		case ' ':
			condition = Const.UNKNOWN; break;  //unknown
		}
		return condition;
	}

	private static Sector selectRandom(ActualStatus status) {
		Random rnd = new Random();
		return status.battlefield[rnd.nextInt(status.battlefield.length)][rnd.nextInt(status.battlefield[0].length)];
	}
	
	/*
		for (int row = 0; row<status.battlefield.length;row++) {
			for (int column = 0; column<status.battlefield[0].length;column++) {

			}
		}
		return null;

	}

	 */

}