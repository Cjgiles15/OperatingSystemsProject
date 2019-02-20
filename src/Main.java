import java.util.Scanner;

public class Main {
	
	//NOTES
	//Shortest job First goes by Byte size rather than text file length


	public static void main(String[] args) {
		Scanner scnr = new Scanner(System.in);
		
		String folderLocation = "";
                boolean folderSet = false;
                
		myProcessManager pm = new myProcessManager();
                
                System.out.println("Enter folder location: ");
                folderLocation = scnr.nextLine();
                
		
                while(folderSet == false){
                    
                    folderSet = pm.setFolderLocation(folderLocation);
                    
                    if(folderSet == false){
                        System.out.println("Enter folder location: ");
                        folderLocation = scnr.nextLine();
                    }
                    
                }
		
		
		System.out.println("=======Priority========");
		pm.orderTaskByPriority();
		pm.run();
		System.out.println("=======FCFS=======");
		pm.orderTaskByFCFS();
		pm.run();
		System.out.println("========SJF=======");
		pm.orderTaskBySJF();
		pm.run();
		System.out.println("=======Round Robin=======");
		pm.orderTaskByRoundRobin();
		pm.run();
		
		System.out.println("All tasks have been completed");


	}

}
