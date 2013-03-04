package testOhnostroj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ohnostroj {

	public static void main(String[] args) {
		loadStatus();
	}
	
	private static void loadStatus() {
		BufferedReader br = null;
		String currentLine = "";
		String logFileName = "battlefield.txt";
		try {	//nacitanie zo suboru
			File file = new File(logFileName);
			if (!file.isFile()) {
				System.err.println("System input file not found.");
				return;
			}
			br = new BufferedReader(new FileReader(file));
			currentLine = br.readLine();
			String[] splitLine;
			int round = 0;
			if (currentLine != null) {  //status line
				splitLine = currentLine.split("[^0-9]+");
				round = 150 - Integer.parseInt(splitLine[1]);
			}
			if (round != 2) {
				System.out.println("m 0 0");
				return;
			}
			int battlefieldRow = 0;
			while ((currentLine = br.readLine()) != null) {  //citanie riadkov
				if (battlefieldRow<15 && currentLine.length() ==14) {  
					for (int column = 0; column<currentLine.length(); column++) {  //citanie znakov v riadku
						if (column>3 && column < 9 && battlefieldRow>3 && battlefieldRow<9)
							if (currentLine.charAt(column) == '1') {
								System.out.println("f " + column + " " + battlefieldRow);
								return;
							}
					}
					battlefieldRow++;
				} else {
					System.err.println("Vstupny subor nema spravny format. Posledny riadok: " + (battlefieldRow + 1) + ", pocet znakov: " + currentLine.length());
					battlefieldRow++;
				}
			}
		} catch (IOException e) {
			System.err.println("Nieje mozne citat system input.");
			return;
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				System.err.println("Citanie system input nebolo ukoncene.");
			}

		}
	}
}
