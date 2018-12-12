import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GeneticAlgorithm {
		
    public int population_size;
    public double mutation_prob;
    public int max_generations;
    public int num_jobs;

    public double crossover_prob;

    public Individual[] population;
    public SMTWTP smtwtp;
    
    
    public GeneticAlgorithm(int population_size, double mutation_prob, int max_generations, double crossover_prob) {
    	
    	this.population_size = population_size;
    	this.mutation_prob = mutation_prob;
    	this.max_generations = max_generations;
    	this.crossover_prob = crossover_prob;
    }

	public void RunGA(int[][] workflows, SMTWTP smtwtp) {
		
		Random rand = new Random();
		
		Individual[] parents = new Individual[2];
		Individual[] children = new Individual[2];
		Individual[] new_population;
		
		this.smtwtp = smtwtp;
		this.num_jobs = smtwtp.getNum_jobs();
		this.population_size = workflows.length;	
		population = new Individual[population_size];
		
		for (int i = 0; i < population_size; i++) {
			population[i] = new Individual(num_jobs, smtwtp, workflows[i]);
			population[i].scoreWorkflow();
		}
		
		int count, generation, best_score = Integer.MAX_VALUE;
		int[] best_workflow = new int[num_jobs];
		
		generation = 0;
		
		while (generation < max_generations) {
			
			new_population = new Individual[population_size];
			
			count = 0;
					
			while (count < population_size) {
						
				parents = tournamentSelection(population);
						
				if (rand.nextDouble() < crossover_prob) {
					children = iPOX(parents);
				}
				else {
					children[0] = parents[0];
					children[1] = parents[1];
				}
						
				new_population[count] = children[0];
				++count;
				
				if (count < population_size) {
					new_population[count] = children[1];
					++count;
				}
			}
			
			population = mutation(new_population);
			
			for (int i = 0; i < population_size; i++) {
				population[i].scoreWorkflow();
				if (population[i].getWorkflow_score() < best_score) {
					best_score = population[i].getWorkflow_score();
					best_workflow = population[i].getWorkflow().clone();
				}
			}
			
			System.out.println("Generation " + generation + ", best score is " + best_score);
			
			generation++;
		}
		System.out.println("After " + max_generations + " generations, the best score is " + best_score);
		System.out.println("Best workflow:");
		for (int i = 0; i < num_jobs; i++) {
			System.out.print(best_workflow[i] + " ");
		}
	}
    
    // return two individuals that have undergone tournament selection
    public Individual[] tournamentSelection(Individual[] population) {

        Random rand = new Random();

        int count = 0;
        int index1, index2;
        Individual one, two;
        Individual[] parents = new Individual[2];

        while (count < 2) {
        	
        	index1 = (int) (population_size * rand.nextDouble());
        	index2 = (int) (population_size * rand.nextDouble());

        	if (index1 != index2) {
        	
        		one = population[index1];
        		two = population[index2];
        	
        		if (one.getWorkflow_score() > two.getWorkflow_score()) {
        			parents[count] = one;
        			++count;
        		}
        		else if (one.getWorkflow_score() < two.getWorkflow_score()) {
        			parents[count] = two;
        			++count;
        		}
        	}
        }
        return parents;
    }
    
    public Individual[] iPOX(Individual[] parents) {
    	
    	Random rand = new Random();
		Set<Integer> child1_set = new HashSet<Integer>();
		Set<Integer> child2_set = new HashSet<Integer>();
		int curr, index;
    	
    	int cut_point = rand.nextInt(num_jobs);
    	
    	Individual[] children = new Individual[2];
    	
    	int[] child1_workflow = new int[num_jobs];
    	int[] child2_workflow = new int[num_jobs];
    	
    	int[] workflow1 = parents[0].getWorkflow();
    	int[] workflow2 = parents[1].getWorkflow();
    	
    	for (int i = 0; i < cut_point; i++) {
    		child1_workflow[i] = workflow1[i];
    		child2_workflow[i] = workflow2[i];
    		
    		child1_set.add(workflow1[i]);
    		child2_set.add(workflow2[i]);
    	}
    	
    	curr = cut_point;
    	index = 0;
    	
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
    	
    	while (curr < num_jobs && index < num_jobs) {
    		
    		if (!child2_set.contains(workflow1[index])) {
    			child2_workflow[curr] = workflow1[index];
    			child2_set.add(workflow1[index]);
    			curr++;
    		}
    		
    		index++;
    	}
    	
    	children[0] = new Individual(num_jobs, smtwtp, child1_workflow);
    	children[1] = new Individual(num_jobs, smtwtp, child2_workflow);
    	
    	return children;
    }
    
    // potentially mutate each characteristic for each individual in population
    public Individual[] mutation(Individual[] population){
    	
        Random rand = new Random();
        
        int temp;
        int[] workflow;
        
        for (int i = 0; i < population_size; i++) {
        	
        	if (rand.nextDouble() < mutation_prob) {
        	
        		workflow = population[i].getWorkflow();
        		int range_size = rand.nextInt(3) + 2;
        		int starting_index = rand.nextInt(num_jobs - range_size);
        		int counter = range_size - 1;
        	
        		for (int j = starting_index; j < starting_index + (range_size / 2); j++) {
        			temp = workflow[j];
        			workflow[j] = workflow[j + counter];
        			workflow[j + counter] = temp;
        			counter -= 2;
        		}
        		
        		population[i].setWorkflow(workflow);
        	}
        }
        
        return population;
    }
    
    /*
    // read in the arguments for ga
	public void ReadArguments(String[] args) {
        
            testFile = args[0];
            populationSize = Integer.parseInt(args[1]);
            selectionType = args[2];
            crossoverMethod = args[3];
            crossoverProb = Float.parseFloat(args[4]);
            mutationProb = Float.parseFloat(args[5]);
            numGenerations = Integer.parseInt(args[6]);
        
    }

	// print out info when algorithm finished
	public void algorithmDone(Individual best, int fitness, int iteration) {

        double percentClauses = ((double)fitness/(double)numClauses)*100;

        System.out.println("Problem file: " + testFile);
        System.out.println("Number of variables: " + numVariables);
        System.out.println("Number of clauses: " + numClauses);
        System.out.println("Number of clauses satisfied: " + fitness);
        System.out.println("Percentage of clauses satisfied: " + percentClauses + "%");
        System.out.println("Best assignment: " + best.GetAssignments());
        System.out.println("Found in iteration " + (iteration+1));
        System.exit(0);
    }
    */
}
