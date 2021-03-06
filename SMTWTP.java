/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 12/15/2018
 * 
 * This class defines a SMTWTP object. The object contains all of the necessary 
 * information for a SMTWTP problem including the number of jobs, arrays for the
 * processing times, due dates, and weights of the jobs, and 2d arrays for the 
 * pheromone matrix and numerator values for EAS stochastic equation.
 * 
 * This file parses a given SMTWTP problem.
 * 
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class SMTWTP {
	
	int num_jobs;
	
	int[] processing_times;
	int[] due_dates;
	int[] weights;
	
	double[][] smtwtp_pheromone;
	double[][] smtwtp_value;
	
	JOB[] jobs;
	
	public static final int PROCESSING_TIME = 0;
	public static final int DUE_DATE = 1;
	public static final int WEIGHT = 2;
	public static final int DONE = 3;
	
	public SMTWTP(String filename) {
		
		File smtwtpFile = new File(filename);
		
		try {
			String[] line;
			
			int counter = 0;
			Scanner sc = new Scanner(smtwtpFile);
			
			while(counter < DONE) {
				
				line = sc.nextLine().trim().split("\\s+");
				
				//first section is processing times
				if (counter == PROCESSING_TIME) {
					
					this.num_jobs = line.length;
					this.processing_times = new int[num_jobs];
					this.due_dates = new int[num_jobs];
					this.weights = new int[num_jobs];
					
					for (int i = 0; i < num_jobs; i++) {
						processing_times[i] = Integer.parseInt(line[i]);
					}
				}
				
				//second section is due dates
				else if (counter == DUE_DATE) {
					
					for (int i = 0; i < num_jobs; i++) {
						int temp = Integer.parseInt(line[i]);
						due_dates[i] = temp;
					}
				}
				
				//third section is weights
				else if (counter == WEIGHT) {
					
					for (int i = 0; i < num_jobs; i++) {
						weights[i] = Integer.parseInt(line[i]);
					}
				}
				
				counter++;
			}
			
			sc.close();
			
			jobs = new JOB[num_jobs];
			
			for (int i = 0; i < num_jobs; i++) {
				jobs[i] = new JOB(i, processing_times[i], due_dates[i], weights[i]);
			}
			
			this.smtwtp_pheromone = new double[num_jobs][num_jobs];
			this.smtwtp_value = new double[num_jobs][num_jobs];
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found, please verify input and try again");
		}
		
	}
	
	/* Purpose: Initialize the pheromone array to base tau
	 * Parameters: A double value of the base pheromone
	 * Return: None
	 */
	public void initializePheromone(double base_tau) {
		
		for(int i = 0; i < num_jobs; i++) {
			for(int j = 0; j < i; j++) {
				smtwtp_pheromone[i][j] = base_tau;
			}
		}
	}
	
	/* Purpose: Increase the pheromone level on a certain path
	 * Parameters: An integer value of job 1, an integer value
	 * of job 2, and a double value of the desired pheromone increase
	 * Return: None
	 */
	public void increasePheromone(int i, int j, double pheromone) {
		
		smtwtp_pheromone[i][j] += pheromone;
	}
	
	/* Purpose: Evaporate the pheromone level on a certain path
	 * Parameters: An integer value of job 1, an integer value
	 * of job 2, and a double value of the evaporation factor
	 * Return: None
	 */
	public void evaporatePheromone(int i, int j, double rho) {
		
		smtwtp_pheromone[i][j] *= 1.0 - rho;
	}
	
	/* Purpose: Calculate the numerator of the probability of
	 * selecting a given path based on its pheromone levels
	 * Parameters: An double value of alpha and a double value
	 * of beta
	 * Return: None
	 */
	public void calculateValue(double alpha, double beta) {
		double heuristic, pheromone;
		
		for(int i = 0; i < num_jobs; i++) {
			for(int j = 0; j < i; j++) {
				pheromone = smtwtp_pheromone[i][j];
				if (due_dates[i] == 0) {
					heuristic = 1;
				} else {
					heuristic = 1 / (double) due_dates[i];
				}
				smtwtp_value[i][j] = Math.pow(pheromone, alpha) * Math.pow(heuristic, beta);
			}
		}
	}

	public int getNum_jobs() {
		return num_jobs;
	}

	public void setNum_jobs(int num_jobs) {
		this.num_jobs = num_jobs;
	}

	public int[] getProcessing_times() {
		return processing_times;
	}

	public void setProcessing_times(int[] processing_times) {
		this.processing_times = processing_times.clone();
	}

	public int[] getDue_dates() {
		return due_dates;
	}

	public void setDue_dates(int[] due_dates) {
		this.due_dates = due_dates.clone();
	}

	public JOB[] getJobs() {
		return jobs;
	}

	public void setJobs(JOB[] jobs) {
		this.jobs = jobs.clone();
	}

	public double[][] getSmtwtp_pheromone() {
		return smtwtp_pheromone;
	}

	public void setSmtwtp_pheromone(double[][] smttp_pheromone) {
		this.smtwtp_pheromone = smttp_pheromone.clone();
	}

	public double[][] getSmtwtp_value() {
		return smtwtp_value;
	}

	public void setSmtwtp_value(double[][] smttp_value) {
		this.smtwtp_value = smttp_value.clone();
	}
	

}
