/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 12/16/2018
 * 
 * This class defines a JOB object. The object contains a job number, processing
 * time, due date, and weight for that job. 
 * 
 */

public class JOB {
	
	int job_number;
	int processing_time;
	int due_date;
	int weight;
	
	public JOB(int job_number, int processing_time, int due_date, int weight) {
		this.job_number = job_number;
		this.processing_time = processing_time;
		this.due_date = due_date;
		this.weight = weight;
	}

	public int getJob_number() {
		return job_number;
	}

	//sets the job number of the job
	public void setJob_number(int job_number) {
		this.job_number = job_number;
	}

	public int getProcessing_time() {
		return processing_time;
	}

	public void setProcessing_time(int processing_time) {
		this.processing_time = processing_time;
	}

	public int getDue_date() {
		return due_date;
	}

	public void setDue_date(int due_date) {
		this.due_date = due_date;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
