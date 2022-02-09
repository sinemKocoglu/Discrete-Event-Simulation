import java.util.PriorityQueue;

public class Event implements Comparable<Event>{
	private String type;
	private Player player;
	private double arrivalTime;
	private double duration;
	
	private static double Time = 0;
	
	private static PriorityQueue<Event> eventsQueue = new PriorityQueue<>();
	
	public Event(String type, Player player, double arrivalTime, double duration) {
		this.type = type;
		this.player = player;
		this.arrivalTime = arrivalTime;
		this.duration = duration;
	}

	@Override
	public int compareTo(Event o) {
		if(arrivalTime<o.arrivalTime) {       
			return -1;
		}
		else if(arrivalTime>o.arrivalTime) {
			return 1;
		}
		else {
			if(player.getID()<o.player.getID()) {
				return -1;
			}
			else if(player.getID()>o.player.getID()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	
	public static void eventSchedule(Event e) {
		eventsQueue.add(e);
	}

	/**
	 * simulation of discrete events in time.
	 */
	public static void simulate() {	
		while(!eventsQueue.isEmpty()) {
			Event e = eventsQueue.poll();
			if(e.type.equals("t")) {
				e.player.training(e);
			}
			else if(e.type.equals("m")) {
				e.player.massage(e);
			}
			else if(e.type.equals("endOfTraining")) {
				e.player.endTraining(e);
			}
			else if(e.type.equals("endOfMassage")) {
				e.player.endMassage(e);
			}
			else if(e.type.equals("endOfPhysiotherapy")) {
				e.player.endPhysiotherapy(e);
			}
			Time = e.arrivalTime;
		}
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @return the arrivalTime
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the time
	 */
	public static double getTime() {
		return Time;
	}

}
