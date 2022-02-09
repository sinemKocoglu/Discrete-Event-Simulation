import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

public class Player implements Comparable<Player>{
	
	private int ID;
	private int skillLevel;
	
	static PriorityQueue<Player> trainingQueue = new PriorityQueue<>(new TrainingQueueComparator());   
	static PriorityQueue<Player> physiotherapyQueue = new PriorityQueue<>(new PhysiotherapyQueueComparator()); 
	static PriorityQueue<Player> massageQueue = new PriorityQueue<>();   //sorting according to skill level
	
	private static int maxLengTraQue = 0;   //max lenght of training queue
	private static int maxLengPhysQue = 0;  //max lenght of physiotherapy queue
	private static int maxLengMassQue = 0;  //max lenght of massage queue
	
	private static double totWaitTimeTraQue = 0;      //total waiting time in training queue for all players
	private static double totWaitTimePhysQue = 0;     //total waiting time in physiotherapy queue for all player
	private static double totWaitTimeMassQue = 0;     //total waiting time in massage queue for all players
	
	private static double totalTrainingTime = 0;       //total training time for all players
	private static double totalPhysiotherapyTime = 0;  //total physiotherapy time for all players
	private static double totalMassageTime = 0;        //total massage time for all players
	
	private double playerTotalWaitingTimeInPhysQue;     //total waiting time in physiotherapy queue for a specific player
	private static double maxWaitingTimeInPhysQue = 0;  // waiting time of the player waited the most in physiotherapy queue
	private static int PlayerIdWaitedTheMostInPhysQue = 0;
	
	private double playerTotalWaitingTimeInMassQue;     //total waiting time in massage queue for a specific player
	private static PriorityQueue<Player> waitingTimeInMassQue = new PriorityQueue<>(new WaitingTimeInMassQueComparator());  //players entered massage queue and having 3 massage attempt will be added to this queue to find the player having minimum waiting time in massage queue.
	private static Map<Integer,Player> playersHaving3MassageAttempt=new HashMap<Integer,Player>();	//this map is to hold players having 3 attempts with their updated fields for waitingTimeInMassQue
	
	private Physiotherapist p;
	
	private double entranceTimetoTrainingQueue;
	private double exitTimefromTrainingQueue;
	
	private double entranceTimetoPhysiotherapyQueue;
	private double exitTimefromPhysiotherapyQueue;
	
	private double entranceTimetoMassageQueue;
	private double exitTimefromMassageQueue;
	
	private double exitTimeFromTraining;
	private double exitTimeFromPhysiotherapy;
	private double exitTimeFromMassage;
	
	private double entranceTimeToTurnaround;
	private double exitTimeFromTurnaround;
	private static double totalTurnaroundTimes=0;
	private static int numOfTurnaround=0;
	
	private int numOfMassageAttempt;
	
	private static int totalNumOfTraining = 0;
	private static int totalNumOfValidMassage = 0;
	
	private double trainingTime;    //the current trainingTime
	
	private double durationOfMassage = 0;
	
	private static int numOfFreeTrainingCoaches;
	private static int numOfFreeMasseurs;
	
	private static int invalidAttemptForMassageService = 0;
	private static int cancelledAttemptForTraAndMass = 0;
	
	
	public Player(int ID, int skillLevel) {
		this.ID = ID;
		this.skillLevel = skillLevel;
		
		this.entranceTimetoTrainingQueue = -1;     // -1 means there is no entrance to or exit from a queue or service yet.
		this.exitTimefromTrainingQueue = -1;
		
		this.entranceTimetoPhysiotherapyQueue = -1;
		this.exitTimefromPhysiotherapyQueue = -1;
		
		this.entranceTimetoMassageQueue = -1;
		this.exitTimefromMassageQueue = -1;
		
		this.exitTimeFromTraining = -1;
		this.exitTimeFromPhysiotherapy= -1;
		this.exitTimeFromMassage = -1;
		
		this.entranceTimeToTurnaround=-1;
		this.exitTimeFromTurnaround=-1;
		
		this.trainingTime = 0;
		
		this.durationOfMassage = 0;
		
		this.numOfMassageAttempt = 0;
		
		this.p = null;
		
		this.playerTotalWaitingTimeInPhysQue = 0;
		this.playerTotalWaitingTimeInMassQue = 0;
		
	}
	
