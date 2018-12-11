
public class Main {

	public static void main(String[] args) {
		
		String filename = args[0];
		
		SMTTP smttp = new SMTTP(filename);
		
		int[] processing_times = smttp.get_processing_times();
		int[] due_dates = smttp.get_due_dates();
		
		for (int i = 0; i < processing_times.length; i++) {
			System.out.println("Processing time: " + processing_times[i] + " Due date: " + due_dates[i]);
		}

	}

}
