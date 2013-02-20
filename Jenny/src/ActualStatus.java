
public class ActualStatus {
	
	interface StatusConsts{
		public static final int SECTOR_SIZE=14;
		public static final int HEUR_OFFSET=2;
		// TODO: toto prepisat tak aby to importovalo konstantu zo Sector 
		public static final int HEUR_THRESHOLD=100;
	}
	public int side=0;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	public Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	
	//TODO: otestovat
	
	public int[] findAirstrikePos(){
		int PosBest[]= {15,15};
		int best=StatusConsts.HEUR_THRESHOLD+1;
		int currHeurValue;
		
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				currHeurValue=battlefield[xAxis][yAxis].getHeurValue();
				if(currHeurValue<=StatusConsts.HEUR_THRESHOLD)
				{
					if(currHeurValue<best)
					{
						PosBest[0]=xAxis;
						PosBest[1]=yAxis;
						best=currHeurValue;
					}
						
				}
			}
			}
		return PosBest;
	}
	
	public void calculateHeuristics()
	{
		Sector currSector;
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				/*there's no point in calculating it over and over again
				 * if we know it's a bad location to do an air strike*/
				currSector=battlefield[xAxis][yAxis];
				if(currSector.getHeurValue()<StatusConsts.HEUR_THRESHOLD){
					currSector.setHeurValue(
							calculateSectorHeuristics(xAxis,yAxis));
				}
				battlefield[xAxis][yAxis]=currSector;
			}
		}
	}
	
	public void print_heuristics()
	{
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				System.out.printf("%4d",battlefield[xAxis][yAxis].getHeurValue());
			}
			System.out.println();
		}
	}
    
	private int calculateSectorHeuristics(int x, int y)
	{
		int value;
		int retval=0;
		for(int xAxis=(x-StatusConsts.HEUR_OFFSET);
				xAxis<(x+StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=y-StatusConsts.HEUR_OFFSET;
					yAxis<(y+StatusConsts.HEUR_OFFSET);
					yAxis++){
				if(retval>StatusConsts.HEUR_THRESHOLD)
				{
					return retval;
				}
				try{
					value=battlefield[xAxis][yAxis].getSpecialValue();
				}
				catch(ArrayIndexOutOfBoundsException ex)
				{
					value=100;
				}
				retval+=value;
			}
		}
		return retval;
	}

}
