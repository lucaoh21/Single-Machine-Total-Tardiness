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
	int num_cities;
	int optimal;
	double alpha;
	double beta;
	double rho;
	double elitism_factor;
	double stop_percent;
	String filename;
	
	TSP tsp;
	HIVE hive;
	Set<Integer> paths_in_best_tour;
	
	public static final double NANO_TO_SEC = 1000000000;
	public static final int PRINT_ON_ITERATION = 20;
	public static final int STOP_TIME = Integer.MAX_VALUE;
	
	public EAS(int num_ants, int max_iterations, double alpha, double beta, double rho,
			double elitism_factor, double stop_percent, int optimal, String filename) {
		
		this.num_ants = num_ants;
		this.max_iterations = max_iterations;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitism_factor = elitism_factor;
		this.stop_percent = stop_percent;
		this.optimal = optimal;
		this.filename = filename;
		this.paths_in_best_tour = new HashSet<Integer>();
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
	public void runEAS() {
		
		double startTime = System.nanoTime();
		
		//creates a new traveling salesman problem and initializes
		tsp = new TSP(filename);
		num_cities = tsp.getNum_cities();
		tsp.initializeDistances();
		tsp.initializePheromone(setBasePheromone());
		
		//creates a new hive object
		hive = new HIVE(num_ants, tsp.getNum_cities(), tsp);
		
		int num_iteration = 0;
		double bsf_percent = Double.MAX_VALUE;
		
		//iterates until max iterations or specified percentage is met
		while(num_iteration < max_iterations && bsf_percent > stop_percent) {
			
			//recalculate the numerator of the prob selection rule
			tsp.calculateValue(alpha, beta);
			//construct the tours
			construct(num_iteration);
			//checks if there is new best
			if(hive.findBest()) {
				
				//if there is a new best, the paths in best_so_far set are updated
				updatePathSet();
			}
			
			//perform evaporation and depositing of pheromone
			evaporatePheromone();
			depositPheromone();
			
			bsf_percent = (hive.getBest_score_so_far() / optimal) - 1.0;

			System.out.println("in iteration: " + num_iteration + ", best score is " + hive.getBest_score_so_far());

			num_iteration++;
		}
		
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
	public void updatePathSet() {
		
		//removes the old hashkeys
		paths_in_best_tour.clear();

		int city1, city2;
		
		//for each city in the bsf tour
		for(int i = 0; i < num_cities-1; i++) {
			
			//sort the cities in the path
			if(hive.getBest_tour_so_far()[i] < hive.getBest_tour_so_far()[i+1]) {
				city1 = hive.getBest_tour_so_far()[i];
				city2 = hive.getBest_tour_so_far()[i+1];
			} else {
				city1 = hive.getBest_tour_so_far()[i+1];
				city2 = hive.getBest_tour_so_far()[i];
			}
		
			//create hashkey using Cantor Pairing Function
			paths_in_best_tour.add((((city1 + city2)*(city1 + city2 + 1)) / 2) + city2);
			
		}
	}
	
	/*
	 * Purpose: To determine the base tau for the paths in the problem, useing
	 *  the equation from the handout to calculate the base tau.
	 * Parameters: none
	 * Return: General base tau (double) for the problem.
	 */
	public double setBasePheromone() {
		
		Set<Integer> unvisited_cities = new HashSet<Integer>();
		//create set of cities to be visited
		for(int j = 0; j < num_cities; j++) {
			unvisited_cities.add(j);
		}
		
		Random r = new Random();
		
		//choose random starting city
		int curr_city = r.nextInt(num_cities);
		unvisited_cities.remove(curr_city);
		
		int best_next_city = curr_city;
		double total_greedy_distance = 0;
		double distance, best_next_distance;
		
		//create a greedy path from the random starting location
		for(int i = 1; i < num_cities; i++) {
			best_next_distance = Double.MAX_VALUE;
			
			//only uses cities that are unvisited
			for(int city : unvisited_cities) {
					
				//only half the distance array is full
				if(curr_city < city) {
					distance = tsp.getTsp_distance()[city][curr_city];
					if(distance < best_next_distance) {
						best_next_distance = distance;
						best_next_city = city;
					}
					
				} else {
					distance = tsp.getTsp_distance()[curr_city][city];
					if(distance < best_next_distance) {
						best_next_distance = distance;
						best_next_city = city;
					}
					
				}
			}
			
			//add the closest city, and repeat the loop
			total_greedy_distance += best_next_distance;
			unvisited_cities.remove(best_next_city);
			curr_city = best_next_city;
		}
		
		//equation for base tau from ant variations handout
		return (elitism_factor + num_ants)/(rho * total_greedy_distance);
		
	}
	
	/*
	 * Purpose: Constructs a tour for each ant in the hive. Performs probabilistic 
	 * selection. It also calls scoreTour() to score the tour of each ant. 
	 * Parameters: none
	 * Return: none, sets the tour for each ant in the hive
	 * 
	 */
	public void construct(int iteration) {		
		
		//construct a tour for each ant in the hive
		for (int i = 0; i < num_ants; i++) {
			
			//using the probabilistic selection technique
			hive.getHive()[i].setTour(probSelection());
			
			//scores the ants tour
			hive.getHive()[i].scoreTour();

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
		
		//initialize set with all cities
		Set<Integer> unvisited_cities = new HashSet<Integer>();
		for(int i = 0; i < num_cities; i++) {
			unvisited_cities.add(i);
		}
		
		int[] tour = new int[num_cities];

		Random r = new Random();
		
		//choose random starting city
		int curr_city = r.nextInt(num_cities);
		unvisited_cities.remove(curr_city);
		tour[0] = curr_city;
		
		//visit each city once
		for(int i = 1; i < num_cities; i++) {
			
			//calculates the denominator of the probabilistic selection rule
			double sum_prob = findSumProb(curr_city, unvisited_cities);
			//creates an array of probabilities from (0.0, 1.0)
			double[] prob_array = findProb(curr_city, unvisited_cities, sum_prob);
						
			//generate random double to pick next city, make sure its not 0
			double prob = r.nextDouble();
			while(prob == 0.0) {
				prob = r.nextDouble();
			}
			
			int next_city = curr_city;
			//finds which city matches the random generated int
			for(int j = 0; j < num_cities; j++) {
				if(prob <= prob_array[j]) {
					next_city = j;
					break;
				}
			}
			
			tour[i] = next_city;
			unvisited_cities.remove(next_city);
			curr_city = next_city;
		}
		
		return tour;
	}
	
	/*
	 * Purpose: To calculate the denominator of the probabilistic selection rule. This
	 * 	is the sum of each ants pheromone^alpha*heuristic^beta.
	 * Parameters: the current city the ant is at (int), the set of unvisited cities (Set<Integer>)
	 * Return: Returns the sum of path values (double)
	 * 
	 */
	public double findSumProb(int curr_city, Set<Integer> unvisited_cities) {
		double sum_prob = 0.0;
		
		//only sums the cities that are unvisited
		for(int city : unvisited_cities) {
			
			//larger index first
			if(curr_city < city) {
				sum_prob += tsp.getTsp_value()[city][curr_city];
				
			} else {
				sum_prob += tsp.getTsp_value()[curr_city][city];
			
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
	public double[] findProb(int curr_city, Set<Integer> unvisited_cities, double sum_prob) {
		double[] prob = new double[num_cities];
		
		//for each of the cities
		for(int i = 0; i < num_cities-1; i++) {
			
			//first element in the array has no previous element
			if(i == 0) {
				
				//if the city is unvisited, set probability
				if(unvisited_cities.contains(i)) {
					if(curr_city < i) {
						prob[i] = tsp.getTsp_value()[i][curr_city] / sum_prob;

					} else {
						prob[i] = tsp.getTsp_value()[curr_city][i] / sum_prob;
				
					}
					
				//if city is visited, probability is 0.0
				} else {
					prob[i] = 0.0;
				}
				
			//if not first element, add previous elements probability
			} else {
				
				//if the city is unvisited, set probability
				if(unvisited_cities.contains(i)) {
					if(curr_city < i) {
						prob[i] = prob[i-1] + (tsp.getTsp_value()[i][curr_city] / sum_prob);

					} else {
						prob[i] = prob[i-1] + (tsp.getTsp_value()[curr_city][i] / sum_prob);
				
					}
				
				//if city is visited, probability is previous element value
				} else {
					prob[i] = prob[i-1];
				}
				
			}			
		
		}
		
		//set last probability to 1.0, so range is from 0.0 to 1.0 
		prob[num_cities-1] = 1.0;
		
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
		int city1, city2;
		
		//for each ant
		for(int i = 0; i < num_ants; i++) {
			added_pheromone = 0;
			
			//for each leg
			for(int j = 0; j < num_cities - 1; j++) {
				
				//sort the cities in the path
				if(hive.getHive()[i].tour[j] < hive.getHive()[i].tour[j+1]) {
					city1 = hive.getHive()[i].tour[j];
					city2 = hive.getHive()[i].tour[j+1];
				} else {
					city1 = hive.getHive()[i].tour[j+1];
					city2 = hive.getHive()[i].tour[j];
				}

				//check for elitism factor by checking hashkey
				hash_key = (((city1 + city2)*(city1 + city2 + 1)) / 2) + city2;
				if(paths_in_best_tour.contains(hash_key)) {
										
					//if the leg is in bsf tour, add more pheromone
					added_pheromone += elitism_factor * (1/hive.getBest_score_so_far());
				}
				
				//increase pheromone in leg normally
				added_pheromone += 1/hive.getHive()[i].getTour_score();
				tsp.increasePheromone(city2, city1, added_pheromone);	
				
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
		for(int i = 0; i < num_cities; i++) {
			for(int j = 0; j < i; j++) {
				tsp.evaporatePheromone(i, j, rho);
			}
		}
	}

}
