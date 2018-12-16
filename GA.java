/*
 * Author Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date 12/16/2018
 * 
 * This class defines the Genetic Algorithm. Called by SMTWTP_HYBRID and given
 * the user inputed values, it runs the GA for the specified number of
 * generations. If being run as a part of the hybrid, the GA algorithm 
 * performs local search on a population of solutions produced by the EAS
 * algorithm. If being run alone, GA runs on a randomly initialized set of
 * possible workflows. In each generation, the tournament selection is
 * performed to select a breeding pool, offspring are generated through
 * Order One Crossover, and two types of mutation, General Swap and Range
 * Reversal, may be performed on the new population. 
 * 
 */

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GA {
		
	// the number of workflows in the population
    public int population_size;
    // the probability of mutation on a given individual
    public double mutation_prob;
    // the number of generations
    public int max_generations;
    // the number of jobs in a workflow
    public int num_jobs;
    // the probability of crossover on a given set of parents
    public double crossover_prob;

    public INDIVIDUAL[] population;
    public SMTWTP smtwtp;
    
    
    public GA(int population_size, double mutation_prob, int max_generations, double crossover_prob) {
    	
    	this.population_size = population_size;
    	this.mutation_prob = mutation_prob;
    	this.max_generations = max_generations;
    	this.crossover_prob = crossover_prob;
    }

	/*
	 * Purpose: The main algorithm of the GA. It iteratively selects a breeding pool,
	 * performs crossover, and mutates offspring.
	 * Parameters: A 2d integer array of initial workflows, the smtwtp problem object
	 * Return: none, prints the best so far in each iteration
	 * 
	 */
	public void RunGA(int[][] workflows, SMTWTP smtwtp) {
		
		Random rand = new Random();
		
		INDIVIDUAL[] parents = new INDIVIDUAL[2];
		INDIVIDUAL[] children = new INDIVIDUAL[2];
		INDIVIDUAL[] new_population;
		
		this.smtwtp = smtwtp;
		this.num_jobs = smtwtp.getNum_jobs();
		this.population_size = workflows.length;	
		population = new INDIVIDUAL[population_size];
		
		// create the initial individuals
		for (int i = 0; i < population_size; i++) {
			population[i] = new INDIVIDUAL(num_jobs, smtwtp, workflows[i]);
			population[i].scoreWorkflow();
		}
		
		int best_score = Integer.MAX_VALUE;
		int[] best_workflow = new int[num_jobs];
		
		// find the best member of the inital populaiton
		for (int i = 0; i < population_size; i++) {
			if (population[i].getWorkflow_score() < best_score) {
				best_score = population[i].getWorkflow_score();
				best_workflow = population[i].getWorkflow().clone();
			}
		}
		
		int count, generation;
		
		generation = 0;
		
		// for each generation
		while (generation < max_generations) {
			
			new_population = new INDIVIDUAL[population_size];
			
			count = 0;
					
			while (count < population_size) {
						
				// select parents for breeding
				parents = tournamentSelection(population);
						
				// perform crossover with some probability
				if (rand.nextDouble() < crossover_prob) {
					children = orderOneCrossover(parents);
				}
				else {
					children[0] = parents[0];
					children[1] = parents[1];
				}
						
				// add children to the new population
				new_population[count] = children[0];
				++count;
				
				if (count < population_size) {
					new_population[count] = children[1];
					++count;
				}
			}
			
			// mutate the new population
			population = mutation(new_population);
			
			// find the best workflow so far
			for (int i = 0; i < population_size; i++) {
				population[i].scoreWorkflow();
				if (population[i].getWorkflow_score() < best_score) {
					best_score = population[i].getWorkflow_score();
					best_workflow = population[i].getWorkflow().clone();
				}
			}
			
			generation++;
		}
		System.out.println(best_score);
	}
    
	/*
	 * Purpose: This function takes in the population and performs tournament selection
	 * twice to select two parents for breeding. 
	 * Parameters: An array of all individuals in the population
	 * Return: An array of two parents
	 * 
	 */
    public INDIVIDUAL[] tournamentSelection(INDIVIDUAL[] population) {

        Random rand = new Random();

        int count = 0;
        int index1, index2;
        INDIVIDUAL one, two;
        INDIVIDUAL[] parents = new INDIVIDUAL[2];

        while (count < 2) {
        	
        	index1 = (int) (population_size * rand.nextDouble());
        	index2 = (int) (population_size * rand.nextDouble());

        	if (index1 != index2) {
        	
        		one = population[index1];
        		two = population[index2];
        	
        		if (one.getWorkflow_score() < two.getWorkflow_score()) {
        			parents[count] = one;
        			++count;
        		}
        		else if (one.getWorkflow_score() >= two.getWorkflow_score()) {
        			parents[count] = two;
        			++count;
        		}
        	}
        }
        return parents;
    }
    
	/*
	 * Purpose: Performs crossover on two parents in an order one fashion. To create an
	 * offspring, a substring from the beginning of a parent is selected. The remaining
	 * slots are filled by iterating through the other parent and inserting a symbol if
	 * it was not already included in the offspring.
	 * Parameters: An array of two parents
	 * Return: An array of two offspring
	 * 
	 */
    public INDIVIDUAL[] orderOneCrossover(INDIVIDUAL[] parents) {
    	
    	Random rand = new Random();
		Set<Integer> child1_set = new HashSet<Integer>();
		Set<Integer> child2_set = new HashSet<Integer>();
		int curr, index;
    	
    	int cut_point = rand.nextInt(num_jobs);
    	
    	INDIVIDUAL[] children = new INDIVIDUAL[2];
    	
    	int[] child1_workflow = new int[num_jobs];
    	int[] child2_workflow = new int[num_jobs];
    	
    	int[] workflow1 = parents[0].getWorkflow();
    	int[] workflow2 = parents[1].getWorkflow();
    	
    	// fill the beginning of the children up to the cut point
    	for (int i = 0; i < cut_point; i++) {
    		child1_workflow[i] = workflow1[i];
    		child2_workflow[i] = workflow2[i];
    		
    		child1_set.add(workflow1[i]);
    		child2_set.add(workflow2[i]);
    	}
    	
    	curr = cut_point;
    	index = 0;
    	
    	// fill the remainder of child 1
    	while (curr < num_jobs && index < num_jobs) {
    		
    		if (!child1_set.contains(workflow2[index])) {
    			child1_workflow[curr] = workflow2[index];
    			child1_set.add(workflow2[index]);
    			curr++;
    		}
    		
    		index++;
    	}
    	
    	curr = cut_point;
    	index = 0;
    	
    	// fill the remainder of child 2
    	while (curr < num_jobs && index < num_jobs) {
    		
    		if (!child2_set.contains(workflow1[index])) {
    			child2_workflow[curr] = workflow1[index];
    			child2_set.add(workflow1[index]);
    			curr++;
    		}
    		
    		index++;
    	}
    	
    	children[0] = new INDIVIDUAL(num_jobs, smtwtp, child1_workflow);
    	children[1] = new INDIVIDUAL(num_jobs, smtwtp, child2_workflow);
    	
    	return children;
    }
    
    /*
	 * Purpose: With some probability, performs one or both General Swap and
	 * Range Reversal mutation. General Swap mutation selects two indices
	 * in the workflow and swaps their contents. Range Reversal selects a 
	 * random range of length 2 to 4 in the workflow and reverses the contents.
	 * Parameters: The entire population
	 * Return: The entire population, with some mutation performed
	 * 
	 */
    public INDIVIDUAL[] mutation(INDIVIDUAL[] population){
    	
        Random rand = new Random();
        
        int temp;
        int[] workflow;
        
        int mutate1, mutate2;
        
        for (int i = 0; i < population_size; i++) {
        	
        	//mutation 1.0 finds a range in the jobs list and reverses the order of jobs
        	if (rand.nextDouble() < mutation_prob) {
        	
        		workflow = population[i].getWorkflow();
        		int range_size = rand.nextInt(3) + 2;
        		int starting_index = rand.nextInt(num_jobs - range_size);
        		int counter = range_size - 1;
        	
        		// reverse the order of only the selected range
        		for (int j = starting_index; j < starting_index + (range_size / 2); j++) {
        			temp = workflow[j];
        			workflow[j] = workflow[j + counter];
        			workflow[j + counter] = temp;
        			counter -= 2;
        		}
        		
        		population[i].setWorkflow(workflow);
        	}
        	
        	//mutation 2.0 finds two jobs and flips there place in the jobs list
        	if (rand.nextDouble() < mutation_prob) {
        		
        		mutate1 = rand.nextInt(num_jobs);
        		mutate2 = rand.nextInt(num_jobs);
        		
        		workflow = population[i].getWorkflow();
        		
        		temp = workflow[mutate1];
        		workflow[mutate1] = workflow[mutate2];
        		workflow[mutate2] = temp;
        		
        		population[i].setWorkflow(workflow);
        		
        	}
        	
        }
        
        return population;
    }

}
