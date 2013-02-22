import usedConsts.Heuristic;
import usedConsts.StatusConsts;

public class ActualStatus {
	
	public int side=0;
	public int round = 0;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	public Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	
	//status variables:
	public Sector enemyJustHit = null;
	
	
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
				if(currSector.getSpecialValue()==Heuristic.OWN_SHIP){
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
		int retval=0;
		int value;
		for(int xAxis=x-StatusConsts.HEUR_OFFSET;
				xAxis<(x+StatusConsts.HEUR_OFFSET);
				xAxis++){
			
				for(int yAxis=y-StatusConsts.HEUR_OFFSET;
						yAxis<(y+StatusConsts.HEUR_OFFSET);
						){
					if(retval>StatusConsts.HEUR_THRESHOLD)
					{
						return retval;
					}
					value=tryHeurValue(xAxis,yAxis);
					retval+=value;
					//aby sme ratali len okraje stvorca 5x5 v okoli vybraneho bodu
					if((xAxis==x-StatusConsts.HEUR_OFFSET) || 
							(xAxis==x+StatusConsts.HEUR_OFFSET)){
						yAxis++;
					}
					else{
						yAxis+=2*StatusConsts.HEUR_OFFSET;
					}
				}
		}
		return retval;

			/*
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
		*/
	}
	
	private int tryHeurValue(int x, int y)
	{
		int value;
		try{
			value=battlefield[x][y].getSpecialValue();
		}
		catch(ArrayIndexOutOfBoundsException ex)
		{
			value=100;
		}
		return value;
	}
}
