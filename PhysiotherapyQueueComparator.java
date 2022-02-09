import java.util.Comparator;

public class PhysiotherapyQueueComparator implements Comparator<Player> {

	@Override
	public int compare(Player o1, Player o2) {
		if(o1.getTrainingTime()<o2.getTrainingTime()) {
			return 1;
		}
		else if(o1.getTrainingTime()>o2.getTrainingTime()) {
			return -1;
		}
		else {
			if(o1.getEntranceTimetoPhysiotherapyQueue()<o2.getEntranceTimetoPhysiotherapyQueue()) {
				return -1;
			}
			else if(o1.getEntranceTimetoPhysiotherapyQueue()>o2.getEntranceTimetoPhysiotherapyQueue()) {
				return 1;
			}
			else {
				if(o1.getID()<o2.getID()) {
					return -1;
				}
				else if(o1.getID()>o2.getID()) {
					return 1;
				}
				return 0;
			}
		}	
	}

}
