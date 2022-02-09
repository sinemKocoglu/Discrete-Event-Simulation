import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class project2main {

	public static void main(String[] args) throws FileNotFoundException {
		
		int numOfPlayers = 0;
		int numOfArrivals = 0;
		int numOfPhysiotherapists = 0;
		
		try {
			Scanner in = new Scanner(new File(args[0]));
			if(in.hasNextLine()) {
				numOfPlayers = Integer.parseInt(in.nextLine());
			}
			Player[] players = new Player[numOfPlayers];
			for(int i=0; i<numOfPlayers;i++) {
				String[] pline=in.nextLine().split(" ");
				players[i] = new Player(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]));
			}
			
			if(in.hasNextLine()) {
				numOfArrivals = Integer.parseInt(in.nextLine());
			}
			for(int j=0;j<numOfArrivals;j++) {
				String[] line = in.nextLine().split(" ");
				Event.eventSchedule(new Event(line[0], players[Integer.parseInt(line[1])], Double.parseDouble(line[2]), Double.parseDouble(line[3])));
			}
			
			if(in.hasNextLine()) {
				String[] line = in.nextLine().split(" ");
				numOfPhysiotherapists = Integer.parseInt(line[0]);
				for(int k=0;k<numOfPhysiotherapists;k++) {
					Physiotherapist.freePtherapists.add(new Physiotherapist(k, Float.parseFloat(line[k+1])));
				}
			}
			String[] line=in.nextLine().split(" ");
			Player.setNumOfTrainingCoaches(Integer.parseInt(line[0]));
			Player.setNumOfMasseurs(Integer.parseInt(line[1]));
			
			in.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Catch - An error occurred.");
			e.printStackTrace();
		}
		
		Event.simulate();
		
		File myOutputFile = new File(args[1]);
		try {
		      myOutputFile.createNewFile();
		      FileWriter writer = new FileWriter(args[1]);
		
		      writer.write(Player.getMaxLengTraQue() + "\n" + Player.getMaxLengPhysQue() + "\n" + Player.getMaxLengMassQue() + "\n");      
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotWaitTimeTraQue()/Player.getTotalNumOfTraining()));  
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotWaitTimePhysQue()/Player.getTotalNumOfTraining()));     
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotWaitTimeMassQue()/Player.getTotalNumOfValidMassage()));     
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotalTrainingTime()/Player.getTotalNumOfTraining()));     
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotalPhysiotherapyTime()/Player.getTotalNumOfTraining()));   
		      writer.write(String.format(Locale.US,"%.3f\n", Player.getTotalMassageTime()/Player.getTotalNumOfValidMassage()));       
		      writer.write(String.format(Locale.US,"%.3f\n",Player.getTotalTurnaroundTimes()/Player.getNumOfTurnaround()));      
		      writer.write(Player.getPlayerIdWaitedTheMostInPhysQue() + " " + String.format(Locale.US,"%.3f\n", Player.getMaxWaitingTimeInPhysQue())); 
		      writer.write(Player.playerWaitedTheLeastInMassQue());   
		      writer.write(Player.getInvalidAttemptForMassageService() + "\n");  
		      writer.write(Player.getCancelledAttemptForTraAndMass() + "\n");    
		      writer.write(String.format(Locale.US,"%.3f\n", Event.getTime())); 
		      
		     writer.close();
		      
	    } catch (IOException e) {
	      System.out.println("Catch - An error occurred.");
	      e.printStackTrace();
	    }

	}

}
