
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import usedConsts.Const;

public class Jenny {
	static ActualStatus status;

	private static final String INPUT_FILE_PATH = ".%sbattlefield.txt";
	private static final String LOG_FILE_PATH = ".%slog.txt";
	private static final String WIN_PATH_SEP = "\\";
	private static final String UNIX_PATH_SEP = "/";

	private enum OS_type {
		WINDOWS,LINUX 
	}

	private static OS_type os_type=OS_type.LINUX;

	public static void main(String[] args) {
		/////////////////////////////////////////////////////
		Stopwatch watch = new Stopwatch(true);///////////////////
		/////////////////////////////////////////////////////
		loadLog(loadStatus());
		Sector shotSector = Strategies.doSomeLogic(status);
		if (shotSector == null) System.err.println("Fatal error: no result to execute.");
		String lastWord = "";
		switch (shotSector.action) {
		case Const.BOMB:
			break;
		case Const.TORPEDO:
			break;
		case Const.FIREWORK:
			break;
		case Const.SHOT:
		default:
			lastWord = String.format("%c %d %d", Const.SHOT, shotSector.xPos, shotSector.yPos);
			shotSector.condition = Const.OUR_SHOT;
			break;
		}
		saveStatusToLog(shotSector);
		if (Const.TIMER) System.err.println("ROUND " + status.round + " Time: " + (watch.actualTime()/1000000) + "ms."); 
		System.out.println(lastWord);
		//System.out.format("%s [%d] [%d]", shot.)
		//System.out.format("%s [%d] [%d] %c",
		/*
		status.calculateHeuristics();
		status.print_heuristics();
		int position[]=status.findAirstrikePos();
		System.out.println("launch airstrike on position x"+position[0]+"y"+position[1]);
		 */

	}

	private static void loadLog(boolean badInputFile) {
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
				
				//set blank near our ships
				Sector actual;
				SectorIterator iterator = new SectorIterator(status);
				while ((actual = iterator.nextSector()) != null) {
					if (actual.getCondition() == Const.ALLY_SHIP) {
						actual.makeNearestBlank(status);
					}
				}
				return;
			}
			br = new BufferedReader(new FileReader(file));

			currentLine = br.readLine(); //initialize priorities for system input
			String[] splitLine = currentLine.split("[^0-9]+");
			
			currentLine = br.readLine(); //last move
			int priorTemp = 0;