	public void training(Event e) {
		if(e.getPlayer().exitTimeFromTraining>=e.getArrivalTime() || e.getPlayer().exitTimeFromMassage>e.getArrivalTime() || e.getPlayer().exitTimeFromPhysiotherapy>e.getArrivalTime() || trainingQueue.contains(e.getPlayer()) || physiotherapyQueue.contains(e.getPlayer()) || massageQueue.contains(e.getPlayer())) {
			cancelledAttemptForTraAndMass++;          //If player is in any of queues or services, the attempt is cancelled.
			return;
		}
		e.getPlayer().entranceTimeToTurnaround= e.getArrivalTime();
		totalNumOfTraining++;
		if(numOfFreeTrainingCoaches == 0) {    //player has to enter the training queue
			e.getPlayer().entranceTimetoTrainingQueue = e.getArrivalTime();
			e.getPlayer().trainingTime= e.getDuration();
			trainingQueue.add(e.getPlayer());
			findMaxLenght(trainingQueue);
		}
		else if(trainingQueue.isEmpty()){    //Because there are free coaches and queue is empty, player can go directly to the coach without entering queue.
			numOfFreeTrainingCoaches--;
			e.getPlayer().trainingTime = e.getDuration();
			e.getPlayer().exitTimeFromTraining=e.getArrivalTime()+e.getDuration();
			Event.eventSchedule(new Event("endOfTraining", e.getPlayer(), e.getArrivalTime()+e.getDuration(),0.0));
		}
		else {
			e.getPlayer().entranceTimetoTrainingQueue = e.getArrivalTime();
			e.getPlayer().trainingTime = e.getDuration();
			trainingQueue.add(e.getPlayer());
			findMaxLenght(trainingQueue);
			while(numOfFreeTrainingCoaches!=0 && !trainingQueue.isEmpty()) {   //assigning players in training queue to free coaches
				Player p = trainingQueue.poll();
				p.exitTimefromTrainingQueue = e.getArrivalTime();
				p.exitTimeFromTraining = e.getArrivalTime()+p.trainingTime;
				totWaitTimeTraQue+=(p.exitTimefromTrainingQueue-p.entranceTimetoTrainingQueue);
				Event.eventSchedule(new Event("endOfTraining", p, e.getArrivalTime()+p.trainingTime,0.0));
				numOfFreeTrainingCoaches--;
			}
		}
	}
	
	public void endTraining(Event e) {
		numOfFreeTrainingCoaches++;
		totalTrainingTime+=e.getPlayer().trainingTime;
		while(numOfFreeTrainingCoaches!=0 && !trainingQueue.isEmpty()) {    //assigning players in training queue to free coaches
			Player p = trainingQueue.poll();
			p.exitTimefromTrainingQueue = e.getArrivalTime();
			p.exitTimeFromTraining = e.getArrivalTime()+p.trainingTime;
			totWaitTimeTraQue+=(p.exitTimefromTrainingQueue-p.entranceTimetoTrainingQueue);
			Event.eventSchedule(new Event("endOfTraining", p, e.getArrivalTime()+p.trainingTime,0.0));
			numOfFreeTrainingCoaches--;
		}

		if(Physiotherapist.freePtherapists.size() == 0) {   //player has to enter physiotherapy queue
			e.getPlayer().entranceTimetoPhysiotherapyQueue = e.getArrivalTime();
			physiotherapyQueue.add(e.getPlayer());
			findMaxLenght(physiotherapyQueue);
		}
		else if(physiotherapyQueue.isEmpty()){        //Because there are free physiotherapists and queue is empty, player can go directly to the physiotherapist without entering queue.
			Physiotherapist ph = Physiotherapist.freePtherapists.poll();
			e.getPlayer().p = ph;
			e.getPlayer().exitTimeFromPhysiotherapy=e.getArrivalTime()+ph.getServiceTime();
			Event.eventSchedule(new Event("endOfPhysiotherapy", e.getPlayer(), e.getArrivalTime()+ph.getServiceTime(), 0.0));
		}
		else {
			e.getPlayer().entranceTimetoPhysiotherapyQueue= e.getArrivalTime();
			physiotherapyQueue.add(e.getPlayer());
			findMaxLenght(physiotherapyQueue);
			while(!Physiotherapist.freePtherapists.isEmpty() && !physiotherapyQueue.isEmpty()) {   //assigning players in physiotherapy queue to free physiotherapists.
				Player pl = physiotherapyQueue.poll(); 
				Physiotherapist ph = Physiotherapist.freePtherapists.poll();
				pl.p = ph;         // physiotherapist(p) of player pl is ph.
				pl.exitTimefromPhysiotherapyQueue = e.getArrivalTime();
				pl.playerTotalWaitingTimeInPhysQue+= (pl.exitTimefromPhysiotherapyQueue-pl.entranceTimetoPhysiotherapyQueue);
				findPlayerWaitedTheMostInPhysQue(pl);
				pl.exitTimeFromPhysiotherapy = e.getArrivalTime()+ph.getServiceTime();
				totWaitTimePhysQue+=(pl.exitTimefromPhysiotherapyQueue-pl.entranceTimetoPhysiotherapyQueue);
				Event.eventSchedule(new Event("endOfPhysiotherapy", pl, e.getArrivalTime()+ph.getServiceTime(),0.0 ));
			}
		}
	}

