import java.util.Comparator;

public class TrainingQueueComparator implements Comparator<Player>{

	@Override
	public int compare(Player o1, Player o2) {
		if(o1.getEntranceTimetoTrainingQueue()<o2.getEntranceTimetoTrainingQueue()) {
			return -1;
		}
		else if(o1.getEntranceTimetoTrainingQueue()>o2.getEntranceTimetoTrainingQueue()) {
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
