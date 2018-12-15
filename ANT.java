/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 11/7/2018
 * 
 * This class defines an ANT object for use in Ant Colony Optimization on the SMTWTP.
 * Each ANT object can hold workflow, represented as an array of integers,
 * a score of that workflow of jobs, represented as a double, an integer number of
 * jobs in the problem instance, and an array of the jobs, represented as an array of jobs.
 * 
 */

public class ANT {
	
	int[] workflow;
	int workflow_score;
	
	int num_jobs;
	JOB[] jobs;
	
	/* Purpose: Initialize an ANT object
	 * Parameters: The number of jobs and a given SMTWTP object.
	 * Return: N/A
	 */
	public ANT(int num_jobs, SMTWTP smtwtp) {
		
		this.num_jobs = num_jobs;
		this.jobs = smtwtp.getJobs();
		
		this.workflow = new int[num_jobs];
		this.workflow_score = Integer.MAX_VALUE;
	}
	
	/* Purpose: Score the ANT's workflow
	 * Parameters: None
	 * Return: None
	 */
	public void scoreWorkflow() {
		
		int score = 0;
		int time_so_far = 0;
		
		int curr_job;
		int finish_difference;
		
		for (int i = 0; i < num_jobs; i++) {
			
			curr_job = workflow[i];
			
			finish_difference = (time_so_far + jobs[curr_job].getProcessing_time()) - jobs[curr_job].getDue_date();
			
			if (finish_difference > 0) {
				score += (finish_difference * jobs[curr_job].getWeight());
			}
			
			time_so_far += jobs[curr_job].getProcessing_time();
		}
		this.workflow_score = score;
	}
	
	/*
	 * Getters and Setters
	 */

	public int[] getWorkflow() {
		return workflow;
	}

	public void setWorkflow(int[] workflow) {
		this.workflow = workflow.clone();
	}

	public int getWorkflow_score() {
		return workflow_score;
	}

	public void setWorkflow_score(int workflow_score) {
		this.workflow_score = workflow_score;
	}

	public int getNum_jobs() {
		return num_jobs;
	}

	public void setNum_jobs(int num_jobs) {
		this.num_jobs = num_jobs;
	}

}
