
public class ActualStatus {
	
	interface StatusConsts{
		public static final int SECTOR_SIZE=14;
	}
	public int side=0;
	public int roundsToEnd = 0;
	public int specialShots = 10;
	public Sector[][] battlefield = 
			new Sector[StatusConsts.SECTOR_SIZE][StatusConsts.SECTOR_SIZE];
	
	/*
	public void calculateHeuristics()
	{
		for
	}
	*/

}
