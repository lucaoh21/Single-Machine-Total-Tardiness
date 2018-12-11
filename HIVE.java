/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 11/7/2018
 * 
 * This class defines a HIVE object for use in Ant Colony Optimization on the TSP.
 * A HIVE object contains all of the ANTs for use in the algorithms, stored as an
 * array of ANTs, the integer number of cities in the TSP problem instance, the
 * integer number of ants for use in the algorithm instance, the score of the best
 * tour found so far by any ants, stored as a double, and the best tour found so
 * far by any of the ants, stored as an array of integers. 
 * 
 */

public class HIVE {
	
	ANT[] hive;
	int num_jobs;
	int num_ants;
	double best_score_so_far;
	int[] best_workflow_so_far;
	
	/* Purpose: Initialize a HIVE object
	 * Parameters: An integer number of ants, an integer number of cities,
	 * and the TSP problem instance
	 * Return: N/A
	 */
	public HIVE(int num_ants, int num_jobs, SMTTP smttp) {
		
		this.num_ants = num_ants;
		this.num_jobs = num_jobs;
		this.hive = new ANT[num_ants];
		this.best_score_so_far = Double.MAX_VALUE;
		this.best_workflow_so_far = new int[num_jobs];
		
		for (int i = 0; i < num_ants; i++) {
			hive[i] = new ANT(num_jobs, smttp);
		}
		
	}

	
	/* Purpose: Find the best tour of all the ANTs in the HIVE
	 * Parameters: None
	 * Return: boolean indicating whether a new best tour was found
	 */
	public boolean findBest() {
		
		boolean is_new_best = false;
		for (int i = 0; i < num_ants; i++) {
			if (hive[i].getWorkflow_score() < best_score_so_far) {
				this.best_score_so_far = hive[i].getWorkflow_score();
				this.best_workflow_so_far = hive[i].getWorkflow().clone();
				is_new_best = true;
			}
		}	
		return is_new_best;
	}
	
	/*
	 * Getters and Setters
	 */

	public ANT[] getHive() {
		return hive;
	}

	public void setHive(ANT[] hive) {
		this.hive = hive.clone();
	}

	public int getNum_jobs() {
		return num_jobs;
	}


	public void setNum_jobs(int num_jobs) {
		this.num_jobs = num_jobs;
	}

	public int getNum_ants() {
		return num_ants;
	}

	public void setNum_ants(int num_ants) {
		this.num_ants = num_ants;
	}

	public double getBest_score_so_far() {
		return best_score_so_far;
	}

	public void setBest_score_so_far(double best_score_so_far) {
		this.best_score_so_far = best_score_so_far;
	}

	public int[] getBest_workflow_so_far() {
		return best_workflow_so_far;
	}

	public void setBest_workflow_so_far(int[] best_workflow_so_far) {
		this.best_workflow_so_far = best_workflow_so_far.clone();
	}

}
