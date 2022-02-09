import java.util.PriorityQueue;

public class Physiotherapist implements Comparable<Physiotherapist> {

	private int ID;
	private float serviceTime;
	
	static PriorityQueue<Physiotherapist> freePtherapists = new PriorityQueue<>();   //comparison according to id of player
	
	public Physiotherapist(int ID,float serviceTime) {
		this.ID = ID;
		this.serviceTime = serviceTime;
	}

	@Override
	public int compareTo(Physiotherapist o) {
		if(this.ID<o.getID()) {
			return -1;
		}
		else if(this.ID>o.getID()) {
			return 1;
		}
		return 0;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the serviceTime
	 */
	public float getServiceTime() {
		return serviceTime;
	}
	
}
