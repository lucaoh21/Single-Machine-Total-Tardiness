/*
 *  Author Luca Ostertag-Hill, Tom Lucy, Jake Rourke 
 *  Date 11/7/2018
 *  
 *  This class defines the Ant Colony System algorithm. Called by RUNACO and given
 *  the user inputed values, it runs the ACS algorithm for the specified number of 
 *  iterations or until a percentage of the optimal value is met. The ACS algorithm
 *  (with m ants) iteratively builds m tours using either the greedy or probability
 *  selection rule. The greedy rule chooses the next path to maximize the pheromone 
 *  heuristic info, while the probability rule randomly chooses a path based on 
 *  probabilities assigned by a path's pheromone and heuristic. ACS has a local
 *  pheromone update rule, which evaporates part of the pheromone off each leg 
 *  used by an ant in its tour. It also has a global update rule, which adds and
 *  evaporates pheromone each iteration on the legs of the tour in the best tour
 *  found so far. No other general evaporation occurs.
 *  
 *  The class also contains a base-pheromone equation from the ACO handout, that is
 *  used when the user inputs a base_tau of 0.0.
 *  
 */


import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ACS {
	
	int num_ants;
	int max_iterations;
	int num_cities;
	int optimal;
	double alpha;
	double beta;
	double rho;
	double epsilon;
	double base_tau;
	double greedy_prob;
	double stop_percent;
	String filename;
	
	TSP tsp;
	HIVE hive;
	
	//nano seconds in seconds
	public static final double NANO_TO_SEC = 1000000000;
	public static final int PRINT_ON_ITERATION = 20;
	
	public ACS(int num_ants, int max_iterations, double alpha, double beta, double rho, 
			double epsilon, double base_tau, double greedy_prob, double stop_percent, 
			int optimal, String filename) {
		
		this.num_ants = num_ants;
		this.max_iterations = max_iterations;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.epsilon = epsilon;
		this.base_tau = base_tau;
		this.greedy_prob = greedy_prob;
		this.stop_percent = stop_percent;
		this.optimal = optimal;
		this.filename = filename;
	}
	
	
	/*
	 * Purpose: The main algorithm of the ACS algorithm. It iteratively build tours, finds
	 * 	the best tour, and updates pheromone levels using the local and global pheromone
	 * 	update. The algorithm stops iterating when a specified number of iterations is met
	 * 	or if the algorithm has found a solution within the specified optimal percentage.
	 * Parameters: none
	 * Return: none, prints the best so far in each iteration
	 * 
	 */
	public void runACS() {
		
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
			construct();
			//checks if there is new best
			hive.findBest();
			//performs global pheromone update
			globalPheromoneUpdate();
			
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
	 * Purpose: To determine the base tau for the paths in the problem.
	 * 	If the user inputs a valid base tau (>0.0) we use that value. Otherwise
	 * 	we use the equation from the handout to calculate the base tau.
	 * Parameters: none
	 * Return: General base tau (double) for the problem.
	 */
	public double setBasePheromone() {
		
		//if user inputs valid base tau, we use it
		if(base_tau != 0.0) {
			return base_tau; 
		}
		
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
		return 1/(num_cities * total_greedy_distance);
		
	}
	
	/*
	 * Purpose: Constructs a tour for each ant in the hive. Chooses whether to perform
	 * 	greedy or probabilistic selection at each city, depending on user input for 
	 * 	greedy probability. It also calls scoreTour() to score the tour of each ant and
	 * 	localPheromoneUpdate to perform the evaporation of used legs. 
	 * Parameters: none
	 * Return: none, sets the tour for each ant in the hive
	 * 
	 */
	public void construct() {
		
		Set<Integer> unvisited_cities = new HashSet<Integer>();
		Random r = new Random();
		int[] tour = new int[num_cities];
		
		//construct a tour for each ant in the hive
		for (int i = 0; i < num_ants; i++) {
			
			unvisited_cities.clear();
			//initialize set with all cities
			for(int j = 0; j < num_cities; j++) {
				unvisited_cities.add(j);
			}
			
			//choose random starting city
			int curr_city = r.nextInt(num_cities);
			unvisited_cities.remove(curr_city);
			tour[0] = curr_city;
			
			for(int j = 1; j < num_cities; j++) {
			
				//choose which selection method to use
				if(r.nextDouble() < greedy_prob) {
					curr_city = greedySelection(curr_city, unvisited_cities);
					tour[j] = curr_city;
					unvisited_cities.remove(curr_city);
				} else {
					curr_city = probSelection(curr_city, unvisited_cities);
					tour[j] = curr_city;
					unvisited_cities.remove(curr_city);
				}
			
			}
			
			hive.getHive()[i].setTour(tour);
			//score tour
			hive.getHive()[i].scoreTour();
			//perform local pheromone update
			localPheromoneUpdate(tour);

		}
	}
	
	
	/*
	 * Purpose: Chooses the next city for an ant using the greedy selection method, which
	 * 	chooses the next path based on maximizing pheromone * (1/heuristic)^beta.
	 * Parameters: none
	 * Return: Returns the next city to visit (int).
	 * 
	 */
	public int greedySelection(int curr_city, Set<Integer> unvisited_cities) {
		
		int best_next_city = curr_city;
		double greedy_value, best_greedy_value = 0.0;
		
		//only uses cities that are unvisited
		for(int city : unvisited_cities) {
			
			//only half the distance array is full, so larger index first
			if(curr_city < city) {
				
				//equation for greedy rule
				greedy_value = tsp.getTsp_pheromone()[city][curr_city] * 
						Math.pow(1/tsp.getTsp_distance()[city][curr_city], beta);
				
				//if its better than the best, set it to the best
				if(greedy_value > best_greedy_value) {
					best_greedy_value = greedy_value;
					best_next_city = city;
				}
				
			//larger index first
			} else {
				greedy_value = tsp.getTsp_pheromone()[curr_city][city] * 
						Math.pow(1/tsp.getTsp_distance()[curr_city][city], beta);
				if(greedy_value > best_greedy_value) {
					best_greedy_value = greedy_value;
					best_next_city = city;
				}
			}		
		}
		
		return best_next_city;
	}
	
	/*
	 * Purpose: Chooses the next city for an ant using the probabilistic selection rule
	 * 	for the general Ant System algorithm. Each possible leg is assigned a probability based
	 * 	on the pheromone level of the leg and the heuristic info. The leg is then chosen
	 * 	randomly based on these probabilities. 
	 * Parameters: none
	 * Return: Returns the next city (int)
	 * 
	 */
	public int probSelection(int curr_city, Set<Integer> unvisited_cities) {
		
		Random r = new Random();
		
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
		
		return next_city;
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
	 * Purpose: Updates the pheromone levels on each leg in the given tour using the local
	 * 	pheromone update rule of ACS. 
	 * Parameters: the tour containing the legs to be updated (int[])
	 * Return: none, sets the pheromone levels of the used legs
	 * 
	 */
	public void localPheromoneUpdate(int[] tour) {
		
		//for each leg in the tour
		for(int i = 0; i < num_cities-1; i++) {
			
			//larger index first
			if(tour[i] < tour[i+1]) {
				//local pheromone equation
				tsp.getTsp_pheromone()[tour[i+1]][tour[i]] = (1 - epsilon) * 
						tsp.getTsp_pheromone()[tour[i+1]][tour[i]] + epsilon * base_tau;
			} else {
				tsp.getTsp_pheromone()[tour[i]][tour[i+1]] = (1 - epsilon) * 
						tsp.getTsp_pheromone()[tour[i]][tour[i+1]] + epsilon * base_tau;
			}
		}
	}
	
	/*
	 * Purpose: Update the pheromone levels on each of the legs in the best tour so far, 
	 * 	using the global pheromone update rule.
	 * Parameters: none
	 * Return: none, sets the pheromone of the legs in the bsf tour
	 * 
	 */
	public void globalPheromoneUpdate() {
		int city1, city2;
		
		//for each leg in the best tour
		for(int i = 0; i < num_cities-1; i++) {
			
			//set larger index (half the table is empty)
			if(hive.getBest_tour_so_far()[i] < hive.getBest_tour_so_far()[i+1]) {
				city1 = hive.getBest_tour_so_far()[i];
				city2 = hive.getBest_tour_so_far()[i+1];
			} else {
				city1 = hive.getBest_tour_so_far()[i+1];
				city2 = hive.getBest_tour_so_far()[i];
			}
			
			//global pheromone update rule
			tsp.getTsp_pheromone()[city2][city1] = (1 - rho) * 
					tsp.getTsp_pheromone()[city2][city1] + rho * (1/tsp.getTsp_distance()[city2][city1]);
			
		}
	}

}

