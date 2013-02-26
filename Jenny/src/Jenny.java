
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import usedConsts.Const;
import usedConsts.StatusConsts;

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

		Strategies.doSomeLogic(status);
		saveStatusToLog();
		if (Const.TIMER) System.err.println("ROUND " + status.round + " Time: " + (watch.actualTime()/1000000) + "ms."); 
		System.out.println(status.getActionWord());
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
					if (actual.getCondition() == Const.CONDITION_ALLY_SHIP) {
						actual.makeNearestBlank(status);
					}
				}
				return;
			}
			br = new BufferedReader(new FileReader(file));

			currentLine = br.readLine(); //initialize priorities for system input
			String[] prioritiesLine = currentLine.split("[^0-9]+");
			int priorityIndex = 0;

			currentLine = br.readLine(); //last move
			int priorTemp = 0;

			int battlefieldRow = 0;
			int logCondition = 0;
			while ((currentLine = br.readLine()) != null) {  //posun na dalsi riadok
				for (int column = 0; column< currentLine.length() && column<StatusConsts.SECTOR_SIZE; column++) {  //prechod riadkom
					logCondition = parseCondition(currentLine.charAt(column));
					if (!badInputFile) {
						//priority for unknowns
						if (logCondition == Const.CONDITION_UNKNOWN && priorityIndex < prioritiesLine.length) {
							status.getSector(column, battlefieldRow).setStats(null, Integer.parseInt(prioritiesLine[priorityIndex]));
							priorityIndex++;
						}
						
						compareInputVSLog(status.getSector(column, battlefieldRow), logCondition);


					} else {  //nebol spravne nacitany input ... ziadne nove informacie o hre ... prepisanie dat z logu
						System.err.println("ROUND " + status.getRound() + "Nenacitany system input file.");
						if (logCondition == Const.CONDITION_UNKNOWN) {
							priorTemp = Const.PRIOR_UNKNOWN;
							status.setSector(logCondition, priorTemp, column, battlefieldRow);
						} else {
							status.setSector(logCondition, column, battlefieldRow);
						}
					}

				}
				battlefieldRow++;
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
		if (input == Const.CONDITION_ALLY_SHIP) {  //its ally ship there
			sector.makeNearestBlank(status);
		} else if (input == Const.CONDITION_SOME_SHOT) {  //someone shot, no hit
			switch (log) {  //before:
			case Const.CONDITION_ENEMY_SHIP:
			case Const.CONDITION_BLANK:
			case Const.CONDITION_NEXT_SHOT:
			case Const.CONDITION_UNKNOWN:
				//check if we shot (torpedo or firework) this way else its enemy shot
				sector.setStats(Const.CONDITION_ENEMY_SHOT, null);
				break;
			case Const.CONDITION_OUR_SHOT:  //old our shot
			case Const.CONDITION_ENEMY_SHOT:  //old enemy shot
				sector.setStats(log, null); 
				break;
			case Const.CONDITION_SOME_SHOT:    //possible if there was problem with log in last round (copyed directly from input) or this is first round and the shot is from enemy
				sector.setStats(Const.CONDITION_ENEMY_SHOT, null);
				break;
			}
		} else if (input == Const.CONDITION_ALLY_SUNK) {  //ally ship sunken
			if (log == Const.CONDITION_ALLY_SHIP) {
				//enemy new hit
			}
		} else if (input == Const.CONDITION_ENEMY_SUNK) {  //enemy ship sunken
			switch (log) {
			case Const.CONDITION_ENEMY_SHIP:  //cool logic - reapeat and won
				break;
			case Const.CONDITION_UNKNOWN:  //something destroyed enemy ship (special bomb/enemy)
			case Const.CONDITION_OUR_SHOT:  //last shot succeded - continue in shoting around
				ActualStatus.makeNextShot(status.getNeighbors(sector, 1) );
				System.err.println("Control: "+ status.getSector(3, 8).getCondition());
				break;
			case Const.CONDITION_BLANK:
				break;
			case Const.CONDITION_NEXT_SHOT:  //special bomb succeded
				break;
			default:  //enemy destroyed own ship
				break;
			}
		} else if (input == Const.CONDITION_UNKNOWN) {  //unknown - in log is our logic
			switch (log) {
			case Const.CONDITION_ALLY_SHIP:
			case Const.CONDITION_OUR_SHOT:
			case Const.CONDITION_ENEMY_SHOT:
			case Const.CONDITION_ALLY_SUNK:
			case Const.CONDITION_ENEMY_SUNK:
				sector.setStats(log,null);  //problems with system input - loadead from default
				break;
			case Const.CONDITION_ENEMY_SHIP:  
				sector.setStats(log,null);
				break;
			case Const.CONDITION_BLANK:
				sector.setStats(log,null);
				break;
			case Const.CONDITION_NEXT_SHOT:
				sector.setStats(log,null);
				break;
			default: //copy from input
				break;
			}
		}
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
		try {
			bw = new BufferedWriter(new FileWriter(file));

			//priorities
			SectorIterator iterator = new SectorIterator(status);
			StringBuffer prioritiesLine = new StringBuffer();
			Sector actual;
			while ((actual = iterator.nextSector()) != null) {
				switch (actual.getCondition()) {
				case Const.CONDITION_UNKNOWN:
					if (Const.HARD_DEBUG) System.err.println("Saving priority for x" + actual.getXPos() + " y" + actual.getYPos() + " as " + actual.getPriority());
					prioritiesLine.append(actual.getPriority() + " ");
				}
			}
			bw.write(prioritiesLine.toString());
			bw.newLine();

			/////////////////////////////////
			////////////////////////////////
			///////////////////////////////

			bw.write(status.getActionWord()); //last shot
			bw.newLine();
			StringBuffer currLine = null;
			for (int row = 0; row<StatusConsts.SECTOR_SIZE; row++) {
				currLine = new StringBuffer();				
				for (int column = 0; column<StatusConsts.SECTOR_SIZE; column++) {
					currLine.append(status.getSector(column, row).getCondition());
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
				status.battlefield[column][row] = new Sector(Const.CONDITION_UNKNOWN,Const.PRIOR_UNKNOWN,column,row);
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
			condition = Const.CONDITION_ALLY_SHIP; break;  //ally ship, floating
		case '2':
			condition = Const.CONDITION_ENEMY_SHIP; break;  //enemy ship = max priority
		case '3':          //bad sector in log
		case '.':
			condition = Const.CONDITION_SOME_SHOT; break;  //nothing, hit
		case '4':
			condition = Const.CONDITION_OUR_SHOT; break;  //our shot on nothing  /extends 3
		case '5':
			condition = Const.CONDITION_ENEMY_SHOT; break;  //enemy shot on nothing  /extends 3
		case '6':
		case '*':
			condition = Const.CONDITION_ALLY_SUNK; break;  //ally ship, sunk
		case '7':
		case '+':
			condition = Const.CONDITION_ENEMY_SUNK; break;  //enemy ship, sunk
		case '8':
			condition = Const.CONDITION_BLANK; break;  //probably blank sector
		case '9':
			condition = Const.CONDITION_NEXT_SHOT; break;  //next round shot
		case ' ':
			condition = Const.CONDITION_UNKNOWN; break;  //unknown
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
