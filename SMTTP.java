import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class SMTTP {
	
	int num_jobs;
	
	int[] processing_times;
	int[] due_dates;
	
	public static final int PROCESSING_TIME = 0;
	public static final int DUE_DATE = 1;
	public static final int DONE = 2;
	
	public SMTTP(String filename) {
		
		
		File smttpFile = new File(filename);
		
		try {
			String[] line;
			
			int counter = 0;
			Scanner sc = new Scanner(smttpFile);
			
			while(counter < DONE) {
				
				line = sc.nextLine().trim().split("\\s+");
				
				if (counter == PROCESSING_TIME) {
					
					this.num_jobs = line.length;
					this.processing_times = new int[num_jobs];
					this.due_dates = new int[num_jobs];
					
					for (int i = 0; i < num_jobs; i++) {
						processing_times[i] = Integer.parseInt(line[i]);
					}
				}
				
				else if (counter == DUE_DATE) {
					
					for (int i = 0; i < num_jobs; i++) {
						due_dates[i] = Integer.parseInt(line[i]);
					}
				}
				
				counter++;
			}
			
			sc.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found, please verify input and try again");
		}
		
	}
	
	public int[] get_processing_times() {
		return this.processing_times;
	}
	
	public int[] get_due_dates() {
		return this.due_dates;
	}
	

}