	public void endPhysiotherapy(Event e) {
		e.getPlayer().exitTimeFromTurnaround= e.getArrivalTime();
		totalTurnaroundTimes+=(e.getPlayer().exitTimeFromTurnaround-e.getPlayer().entranceTimeToTurnaround);
		numOfTurnaround++;
		Physiotherapist.freePtherapists.add(e.getPlayer().p);
		totalPhysiotherapyTime+=e.getPlayer().p.getServiceTime();
		e.getPlayer().p=null;
		
		while(!Physiotherapist.freePtherapists.isEmpty() && !physiotherapyQueue.isEmpty()) {   //assigning players in physiotherapy queue to free physiotherapists
			Player pl = physiotherapyQueue.poll(); 
			Physiotherapist ph = Physiotherapist.freePtherapists.poll();
			pl.p = ph;         // physiotherapist(p) of player pl is ph.
			pl.exitTimefromPhysiotherapyQueue = e.getArrivalTime();
			pl.playerTotalWaitingTimeInPhysQue+= pl.exitTimefromPhysiotherapyQueue-pl.entranceTimetoPhysiotherapyQueue;
			findPlayerWaitedTheMostInPhysQue(pl);
			pl.exitTimeFromPhysiotherapy = e.getArrivalTime()+ph.getServiceTime();
			totWaitTimePhysQue+=(pl.exitTimefromPhysiotherapyQueue-pl.entranceTimetoPhysiotherapyQueue);
			Event.eventSchedule(new Event("endOfPhysiotherapy", pl, e.getArrivalTime()+ph.getServiceTime(),0.0 ));
		}
	}
	
	public void massage(Event e) {  
		if(e.getPlayer().numOfMassageAttempt == 3) {        //A player can take at most 3 massage service. More than 3 attempts to massage queue is invalid attempt
			invalidAttemptForMassageService++;
			return;
		}
		else if(e.getPlayer().exitTimeFromTraining>=e.getArrivalTime() || e.getPlayer().exitTimeFromMassage>e.getArrivalTime() || e.getPlayer().exitTimeFromPhysiotherapy>e.getArrivalTime() || trainingQueue.contains(e.getPlayer()) || physiotherapyQueue.contains(e.getPlayer()) || massageQueue.contains(e.getPlayer())) {
			cancelledAttemptForTraAndMass++;     //If player is in any of queues or services, the attempt is cancelled.
			return;
		}
		else {
			e.getPlayer().numOfMassageAttempt++;
			totalNumOfValidMassage++;
			if(e.getPlayer().numOfMassageAttempt==3) {
				playersHaving3MassageAttempt.put(e.getPlayer().ID, e.getPlayer());
			}
			if(numOfFreeMasseurs == 0) {     //player has to enter massage queue
				e.getPlayer().entranceTimetoMassageQueue=e.getArrivalTime();
				e.getPlayer().durationOfMassage=e.getDuration();
				massageQueue.add(e.getPlayer());
				findMaxLenght(massageQueue);
			}
			else if(massageQueue.isEmpty()){    //Because massage queue is empty, player goes directly to a free masseur.
				numOfFreeMasseurs--;
				e.getPlayer().durationOfMassage=e.getDuration();
				e.getPlayer().exitTimeFromMassage=e.getArrivalTime()+e.getDuration();
				Event.eventSchedule(new Event("endOfMassage", e.getPlayer(), e.getArrivalTime()+e.getDuration(), 0.0));
			}
			else {
				e.getPlayer().entranceTimetoMassageQueue=e.getArrivalTime();
				e.getPlayer().durationOfMassage=e.getDuration();
				massageQueue.add(e.getPlayer());
				findMaxLenght(massageQueue);
				while(numOfFreeMasseurs!=0 && !massageQueue.isEmpty()) {     //assigning players in massage queue to free masseurs.
					Player p = massageQueue.poll();
					p.exitTimefromMassageQueue = e.getArrivalTime();
					p.playerTotalWaitingTimeInMassQue+=(p.exitTimefromMassageQueue-p.entranceTimetoMassageQueue);
					totWaitTimeMassQue+=(p.exitTimefromMassageQueue-p.entranceTimetoMassageQueue);
					if(playersHaving3MassageAttempt.containsKey(p.ID)) {
						playersHaving3MassageAttempt.put(p.ID, p);
					}
					p.exitTimeFromMassage = e.getArrivalTime()+p.durationOfMassage;
					Event.eventSchedule(new Event("endOfMassage", p, e.getArrivalTime()+p.durationOfMassage,0.0));
					numOfFreeMasseurs--;
				}
			}
		}
	}
	
