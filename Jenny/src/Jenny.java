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
		Sector temp = selectRandom(status);
		System.out.println("Nahodna pozicia: [" + temp.xPos + ","+ temp.yPos + "] " + temp.condition);

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
						status.battlefield[index][column] = parseSector(currentLine.charAt(column));
						status.battlefield[index][column].setPosition(index, column);
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

	private static Sector parseSector(char charAt) {
		int condition = 0;
		
		//0 for unknown, 1 for own ship, 2 for enemy ship
		//3 for shot, 4 for own ship hit, 5 for enemy ship hit
		//6 for high priority
		
		switch (charAt) {
		case 49:  //one
			condition = 1; break;  //own ship, floating
		case 42:  //star
			condition = 4; break;  //own ship, sunk
		case 43:  //plus
			condition = 5; break;  //enemy ship, sunk
		case 46:  //dot
			condition = 3; break; //nothing, hit
		case 32:  //space
			condition = 0; break;  //unknown
		}
		return new Sector(condition);
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