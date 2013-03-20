import usedConsts.StatusConsts;


public class SectorIterator {
	
	private int iteratorIndex = 0;
	
	public SectorIterator() {
	}
	
	public void reset() {
		iteratorIndex = 0;
	}
	
	public boolean hasNext() {
		return iteratorIndex < (StatusConsts.SECTOR_SIZE * StatusConsts.SECTOR_SIZE);
	}
	
	public Sector nextSector() {
		if (iteratorIndex < (StatusConsts.SECTOR_SIZE * StatusConsts.SECTOR_SIZE) ) {
			return ActualStatus.getSector(iteratorIndex % StatusConsts.SECTOR_SIZE, (int) (iteratorIndex++ / StatusConsts.SECTOR_SIZE));
		} else return null;
	}
}
