import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class myProcess implements Runnable, Serializable {

	
	public String taskPath;
	String alg_id;
	public String Taskid;
	int i;
	int j;
	int[] ary;
	private boolean canRun;
	private boolean finished;
	ArrayList<Integer> fileContents;
	/*
	 * public myProcess(String s){ taskPath = s; }
	 */

	public myProcess(String path) {
		taskPath = path;
		// creates initial processes necessary to the proccess
		readtxt();

	}

	public void run() {

		// Will take the text file name, open it, and sort it, write to new file
		// path + newfileName

		canRun = true;
		finished = false;
		int j = 0;
		while (canRun) {
			if (i > 1) {
				if (j < i) {
					int a = ary[j];
					int b = ary[j + 1];
					if (a > b) {
						int tmp = a;
						ary[j] = b;
						ary[j + 1] = tmp;
					}
					j++;
				}
				else{
					j = 0;
					i--;
				}

			} else {
				finished = true;
				//System.out.println("Task completed");
				WriteToFile();
				break;
			}

		}
		//System.out.println("Stopped");
	}

	private void readtxt() {
		// later to be used as a parameter
		File f = new File(taskPath + ".txt"); // create file with the text file

		fileContents = new ArrayList<Integer>(); // initiate a list called
													// "read"
		Scanner sc = null; // create scanner
		try { // scan line by line
			sc = new Scanner(f);
		} catch (FileNotFoundException e) { // throw exception if nothing is
											// there
			System.out.print("Check file path, file does not exist");
		}

		while (sc.hasNextInt()) { // add each number into the list "read"
			fileContents.add(sc.nextInt());
		}

		createArray();

	}

	private void createArray() {
		ListIterator<Integer> iter = fileContents.listIterator(); // iterates
																	// through
																	// the list
		ary = new int[fileContents.size()]; // created an array for our list
		int j = 0;
		while (iter.hasNext()) { // dump what's in the list into the array
			ary[j] = iter.next();
			j++;
		}
		i = ary.length - 1;
	}

	private void WriteToFile() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(taskPath + alg_id + ".txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Writer wr = new FileWriter(writer);// grabs the file and makes an
		// instance of it
		for (int k = 0; k < ary.length; k++) { // display the array
			//System.out.println(ary[k]);
			writer.write(ary[k] + ",\n\r");// writes the sorted number onto the
										// file
		}
		writer.close();
	}

	public void stop() {
		canRun = false;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isRunning() {
		return canRun;
	}
}
