
public class Individual {
	
	private String assignments = "";
	
	public Individual() {
		assignments = "";
	}
	
	public Individual(String assignments) {
		this.assignments = assignments;
	}
	
	public Individual(int numVariables) {
		
		for (int i = 0; i < numVariables; ++i) {
			if (Math.random() < 0.5) {
				assignments += "1";
			}
			else {
				assignments += "0";
			}
		}
	}
	
	public String GetAssignments() {
		return assignments;
	}
	
	public char GetVariable(int index) {
		return assignments.charAt(index);
	}
	
	public void SetAssignments(String assignments) {
		this.assignments = assignments;
	}

}
