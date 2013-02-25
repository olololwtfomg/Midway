import usedConsts.StatusConsts;


public class SectorIterator {
	
	private int iteratorIndex = 0;
	private ActualStatus status;
	
	public SectorIterator(ActualStatus status) {
		this.status = status;
	}
	
	public void reset() {
		iteratorIndex = 0;
	}
	
	public Sector nextSector() {
		if (iteratorIndex < (StatusConsts.SECTOR_SIZE * StatusConsts.SECTOR_SIZE) ) {
			return status.battlefield[iteratorIndex % StatusConsts.SECTOR_SIZE][(int) (iteratorIndex++ / StatusConsts.SECTOR_SIZE)];
		} else return null;
	}
}
