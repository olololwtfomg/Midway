import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


public class Jenny {


	private enum OS_type {
		WINDOWS,LINUX 
	}

	private static OS_type os_type=OS_type.LINUX;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ActualStatus status = loadStatus();
		loadLog(status);
		Sector temp = selectRandom(status);
		System.out.println(status.battlefield[0][1].condition);
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
			logFileName=".\\src\\battlefield.txt";
			break;
		case LINUX:
			logFileName="./src/battlefield.txt";
			break;
		default:
			logFileName="./src/battlefield.txt";	
			break;
		}

		try {	//nacitanie zo suboru
			File file = new File(logFileName);
			if (!file.isFile()) {
				//log is broken ... create new
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine();
			int index = 0;
			int inputCondition = 0;
			int logCondition = 0;
			while ((currentLine = br.readLine()) != null) {
				if (index<15) {
					for (int column = 0; column< currentLine.length() && column<15; column++) {
						if (!badInputFile) {
							//1 for own ship, 2 for enemy ship
							//3 for unknown shot, 4 for own shot, 5 for enemy shot
							//6 for ally ship hit, 7 for enemy ship hit,
							//8 for lowest priority, 9 for high priority, 0 for unknown

							inputCondition = status.battlefield[index][column].condition;
							logCondition = parseStatus(currentLine.charAt(column));
							if (inputCondition == 1) {  //its ally ship there
								//copy from input
							} else if (inputCondition == 3) {  //someone shot, no hit
								switch (logCondition) {
								case 3:  //check if we shot in last round or its enemy shot (special mode if torpedo or firework used)
									break;
								case 4:  //old shot
									break;
								case 5:  //old shot
									break;
								default: //copy from input
									break;
								}
							} else if (inputCondition == 6) {  //ally ship sunken
								switch (logCondition) {
								case 1:  //enemy new hit
									break;
								case 6:  //old or expected (own hit)
									break;
								default: //copy from input
									break;
								}
							} else if (inputCondition == 7) {  //enemy ship hit
								switch (logCondition) {
								case 2:  //cool logic - reapeat and won
									break;
								case 4:  //last shot succeded
								case 7:  //old record
									break;
								case 8:  //
									break;
								case 9: 
									break;
								default:  //enemy destroyed own ship
									break;
								}
							} else if (inputCondition == 0) {  //unknown
								switch (logCondition) {
								case 1:
									break;
								case 2: 
									break;
								case 3:
									break;
								case 4:
									break;
								case 5: 
									break;
								case 6:
									break;
								case 7: 
									break;
								case 8: 
									break;
								case 9: 
									break;
								}
							}
						}

					}
					index++;
				} else {
					System.err.println("Vstupny subor nema spravny format. Posledny riadok: " + (index - 1) + ", pocet znakov: " + currentLine.length());
					index++;
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

	private static ActualStatus loadStatus() {
		BufferedReader br = null;
		ActualStatus status = new ActualStatus();
		String currentLine = "";
		String logFileName;
		switch(Jenny.os_type)
		{
		case WINDOWS:
			logFileName=".\\src\\battlefield.txt";
			break;
		case LINUX:
			logFileName="./src/battlefield.txt";
			break;
		default:
			logFileName="./src/battlefield.txt";	
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

	private static int parseStatus(String str) {
		return parseStatus(str.charAt(0));
	}

	private static int parseStatus(char charAt) {
		int condition = 0;

		//1 for own ship, 2 for enemy ship
		//3 for unknown shot, 4 for own shot, 5 for enemy shot
		//6 for ally ship hit, 7 for enemy ship hit,
		//8 for lowest priority, 9 for high priority, 0 for unknown

		switch (charAt) {
		case 49:  //one
			condition = 1; break;  //ally ship, floating
		case 50:  //two
			condition = 2; break;  //enemy ship = max priority
		case 51:  //three          //bad logic sector in log
		case 46:  //dot
			condition = 3; break;  //nothing, hit
		case 52:  //four
			condition = 4; break;  //our shot on nothing  /extends 3
		case 53:  //five
			condition = 5; break;  //enemy shot on nothing  /extends 3
		case 54:  //six
		case 42:  //star
			condition = 6; break;  //ally ship, sunk
		case 55:  //seven
		case 43:  //plus
			condition = 7; break;  //enemy ship, sunk
		case 56:  //eight
			condition = 8; break;  //probably blank sector
		case 57:  //nine
			condition = 9; break;  //next round shot
		case 32:  //space
			condition = 0; break;  //unknown
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