	public void endMassage(Event e) {
		numOfFreeMasseurs++;
		totalMassageTime+=e.getPlayer().durationOfMassage;
		while(numOfFreeMasseurs!=0 && !massageQueue.isEmpty()) {    // assigning players in massage queue to free masseurs.
			Player p = massageQueue.poll();
			p.exitTimefromMassageQueue = e.getArrivalTime();
			p.playerTotalWaitingTimeInMassQue+=(p.exitTimefromMassageQueue-p.entranceTimetoMassageQueue);
			totWaitTimeMassQue+=(p.exitTimefromMassageQueue-p.entranceTimetoMassageQueue);
			if(playersHaving3MassageAttempt.containsKey(p.ID)) {
				playersHaving3MassageAttempt.put(p.ID, p);
			}
			p.exitTimeFromMassage = e.getArrivalTime()+p.durationOfMassage;
			Event.eventSchedule(new Event("endOfMassage", p, e.getArrivalTime()+p.durationOfMassage,0.0));
			numOfFreeMasseurs--;
		}
	}

	@Override
	public int compareTo(Player o) {
		if(this.skillLevel<o.skillLevel) {
			return 1;
		}
		else if(this.skillLevel>o.skillLevel) {
			return -1;
		}
		else {
			if(this.entranceTimetoMassageQueue<o.entranceTimetoMassageQueue) {
				return -1;
			}
			else if(this.entranceTimetoMassageQueue>o.entranceTimetoMassageQueue) {
				return 1;
			}
			else {
				if(this.ID<o.getID()) {
					return -1;
				}
				else if(this.ID>o.getID()) {
					return 1;
				}
				return 0;
			}
		}
	}
	
	/**
	 * finding maximum length of queues by comparing current queue length to max length.
	 */
	private void findMaxLenght(PriorityQueue<Player> q){   
		if(q.equals(trainingQueue)) {
			if(q.size()>maxLengTraQue) {
				maxLengTraQue = q.size();
			}
		}
		else if(q.equals(physiotherapyQueue)) {
			if(q.size()>maxLengPhysQue) {
				maxLengPhysQue = q.size();
			}
		}
		else if(q.equals(massageQueue)) {
			if(q.size()>maxLengMassQue) {
				maxLengMassQue = q.size();
			}
		}
	}
	
	/**
	 * finding player waited the most in physiotherapy queue and waiting time.
	 */
	private void findPlayerWaitedTheMostInPhysQue(Player p) {
		if(p.playerTotalWaitingTimeInPhysQue>maxWaitingTimeInPhysQue) {
			maxWaitingTimeInPhysQue=p.playerTotalWaitingTimeInPhysQue;
			PlayerIdWaitedTheMostInPhysQue=p.ID;
		}
		else if(p.playerTotalWaitingTimeInPhysQue==maxWaitingTimeInPhysQue) {
			if(p.ID<PlayerIdWaitedTheMostInPhysQue) {
				PlayerIdWaitedTheMostInPhysQue=p.ID;
			}
		}
	}
	
