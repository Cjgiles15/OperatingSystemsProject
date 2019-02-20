import java.util.Queue;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class myProcessManager {

	public enum TaskState{PS,FCFS,SJF,RR};
	TaskState State;
	public Queue<String> TaskQueue;
	public String folderLocation;
	HashSet<String> lookup;
	int RR_timer;
	ArrayList<Tuple> txtFiles;
	
	public myProcessManager(){
		TaskQueue = new LinkedList<String>();
		
	}

	
	public boolean setFolderLocation(String location){
            
	folderLocation = location + "/";
		
        txtFiles = new ArrayList<Tuple>();
        String path = folderLocation + "config.txt";
        boolean firstLine = false;
        File f = new File(path);

        Scanner sc = null;
        try { // scan line by line
            sc = new Scanner(f);
        } catch (FileNotFoundException e) { // throw exception if nothing is there
            System.out.println("Check file path, file does not exist or config file not found");
            return false;
        }
        while (sc.hasNext()) { // add each number into the list "read"
            String[] temp = null;
            if (firstLine == false) { // this collects the the first line of the config file, a one time thing
                RR_timer = Integer.parseInt(sc.nextLine().replaceAll("\\s+","")); // this is the runtime for round robin
                firstLine = true; // this closes the first condition
            } else {
                String s = sc.nextLine();
                temp = s.split(",");
                int second = Integer.parseInt(temp[1].replaceAll("\\s+",""));
                Tuple t = new Tuple(temp[0], second);
                txtFiles.add(t);
               // System.out.printf("%s, %d \n", temp[0], second);
            }

        }

		//For tuples
		//RR will only read the first vaule
		//FCFS will only read the first
		//Priority needs both values
		//SJF will rewrite the priority values
                
                return true;
	}
	
	

	public void run(){
		String algID = "";
		if(State == TaskState.FCFS){
			algID = "_2";
		}
		if(State == TaskState.SJF){
			algID = "_3";
		}
		if(State == TaskState.PS){
			algID = "_1";
		}
		
		if(State == TaskState.RR){
			//System.out.println("Round Robin Timer " + RR_timer);
			algID = "_4";
			
			lookup = new HashSet<String>();
			while(!TaskQueue.isEmpty()){
				myProcess task;
				String txtFile = TaskQueue.poll();
				if(lookup.contains(txtFile)){
					//System.out.println("looking up " + txtFile + ".txt");
					task = readObject(folderLocation + txtFile + "_save.txt");
				}
				else{
					String path = folderLocation + txtFile;
					task = new myProcess(path);
					task.Taskid = txtFile;
					task.alg_id = algID;
				}
				
				Thread t1 = new Thread(task, "T1");
				System.out.println(task.Taskid + " just started running");
				t1.start();
				StopWatch(RR_timer);
				System.out.println(task.Taskid + " just completed running");
				task.stop();
				
				while(task.isRunning()){
					//stops unpredictability
					//sometimes if loop below is checked before the task has had time to stop
				}
				
				
				if(!task.isFinished()){
					//put the task back in the task pool
					//System.out.println("Adding to look up " + task.Taskid);
					String save = folderLocation +task.Taskid + "_save.txt";
					writeObject(task,save);
					lookup.add(task.Taskid);
					//in main implementation this will be the path
					TaskQueue.add(task.Taskid);
					System.out.println(task.Taskid + " is descheduled");
					continue;
				}
				//if the task is finished and has a save file associated with it
				else if (task.isFinished() && lookup.contains(task.Taskid)){
					//remove from look up and delete the textfile
					//System.out.println("Removing task from lookup " + task.Taskid);
					lookup.remove(task.Taskid);
					File file = new File(folderLocation +task.Taskid + "_save.txt");
					file.delete();
					
					
				}
				
				
				
			}
		}
		
		else{
			while(!TaskQueue.isEmpty()){
				String path = TaskQueue.poll();
				String fullPath = folderLocation + path;
				myProcess task = new myProcess(fullPath);
				task.Taskid = path;
				task.alg_id = algID;
				System.out.println(task.Taskid + " just started running");
				task.run();
				System.out.println(task.Taskid + " just completed running");
				
				
			}
		}
		 
		 
	}
	
	
	public void orderTaskByRoundRobin(){

		//adds textfile names to the taskQueue
		for(int i = 0; i < txtFiles.size(); i++){
			TaskQueue.add(txtFiles.get(i).taskid);
		}

		State = TaskState.RR;

		
		
	}
	public void orderTaskByFCFS(){
		//same as roundRobin
		for(int i = 0; i < txtFiles.size(); i++){
			TaskQueue.add(txtFiles.get(i).taskid);
		}

		
		State = TaskState.FCFS;
	}
	
	public void orderTaskBySJF(){

		ArrayList<Tuple> copy = new ArrayList<Tuple>();
		for(int i = 0; i < txtFiles.size(); i++){
			copy.add(txtFiles.get(i));
		}
		
		for(int i = 0; i < copy.size(); i++){
			String path = folderLocation + copy.get(i).taskid + ".txt";
			File f = new File(path);
			copy.get(i).priority = (int)f.length();
		}
		
		bubbleSortTupleL2G(copy);
		
		
		for(int i = 0; i < copy.size(); i++){
			TaskQueue.add(copy.get(i).taskid);
		}
		

		State = TaskState.SJF;
		
	}
	
	public void orderTaskByPriority(){
		//look at tuple array
		//make a copy (ArrayList<Tuple> Copy)
		//sort copy using the priorities
		//add to taskqueue
		//deallocate memory for copy
		ArrayList<Tuple> copy = new ArrayList<Tuple>();
		for(int i = 0; i < txtFiles.size(); i++){
			copy.add(txtFiles.get(i));
		}
		
		bubbleSortTupleG2L(copy);
		

		
		for(int i = 0; i < copy.size(); i++){
			TaskQueue.add(copy.get(i).taskid);
		}
		
		State = TaskState.PS;
		
	}
	
	
	
	private void StopWatch(int ms){
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		long timer = 0;
		while(true){
			endTime = System.currentTimeMillis();
			timer = endTime - startTime;
			if(timer >= ms){
				return;
			}
		}
	}
	
	
	private void writeObject(myProcess obj, String path){
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(obj);
			objectOut.close();
			fileOut.close();
			System.out.println("Successful write");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}
	
	private myProcess readObject(String path){
		myProcess a = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			a = (myProcess) objectIn.readObject();
			objectIn.close();
			fileIn.close();
			//System.out.println("Successful read");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return a;
	}

	//greatest to least
	private void bubbleSortTupleG2L(ArrayList<Tuple> ar) {
	   for (int i = (ar.size() - 1); i >= 0; i--){
		   
	      for (int j = 1; j <= i; j++){
	    	  
	    	  //
	         if (ar.get(j-1).priority == ar.get(j).priority){
	        	 if(ar.get(j-1).taskid.compareTo(ar.get(j).taskid) > 0 ){
	        		 
	        		 Tuple temp = ar.get(j-1);
	        		 ar.set(j-1, ar.get(j));
	        		 ar.set(j, temp);
	        	 }
	         } 
	         else if (ar.get(j-1).priority < ar.get(j).priority){
	        	 Tuple temp = ar.get(j-1);
        		 ar.set(j-1, ar.get(j));
        		 ar.set(j, temp);
	         	}
	         }
	      } 
	  }
	
	//Least to greatest
	private void bubbleSortTupleL2G(ArrayList<Tuple> ar) {
		   for (int i = (ar.size() - 1); i >= 0; i--){
			   
		      for (int j = 1; j <= i; j++){
		    	  
		    	  //
		         if (ar.get(j-1).priority == ar.get(j).priority){
		        	 if(ar.get(j-1).taskid.compareTo(ar.get(j).taskid) > 0 ){
		        		 
		        		 Tuple temp = ar.get(j-1);
		        		 ar.set(j-1, ar.get(j));
		        		 ar.set(j, temp);
		        	 }
		         } 
		         else if (ar.get(j-1).priority > ar.get(j).priority){
		        	 Tuple temp = ar.get(j-1);
	        		 ar.set(j-1, ar.get(j));
	        		 ar.set(j, temp);
		         	}
		         }
		      } 
		  }

	
}
