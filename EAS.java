/*
 * Author Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date 11/7/2018
 * 
 * This class defines the Elitist Ant System algorithm. Called by RUNACO and given
 * the user inputed values, it runs the EAS algorithm for the specified number of
 * iterations or until a percentage of the optimal value is met. The EAS algorithm
 * (with m ants) iteratively builds m tours using the probability
 * selection rule. The probability rule randomly chooses a path based on 
 * probabilities assigned by a path's pheromone and heuristic. After all the tours are
 * constructed in an iteration, the pheromone levels are updated. Pheromone is 
 * deposited on each leg in ants tour. Further, if that leg is part of the best tour
 * so far, additional pheromone is deposited. A global pheromone rule evaporates
 * pheromone on each leg in the environment depending on the user inputed value for rho.
 * The class contains a base-pheromone equation from the ACO handout, that is
 * used to determine the base-tau for the environment.
 * 
 */

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EAS {
	
	int num_ants;
	int max_iterations;
	int num_jobs;
	double alpha;
	double beta;
	double rho;
	double elitism_factor;
	int optimal;
	double stop_percent;
	
	SMTWTP smtwtp;
	HIVE hive;
	Set<Integer> transitions_in_best_workflow;
	
	JOB[] jobs;
	
	public static final double NANO_TO_SEC = 1000000000;
	public static final int PRINT_ON_ITERATION = 20;
	public static final int STOP_TIME = Integer.MAX_VALUE;
	
	public EAS(int num_ants, int max_iterations, double alpha, double beta, double rho,
			double elitism_factor, int optimal, double stop_percent, SMTWTP smtwtp) {
		
		this.num_ants = num_ants;
		this.max_iterations = max_iterations;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitism_factor = elitism_factor;
		this.optimal = optimal;
		this.stop_percent = stop_percent;
		this.smtwtp = smtwtp;
		this.transitions_in_best_workflow = new HashSet<Integer>();
	}
	
	/*
	 * Purpose: The main algorithm of the EAS algorithm. It iteratively build tours, finds
	 * 	the best tour, and updates pheromone levels. The algorithm stops iterating when 
	 * 	a specified number of iterations is met or if the algorithm has found a 
	 * 	solution within the specified optimal percentage.
	 * Parameters: none
	 * Return: none, prints the best so far in each iteration
	 * 
	 */
	public int[] runEAS() {
		
		double startTime = System.nanoTime();
		
		num_jobs = smtwtp.getNum_jobs();
		jobs = smtwtp.getJobs();
		smtwtp.initializePheromone(setBasePheromone());
		
		//creates a new hive object
		hive = new HIVE(num_ants, smtwtp.getNum_jobs(), smtwtp);
		
		int num_iteration = 0;
		double bsf_percent = Double.MAX_VALUE;
		
		//iterates until max iterations or specified percentage is met
		while(num_iteration < max_iterations && bsf_percent > stop_percent) {
			
			//recalculate the numerator of the prob selection rule
			smtwtp.calculateValue(alpha, beta, 0);
			//construct the tours
			construct();
			//checks if there is new best
			if(hive.findBest()) {
				//if there is a new best, the paths in best_so_far set are updated
				updateTransitionSet();
			}
			
			//perform evaporation and depositing of pheromone
			evaporatePheromone();
			depositPheromone();
			
			bsf_percent = (hive.getBest_score_so_far() / (double) optimal) - 1.0;

			// System.out.println("in iteration: " + num_iteration + ", best score is " + hive.getBest_score_so_far());

			num_iteration++;
		}
		
		/*
		//print finish message
		if(bsf_percent <= stop_percent) {
			System.out.println("The algorithm found a tour of " + hive.getBest_score_so_far() + 
					" with a percent over the optimal of " + bsf_percent + 
					", which is within the specified percentage of " + stop_percent);
		} else {
			System.out.println("Max iterations reached.");
		}
		
		double endTime = System.nanoTime();
		double duration = (endTime - startTime) / NANO_TO_SEC;
		
		System.out.println("Time duration is: " + duration);
		*/
		
		return hive.getBest_workflow_so_far();

	}
	
	/*
	 * Purpose: To update the set of legs of the best tour so far. This set contains
	 * 	a hashkey (int) that represents a path from i to j. The values of the two cities
	 * 	are combined using the Cantor function, so one value is produced and can be
	 * 	added to the set. This is helpful when we later want to determine if a specific
	 * 	leg is in the bsf tour.
	 * Parameters: none
	 * Return: none, updates the set containing hashkeys for paths in bsf tour
	 * 
	 */
	public void updateTransitionSet() {
		
		//removes the old hashkeys
		transitions_in_best_workflow.clear();

		int job1, job2;
		
		//for each job in the bsf workflow
		for(int i = 0; i < num_jobs-1; i++) {
			
			//sort the jobs in the workflow
			if(hive.getBest_workflow_so_far()[i] < hive.getBest_workflow_so_far()[i+1]) {
				job1 = hive.getBest_workflow_so_far()[i];
				job2 = hive.getBest_workflow_so_far()[i+1];
			} else {
				job1 = hive.getBest_workflow_so_far()[i+1];
				job2 = hive.getBest_workflow_so_far()[i];
			}
		
			//create hashkey using Cantor Pairing Function
			transitions_in_best_workflow.add((((job1 + job2)*(job1 + job2 + 1)) / 2) + job2);
			
		}
	}
	
	/*
	 * Purpose: To determine the base tau for the paths in the problem, useing
	 *  the equation from the handout to calculate the base tau.
	 * Parameters: none
	 * Return: General base tau (double) for the problem.
	 */
	public double setBasePheromone() {
		
		Set<Integer> unperformed_jobs = new HashSet<Integer>();
		//create set of jobs to be performed
		for(int j = 0; j < num_jobs; j++) {
			unperformed_jobs.add(j);
		}
		
		Random r = new Random();
		
		//choose random starting job
		int curr_job = r.nextInt(num_jobs);
		unperformed_jobs.remove(curr_job);
		
		int best_next_job = curr_job;
		double total_greedy_time = 0;
		double time, best_next_time;
		
		//create a greedy workflow from the random starting location
		for(int i = 1; i < num_jobs; i++) {
			best_next_time = Double.MAX_VALUE;
			
			//only uses cities that are unvisited
			for(int job : unperformed_jobs) {
				
				// greedily select for shorter times and larger weights
				time = smtwtp.getProcessing_times()[job] * (1 / smtwtp.getJobs()[job].getWeight());
				if(time < best_next_time) {
					best_next_time = time;
					best_next_job = job;
				}
			} 
			
			//add the closest city, and repeat the loop
			total_greedy_time += best_next_time;
			unperformed_jobs.remove(best_next_job);
			curr_job = best_next_job;	
		}
		
		//equation for base tau from ant variations handout
		return (elitism_factor + num_ants)/(rho * total_greedy_time);
	}
	
	/*
	 * Purpose: Constructs a tour for each ant in the hive. Performs probabilistic 
	 * selection. It also calls scoreTour() to score the tour of each ant. 
	 * Parameters: none
	 * Return: none, sets the tour for each ant in the hive
	 * 
	 */
	public void construct() {		
		
		//construct a tour for each ant in the hive
		for (int i = 0; i < num_ants; i++) {
			
			//using the probabilistic selection technique
			hive.getHive()[i].setWorkflow(probSelection());
			
			//scores the ants tour
			hive.getHive()[i].scoreWorkflow();

		}

	}
	
	/*
	 * Purpose: Constructs a tour for an ant using the probabilistic selection rule
	 * 	for the general Ant System algorithm. Each leg is assigned a probability based
	 * 	on the pheromone level of the leg and the heuristic info. The leg is then chosen
	 * 	randomly based on these probabilities. Finally, it calls the local pheromone
	 * 	update rule on the constructed tour.
	 * Parameters: none
	 * Return: Returns a completed tour (int[])
	 * 
	 */
	public int[] probSelection() {
		
		int time_elapsed = 0;
		
		//initialize set with all cities
		Set<Integer> unperformed_jobs = new HashSet<Integer>();
		for(int i = 0; i < num_jobs; i++) {
			unperformed_jobs.add(i);
		}
		
		int[] workflow = new int[num_jobs];

		Random r = new Random();
		
		//choose random starting job
		int curr_job = r.nextInt(num_jobs);
		unperformed_jobs.remove(curr_job);
		workflow[0] = curr_job;
				
		//start each job once
		for(int i = 1; i < num_jobs; i++) {
			
			// time_elapsed += jobs[curr_job].getProcessing_time();
			// System.out.println(time_elapsed);
			
			// replace 
			// smtwtp.calculateValue(alpha, beta, time_elapsed);
			
			//calculates the denominator of the probabilistic selection rule
			double sum_prob = findSumProb(curr_job, unperformed_jobs);
			//creates an array of probabilities from (0.0, 1.0)
			double[] prob_array = findProb(curr_job, unperformed_jobs, sum_prob);
						
			//generate random double to pick next job, make sure its not 0
			double prob = r.nextDouble();
			while(prob == 0.0) {
				prob = r.nextDouble();
			}
			
			int next_job = curr_job;
			//finds which job matches the random generated double
			for(int j = 0; j < num_jobs; j++) {
				if(prob <= prob_array[j]) {
					next_job = j;
					break;
				}
			}
			
			workflow[i] = next_job;
			unperformed_jobs.remove(next_job);
			curr_job = next_job;
		}
		
		return workflow;
	}
	
	/*
	 * Purpose: To calculate the denominator of the probabilistic selection rule. This
	 * 	is the sum of each ants pheromone^alpha*heuristic^beta.
	 * Parameters: the current city the ant is at (int), the set of unvisited cities (Set<Integer>)
	 * Return: Returns the sum of path values (double)
	 * 
	 */
	public double findSumProb(int curr_job, Set<Integer> unperformed_jobs) {
		double sum_prob = 0.0;
		
		//only sums the cities that are unvisited
		for(int job : unperformed_jobs) {
			
			//larger index first
			if(curr_job < job) {
				sum_prob += smtwtp.getSmtwtp_value()[job][curr_job];
				
			} else {
				sum_prob += smtwtp.getSmtwtp_value()[curr_job][job];
			
			}
		}
		return sum_prob;
	}
	
	/*
	 * Purpose: To create an array of probabilities for each of the paths from the current
	 * 	city. The array stores the probability of each path + the probability of the path
	 * 	at the index before it (so with probabilities 0.2, 0.3, 0.5 the array is [0.2, 0.5, 1.0]).
	 * 	This allows us to generate a random double, which will correspond to a value in the array.
	 * Parameters: the current city the ant is at (int), the set of unvisited cities (Set<Integer>),
	 * 	the denominator of the selection rule (double)
	 * Return: an array of probabilities from (0.0, 1.0) (double[])
	 * 
	 */
	public double[] findProb(int curr_job, Set<Integer> unperformed_jobs, double sum_prob) {
		
		double[] prob = new double[num_jobs];
		
		//for each of the jobs
		for(int i = 0; i < num_jobs-1; i++) {
			
			//first element in the array has no previous element
			if(i == 0) {
				
				//if the city is unvisited, set probability
				if(unperformed_jobs.contains(i)) {
					
					if(curr_job < i) {
						prob[i] = smtwtp.getSmtwtp_value()[i][curr_job] / sum_prob;

					} else {
						prob[i] = smtwtp.getSmtwtp_value()[curr_job][i] / sum_prob;
				
					}
					
				//if city is visited, probability is 0.0
				} else {
					prob[i] = 0.0;
				}
				
			//if not first element, add previous elements probability
			} else {
				
				//if the city is unvisited, set probability
				if(unperformed_jobs.contains(i)) {
					if(curr_job < i) {
						prob[i] = prob[i-1] + (smtwtp.getSmtwtp_value()[i][curr_job] / sum_prob);

					} else {
						prob[i] = prob[i-1] + (smtwtp.getSmtwtp_value()[curr_job][i] / sum_prob);
				
					}
				
				//if city is visited, probability is previous element value
				} else {
					prob[i] = prob[i-1];
				}
				
			}			
		
		}
		
		//set last probability to 1.0, so range is from 0.0 to 1.0 
		prob[num_jobs-1] = 1.0;
		
		return prob;
	}
	
	
	/*
	 * Purpose: Deposits pheromone on each leg of each ants tour. The amount of pheromone
	 * 	deposited is dependent on the length of the tour. Further if the leg is in the
	 * 	bsf tour, additional pheromone is deposited.
	 * Parameters: none
	 * Return: none, updates the pheromone levels
	 * 
	 */
	public void depositPheromone() {
		
		int hash_key;
		double added_pheromone;
		int job1, job2;
		
		//for each ant
		for(int i = 0; i < num_ants; i++) {
			added_pheromone = 0;
			
			//for each transition
			for(int j = 0; j < num_jobs - 1; j++) {
				
				//sort the jobs in the workflow
				if(hive.getHive()[i].workflow[j] < hive.getHive()[i].workflow[j+1]) {
					job1 = hive.getHive()[i].workflow[j];
					job2 = hive.getHive()[i].workflow[j+1];
				} else {
					job1 = hive.getHive()[i].workflow[j+1];
					job2 = hive.getHive()[i].workflow[j];
				}

				//check for elitism factor by checking hashkey
				hash_key = (((job1 + job2)*(job1 + job2 + 1)) / 2) + job2;
				if(transitions_in_best_workflow.contains(hash_key)) {
										
					//if the transition is in bsf workflow, add more pheromone
					added_pheromone += elitism_factor * (1/hive.getBest_score_so_far());
				}
				
				//increase pheromone in leg normally
				added_pheromone += 1 / hive.getHive()[i].getWorkflow_score();
				smtwtp.increasePheromone(job2, job1, added_pheromone);	
				
			}
		}
	}
	
	/*
	 * Purpose: To generally evaporate pheromone off each leg in the environemnt, 
	 * 	based on the user inputed value for rho.
	 * Parameters: none
	 * Return: none, sets teh new pheromone values
	 * 
	 */
	public void evaporatePheromone() {
		for(int i = 0; i < num_jobs; i++) {
			for(int j = 0; j < i; j++) {
				smtwtp.evaporatePheromone(i, j, rho);
			}
		}
	}

}
