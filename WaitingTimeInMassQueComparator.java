import java.util.Comparator;

public class WaitingTimeInMassQueComparator implements Comparator<Player>{

	@Override
	public int compare(Player o1, Player o2) {
		if(o1.getPlayerTotalWaitingTimeInMassQue()<o2.getPlayerTotalWaitingTimeInMassQue()) {
			return -1;
		}
		else if(o1.getPlayerTotalWaitingTimeInMassQue()>o2.getPlayerTotalWaitingTimeInMassQue()) {
			return 1;
		}
		else {
			if(o1.getID()<o2.getID()) {
				return -1;
			}
			else if(o1.getID()>o2.getID()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	
}
