
public class ActualStatus {
	
	interface StatusConsts{
		public static final int SECTOR_SIZE=14;
		public static final int HEUR_OFFSET=2;
		public static final int HEUR_THRESHOLD=150;
	}
	public int side=0;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	public Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	
	
	public void calculateHeuristics()
	{
		int value;
		for(int xAxis=StatusConsts.HEUR_OFFSET;
				xAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
				xAxis++){
			for(int yAxis=StatusConsts.HEUR_OFFSET;
					yAxis<(StatusConsts.SECTOR_SIZE-StatusConsts.HEUR_OFFSET);
					yAxis++){
				
			}
		}
	}

	private int calculateSectorHeuristics(int x, int y)
	{
		int value=0;
		//for(int i)
		return value;
	}

}