			int battlefieldRow = 0;
			int logCondition = 0;
			int priorityIndex = 0;
			while ((currentLine = br.readLine()) != null) {  //posun na dalsi riadok
				if (battlefieldRow<14) {
					for (int column = 0; column< currentLine.length() && column<15; column++) {  //prechod riadkom
						logCondition = parseCondition(currentLine.charAt(column));
						if (!badInputFile) {
							switch (logCondition) {  //load priority
							case Const.ENEMY_SHIP:
							case Const.NEXT_ROUND_SHOT:
							case Const.UNKNOWN:
							case Const.PROBABLY_BLANK:
								if (splitLine.length > priorityIndex) {
									status.battlefield[column][battlefieldRow].priority = Integer.parseInt(splitLine[priorityIndex++]);
								} else {
									System.err.println("Fatal error while loading priorities from log. priorities.length = " + splitLine.length);
									status.battlefield[column][battlefieldRow].priority = Const.PRIOR_UNKNOWN;  //if the log priorities are not good enough
								}
							}
							
							compareInputVSLog(status.battlefield[column][battlefieldRow], logCondition);

						} else {  //nebol spravne nacitany input ... ziadne nove informacie z logu
							System.err.println("Nenacitany system input file v kole: " + status.round);
							switch (logCondition) {
							case Const.PROBABLY_BLANK: priorTemp = Const.PRIOR_MIN; break;
							case Const.UNKNOWN: priorTemp = Const.PRIOR_UNKNOWN; break;
							case Const.NEXT_ROUND_SHOT: priorTemp = Const.PRIOR_SOON; break;
							case Const.ENEMY_SHIP: priorTemp = Const.PRIOR_MAX; break;
							}
							status.battlefield[column][battlefieldRow] = new Sector(logCondition, priorTemp, column, battlefieldRow);
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

	private static void compareInputVSLog(Sector sector, int log) {
		int input = sector.getCondition(); //status from actual input
		if (input == Const.ALLY_SHIP) {  //its ally ship there
			sector.makeNearestBlank(status);
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
			if (log == Const.ALLY_SHIP) {
				//enemy new hit
			}
		} else if (input == Const.ENEMY_SUNK) {  //enemy ship sunken
			switch (log) {
			case Const.ENEMY_SHIP:  //cool logic - reapeat and won
				break;
			case Const.UNKNOWN:  //something destroyed enemy ship (special bomb/enemy)
			case Const.OUR_SHOT:  //last shot succeded - continue in shoting around
				sector.makeNearestNextShot(status);
				break;
			case Const.PROBABLY_BLANK:
				break;
			case Const.NEXT_ROUND_SHOT:  //special bomb succeded
				break;
			default:  //enemy destroyed own ship
				break;
			}
		} else if (input == Const.UNKNOWN) {  //unknown - in log is our logic
			switch (log) {
			case Const.ALLY_SHIP:
			case Const.OUR_SHOT:
			case Const.ENEMY_SHOT:
			case Const.ALLY_SUNK:
			case Const.ENEMY_SUNK:
				sector.setCondition(log);  //problems with system input - loadead from default
				break;
			case Const.ENEMY_SHIP:  
				sector.setCondition(log);
				break;
			case Const.PROBABLY_BLANK:
				sector.setCondition(log);
				break;
			case Const.NEXT_ROUND_SHOT:  
				sector.setCondition(log);
				break;
			default: //copy from input
				break;
			}
		}
	}

	private static boolean saveStatusToLog(Sector lastShot) {
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
		try {
			bw = new BufferedWriter(new FileWriter(file));

			//priorities
			SectorIterator iterator = new SectorIterator(status);
			StringBuffer prioritiesLine = new StringBuffer();
			Sector actual;
			while ((actual = iterator.nextSector()) != null) {
				switch (actual.condition) {
				case Const.ENEMY_SHIP:
				case Const.NEXT_ROUND_SHOT:
				case Const.UNKNOWN:
				case Const.PROBABLY_BLANK:
					prioritiesLine.append(actual.priority + " ");
				}
			}
			bw.write(prioritiesLine.toString());
			bw.newLine();
			
			/////////////////////////////////
			////////////////////////////////
			///////////////////////////////
			
			if (lastShot != null) bw.write(" "); //last shot
			bw.newLine();
			StringBuffer currLine = null;
			for (int row = 0; row<status.battlefield.length; row++) {
				currLine = new StringBuffer();				
				for (int column = 0; column<status.battlefield[row].length; column++) {
					currLine.append(status.battlefield[column][row].condition);
				}
				bw.write(currLine.toString());
				bw.newLine();
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

	private static boolean loadStatus() {
		BufferedReader br = null;
		status = new ActualStatus();
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
		try {	//nacitanie zo suboru
			File file = new File(logFileName);
			if (!file.isFile()) {
				System.err.println("System input file not found. Loaded default status.");
				loadDefaultStatus();
				return true;
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine();
			String[] splitLine;
			if (currentLine != null) {  //status line
				splitLine = currentLine.split("[^0-9]+");
				status.side = Integer.parseInt(splitLine[0]);
				status.roundsToEnd = Integer.parseInt(splitLine[1]);
				status.round = 151 - status.roundsToEnd;
				status.specialShots = Integer.parseInt(splitLine[2]);
			}
			int battlefieldRow = 0;
			while ((currentLine = br.readLine()) != null) {  //citanie riadkov
				if (battlefieldRow<15 && currentLine.length() ==14) {  
					for (int column = 0; column<currentLine.length(); column++) {  //citanie znakov v riadku
						status.battlefield[column][battlefieldRow] = new Sector(parseCondition(currentLine.charAt(column)) , column, battlefieldRow);
					}
					battlefieldRow++;
				} else {
					System.err.println("Vstupny subor nema spravny format. Posledny riadok: " + (battlefieldRow + 1) + ", pocet znakov: " + currentLine.length());
					battlefieldRow++;
				}
			}
			if (battlefieldRow != 14) {
				System.err.println("Vstupny subor nebol nacitany spravne posledny nacitany riadok: " + battlefieldRow);
			}

		} catch (IOException e) {
			System.err.println("Nieje mozne citat system input.");
			loadDefaultStatus();
			return true;
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie system input nebolo ukoncene.");
			}

		}
		return false;
	}
	
	
	private static void loadDefaultStatus() {
		status.side = 1;
		status.roundsToEnd = 150;
		status.specialShots = 10;
		for (int row = 0; row<status.battlefield.length;row++) {
			for (int column = 0; column<status.battlefield[row].length; column++) {
				status.battlefield[column][row] = new Sector(Const.UNKNOWN,Const.PRIOR_UNKNOWN,column,row);
			}
		}
	}
	
	private static int parseCondition(char charAt) {
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

	/*
		for (int row = 0; row<status.battlefield.length;row++) {
			for (int column = 0; column<status.battlefield[0].length;column++) {

			}
		}
		return null;

	}

	 */

}