	/**
	 * finding the player waited the least in massage queue and waiting time by comparing players in playersHaving3MassageAttempt values.
	 * @return the player waited the least in massage queue and waiting time in string type
	 */
	public static String playerWaitedTheLeastInMassQue() {
		waitingTimeInMassQue.addAll(playersHaving3MassageAttempt.values());
		if(!waitingTimeInMassQue.isEmpty()) {
			return String.format(Locale.US,"%d %.3f\n", waitingTimeInMassQue.peek().ID, waitingTimeInMassQue.peek().playerTotalWaitingTimeInMassQue);
		}
		else {
			return "-1 -1\n";
		}
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param numOfTrainingCoaches the numOfTrainingCoaches to set
	 */
	public static void setNumOfTrainingCoaches(int numOfTrainingCoaches) {
		Player.numOfFreeTrainingCoaches = numOfTrainingCoaches;
	}

	/**
	 * @param numOfMasseurs the numOfMasseurs to set
	 */
	public static void setNumOfMasseurs(int numOfMasseurs) {
		Player.numOfFreeMasseurs = numOfMasseurs;
	}

	/**
	 * @return the maxLengTraQue
	 */
	public static int getMaxLengTraQue() {
		return maxLengTraQue;
	}

	/**
	 * @return the maxLengPhysQue
	 */
	public static int getMaxLengPhysQue() {
		return maxLengPhysQue;
	}

	/**
	 * @return the maxLengMassQue
	 */
	public static int getMaxLengMassQue() {
		return maxLengMassQue;
	}

	/**
	 * @return the totWaitTimeTraQue
	 */
	public static double getTotWaitTimeTraQue() {
		return totWaitTimeTraQue;
	}

	/**
	 * @return the totWaitTimePhysQue
	 */
	public static double getTotWaitTimePhysQue() {
		return totWaitTimePhysQue;
	}

	/**
	 * @return the totWaitTimeMassQue
	 */
	public static double getTotWaitTimeMassQue() {
		return totWaitTimeMassQue;
	}

	/**
	 * @return the totalTrainingTime
	 */
	public static double getTotalTrainingTime() {
		return totalTrainingTime;
	}

	/**
	 * @return the totalPhysiotherapyTime
	 */
	public static double getTotalPhysiotherapyTime() {
		return totalPhysiotherapyTime;
	}

	/**
	 * @return the totalMassageTime
	 */
	public static double getTotalMassageTime() {
		return totalMassageTime;
	}

	/**
	 * @return the invalidAttemptForMassageService
	 */
	public static int getInvalidAttemptForMassageService() {
		return invalidAttemptForMassageService;
	}

	/**
	 * @return the cancelledAttemptForTraAndMass
	 */
	public static int getCancelledAttemptForTraAndMass() {
		return cancelledAttemptForTraAndMass;
	}

	/**
	 * @return the maxWaitingTimeInPhysQue
	 */
	public static double getMaxWaitingTimeInPhysQue() {
		return maxWaitingTimeInPhysQue;
	}

	/**
	 * @return the playerIdWaitedTheMostInPhysQue
	 */
	public static int getPlayerIdWaitedTheMostInPhysQue() {
		return PlayerIdWaitedTheMostInPhysQue;
	}

	/**
	 * @return the totalTurnaroundTimes
	 */
	public static double getTotalTurnaroundTimes() {
		return totalTurnaroundTimes;
	}

	/**
	 * @return the numOfTurnaround
	 */
	public static int getNumOfTurnaround() {
		return numOfTurnaround;
	}

	/**
	 * @return the numOfTraining
	 */
	public static int getTotalNumOfTraining() {
		return totalNumOfTraining;
	}

	/**
	 * @return the totalNumOfValidMassage
	 */
	public static int getTotalNumOfValidMassage() {
		return totalNumOfValidMassage;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * @return the entranceTimetoPhysiotherapyQueue
	 */
	public double getEntranceTimetoPhysiotherapyQueue() {
		return entranceTimetoPhysiotherapyQueue;
	}

	/**
	 * @return the trainingTime
	 */
	public double getTrainingTime() {
		return trainingTime;
	}

	/**
	 * @return the playerTotalWaitingTimeInMassQue
	 */
	public double getPlayerTotalWaitingTimeInMassQue() {
		return playerTotalWaitingTimeInMassQue;
	}

	/**
	 * @return the entranceTimetoTrainingQueue
	 */
	public double getEntranceTimetoTrainingQueue() {
		return entranceTimetoTrainingQueue;
	}
	
}
