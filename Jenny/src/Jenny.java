
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Jenny implements Constants{
	private static final String INPUT_FILE = "battlefield.txt";
	private static final String LOG_FILE = "log.txt";
	//private static final String WIN_PATH_SEP = "\\";
	//private static final String UNIX_PATH_SEP = "/";

/*	private enum OS_type {
		WINDOWS,LINUX 
	}

	private static OS_type os_type=OS_type.LINUX;*/

	public static void main(String[] args) {
		/////////////////////////////////////////////////////
		Stopwatch watch = new Stopwatch(true);///////////////////
		/////////////////////////////////////////////////////
		loadLog(loadStatus());
		
		for (String argument: args) {
			if (argument == "grid4") ActualStatus.setGrid(ActualStatus.Grid.STATIC4);
			else if (argument == "everySecond") ActualStatus.setGrid(ActualStatus.Grid.EVERY_SECOND);
			else ActualStatus.setGrid(ActualStatus.Grid.STATIC3);
		}

		Strategies.doSomeLogic();
		saveStatusToLog();
		System.err.println("ROUND " + ActualStatus.round + " Time: " + (watch.actualTime()/1000000) + "ms."); 
		System.out.println(ActualStatus.getActionWord());
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
/*		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(LOG_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(LOG_FILE_PATH,UNIX_PATH_SEP);
			break;
		}*/

		try {
			File file = new File(LOG_FILE);
			if (!file.isFile()) {
				System.err.println("Log neexistuje.");
				//log is broken ... create new based on system input

				//set blank near our ships
				Sector actual;
				SectorIterator iterator = new SectorIterator();
				while ((actual = iterator.nextSector()) != null) {
					if (actual.getState() == State.ALLY_SHIP) {
						ActualStatus.makeBlank(ActualStatus.getNeighbors(actual, NEIGHBORS_ARROUND));
					}
				}
				return;
			}
			br = new BufferedReader(new FileReader(file));

			currentLine = br.readLine(); //initialize priorities for system input
			String[] prioritiesLine = currentLine.split("[^0-9]+");
			int priorityIndex = 0;

			currentLine = br.readLine(); //last move

			State logState;
			for (int battlefieldRow = 0; battlefieldRow<SECTOR_SIZE; battlefieldRow++) {
				currentLine = br.readLine();
				if (currentLine == null) {
					System.err.println("Error while reading log");
					return;
				}
				for (int column = 0; column< currentLine.length() && column<SECTOR_SIZE; column++) { //prechod riadkom po znakoch/sectoroch
					logState = State.getState(currentLine.charAt(column));
					
					//set priorities for unknowns
					if (logState == State.UNKNOWN) {
						if (priorityIndex < prioritiesLine.length) {
							ActualStatus.getSector(column, battlefieldRow).setPriority(Integer.parseInt(prioritiesLine[priorityIndex++]));
						}  //default priority sets loadStatus
					}
					
					//set conditions
					if (!badInputFile) {
						compareInputVSLog(ActualStatus.getSector(column, battlefieldRow), logState);								
					} else {
						ActualStatus.getSector(column, battlefieldRow).setState(logState);
					}
					
				}
			}
		} catch (IOException e) {
			System.err.println("Nieje mozne citat log.");
			//log is broken ... load from system input (was done probabbly) and save new log
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie logu nebolo ukoncene.");
			}

		}
	}

	private static void compareInputVSLog(Sector sector, State logState) {
		State systemState = sector.getState(); //status from actual input
		if (systemState == State.ALLY_SHIP) {  //its ally ship there
			ActualStatus.makeBlank(ActualStatus.getNeighbors(sector, NEIGHBORS_ARROUND));
		} else if (systemState == State.SOME_SHOT) {  //someone shot, no hit
			switch (logState) {  //before:
			case BLANK:
			case UNKNOWN:
				//check if we shot (torpedo or firework) this way else its enemy shot
				sector.setState(State.ENEMY_SHOT);
				break;
			case OUR_SHOT:  //old our shot
			case ENEMY_SHOT:  //old enemy shot
				sector.setState(logState);
				break;
			case SOME_SHOT:    //possible if there was problem with log in last round (copyed directly from input) or this is first round and the shot is from enemy
				sector.setState(State.ENEMY_SHOT);
				break;
			default: break;
			}
		} else if (systemState == State.ALLY_SUNK) {  //ally ship sunken
			if (logState == State.ALLY_SHIP) {
				//enemy new hit
			}
		} else if (systemState == State.ENEMY_SUNK) {  //enemy ship sunken
			switch (logState) {
			case UNKNOWN:  //something destroyed enemy ship (special bomb/enemy)
			case OUR_SHOT:  //last shot succeded - continue in shoting around
				ActualStatus.makeNextShot(ActualStatus.getNeighbors(sector, NEIGHBORS_LINEAR) );
				break;
			case BLANK:
				break;
			default:  //enemy destroyed own ship
				break;
			}
		} else if (systemState == State.UNKNOWN) {  //unknown - in log is our logic
			switch (logState) {
			case ALLY_SHIP:
			case ENEMY_SUNK:
			case ALLY_SUNK:
			case OUR_SHOT:
			case ENEMY_SHOT:
				if (DEBUG) {  //only for offline version
					sector.setState(logState);
				}
				break;
			case BLANK:
				sector.setState(logState);
				sector.setPriority(PRIORITY_BLANK);
			default: break;
			}
		}
	}

	private static boolean saveStatusToLog() {
/*		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(LOG_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(LOG_FILE_PATH,UNIX_PATH_SEP);
			break;
		}*/

		File file = new File(LOG_FILE);
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
			SectorIterator iterator = new SectorIterator();
			StringBuffer prioritiesLine = new StringBuffer();
			Sector actual;
			while ((actual = iterator.nextSector()) != null) {
				switch (actual.getState()) {
				case UNKNOWN:
					if (HARD_DEBUG) System.err.println("Saving priority for x" + actual.getXPos() + " y" + actual.getYPos() + " as " + actual.getPriority());
					prioritiesLine.append(actual.getPriority() + " ");
				default:
					break;
				}
			}
			bw.write(prioritiesLine.toString());
			bw.newLine();

			/////////////////////////////////
			////////////////////////////////
			///////////////////////////////
			bw.write(ActualStatus.getActionWord()); //last shot
			bw.newLine();
			StringBuffer currLine = null;
			for (int row = 0; row<SECTOR_SIZE; row++) {
				currLine = new StringBuffer();				
				for (int column = 0; column<SECTOR_SIZE; column++) {
					currLine.append(ActualStatus.getSector(column, row).getState().getLogValue());
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
		String currentLine = "";
/*		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName= String.format(INPUT_FILE_PATH,WIN_PATH_SEP);
			break;
		default:
			logFileName= String.format(INPUT_FILE_PATH,UNIX_PATH_SEP);
			break;
		}*/
		try {	//nacitanie zo suboru
			File file = new File(INPUT_FILE);
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
				ActualStatus.side = Integer.parseInt(splitLine[0]);
				ActualStatus.roundsToEnd = Integer.parseInt(splitLine[1]);
				ActualStatus.round = 149 - ActualStatus.roundsToEnd;
				ActualStatus.specialShots = Integer.parseInt(splitLine[2]);
			}
			int battlefieldRow = 0;
			while ((currentLine = br.readLine()) != null) {  //citanie riadkov
				if (battlefieldRow<15 && currentLine.length() ==14) {  
					for (int column = 0; column<currentLine.length(); column++) {  //citanie znakov v riadku
						ActualStatus.setSector(State.getState(currentLine.charAt(column)), column, battlefieldRow);
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
		ActualStatus.side = 1;
		ActualStatus.roundsToEnd = 150;
		ActualStatus.specialShots = 0;
		for (int row = 0; row<SECTOR_SIZE;row++) {
			for (int column = 0; column<SECTOR_SIZE; column++) {
				ActualStatus.setSector(State.UNKNOWN, column, row);
			}
		}
	}
}
