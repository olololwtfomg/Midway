

public class SectorIterator implements Constants{
	
	private int iteratorIndex = 0;
	
	public SectorIterator() {
	}
	
	public void reset() {
		iteratorIndex = 0;
	}
	
	public boolean hasNext() {
		return iteratorIndex < (SECTOR_SIZE * SECTOR_SIZE);
	}
	
	public Sector nextSector() {
		if (iteratorIndex < (SECTOR_SIZE * SECTOR_SIZE) ) {
			return ActualStatus.getSector(iteratorIndex % SECTOR_SIZE, (int) (iteratorIndex++ / SECTOR_SIZE));
		} else return null;
	}
}
