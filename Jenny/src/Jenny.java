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
	
	private static final String INPUT_FILE_PATH = ".%ssrc%<sbattlefield.txt";
	private static final String LOG_FILE_PATH = ".%ssrc%<slog.txt";
	private static final String WIN_PATH_SEP = "\\";
	private static final String UNIX_PATH_SEP = "/";

	private enum OS_type {
		WINDOWS,LINUX 
	}

	private static OS_type os_type=OS_type.LINUX;

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
			logFileName= String.format(LOG_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(LOG_FILE_PATH,UNIX_PATH_SEP);
			break;
		}

		try {
			File file = new File(logFileName);
			if (!file.isFile()) {
				System.err.println("Log neexistuje.");
				//log is broken ... create new based on system input
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine(); //round
			currentLine = br.readLine(); //priorities
			int priorTemp = 0;
			
			int battlefieldRow = 0;
			int logCondition = 0;
			while ((currentLine = br.readLine()) != null) {
				if (battlefieldRow<14) {
					for (int column = 0; column< currentLine.length() && column<15; column++) {
						logCondition = parseStatus(currentLine.charAt(column));
						if (!badInputFile) {  
							compareBeforeNowSector(status.battlefield[battlefieldRow][column], logCondition);
							
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
			System.err.println("Nieje mozne citat log.");
			//log is broken ... load from system input (was done probabbly) and initialize new log
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie logu nebolo ukoncene.");
			}

		}
	}
	
	private static void compareBeforeNowSector(Sector sector, int log) {
		int input = sector.condition; //status from actual input
		if (input == Const.ALLY_SHIP) {  //its ally ship there
			makeNearestBlank(sector);
		} else if (input == Const.SOME_SHOT) {  //someone shot, no hit
			switch (log) {  //before:
			case Const.ENEMY_SHIP:
			case Const.PROBABLY_BLANK:
			case Const.NEXT_ROUND_SHOT:
			case Const.UNKNOWN:
				//check if we shot (torpedo or firework) this way else its enemy shot
				sector.condition = Const.ENEMY_SHOT;
				break;
			case Const.OUR_SHOT:  //old our shot
			case Const.ENEMY_SHOT:  //old enemy shot
				sector.condition = log;
				break;
			case Const.SOME_SHOT:    //possible if there was problem with log in last round (copyed directly from input) or this is first round and the shot is from enemy
				sector.condition = Const.ENEMY_SHOT;
				break;
			}
		} else if (input == Const.ALLY_SUNK) {  //ally ship sunken
			switch (log) {
			case Const.ALLY_SHIP:  //enemy new hit
				break;
			case Const.ALLY_SUNK:  //old or expected (own hit)
				sector.condition = log;
				break;
			}
		} else if (input == Const.ENEMY_SUNK) {  //enemy ship sunken
			switch (log) {
			case Const.ENEMY_SUNK:  //old record
				break;
			case Const.ENEMY_SHIP:  //cool logic - reapeat and won
				break;
			case Const.UNKNOWN:  //something destroyed enemy ship (special bomb/enemy)
			case Const.OUR_SHOT:  //last shot succeded - continue in shoting around
				status.enemyJustHit = sector;
				break;
			case Const.PROBABLY_BLANK:  //bad logic (enemy destroyed own ship in sector that should not contain ship)
				break;
			case Const.NEXT_ROUND_SHOT:  //special bomb succeded
				break;
			default:  //enemy destroyed own ship
				break;
			}
		} else if (input == Const.UNKNOWN) {  //unknown - in log is our logic
			switch (log) {
			case Const.ENEMY_SHIP:  
				
				//initialize priority from log
				break;
			case Const.PROBABLY_BLANK:  
				//initialize priority from log
				break;
			case Const.NEXT_ROUND_SHOT:  
				//initialize priority from log
				break;
			default: //copy from input
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
			logFileName= String.format(LOG_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(LOG_FILE_PATH,UNIX_PATH_SEP);
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
			logFileName= String.format(INPUT_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(INPUT_FILE_PATH,UNIX_PATH_SEP);
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
			System.err.println("Nieje mozne citat system input.");

			////////////////////////////////////////////////
			////////////load status from log
			////////////////////////////////////////////////

		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie system input nebolo ukoncene.");
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
			condition = Const.SOME_SHOT; break;  //nothing, hit
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