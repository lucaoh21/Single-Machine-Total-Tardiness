/*
 * Author Luca Osterag-Hill, Tom Lucy, Jake Rourke
 * Date 11/6/2018
 * 
 * This class is the main class for the ACO project. Given the user inputs,
 * the class calls either EAS or ACS, which do the iterative building.
 * 
 */

public class SMTWTP_HYBRID {
	
	//the number of ants
	public static int num_ants;
	//the number of iterations
	public static int num_iterations;
	//the degree of influence of the pheromone component
	public static double alpha;
	//the degree of influence of the heuristic component
	public static double beta;
	//the pheromone evaporation factor
	public static double rho;
	//the elitism factor for eas (often equal to num ants)
	public static double elitism_factor;
	//the optimal score for the problem
	public static int optimal;
	//the percentage of optimal on which the algorithm should stop
	public static double stop_percent;
	//the smttp problem
	public static String filename;
	
	public static int population_size = 50;
	public static int max_generations = 50;
	public static double mutation_prob = 0.1;
	public static double crossover_prob = 0.6;
	public static int num_jobs;
	
	public static SMTWTP smtwtp;
	
	public static void main(String[] args) {
				
		readArguments(args);
		
		smtwtp = new SMTWTP(filename);
		num_jobs = smtwtp.getNum_jobs();
		int[][] best_eas_solutions = new int[population_size][num_jobs];
		
		System.out.println("Num ants: " + num_ants + ", num iterations: " + num_iterations + 
				", alpha: " + alpha + ", beta: " + beta + ", rho: " + 
				rho + ", elitism factor: " + elitism_factor + ", optimal: " + optimal + 
				", stop percent: " + stop_percent + ", filename: " + filename);
		
		EAS eas = new EAS(num_ants, num_iterations, alpha, beta, rho, elitism_factor, optimal, stop_percent, smtwtp);
		
		for (int i = 0; i < population_size; i++) {
			best_eas_solutions[i] = eas.runEAS();
		}
		
		GA genetic_algorithm = new GA(population_size, mutation_prob, max_generations, crossover_prob);
		genetic_algorithm.RunGA(best_eas_solutions, smtwtp);
	}
	
	public static void readArguments(String[] args) {
		
		try {
				num_ants = Integer.parseInt(args[0]);
				num_iterations = Integer.parseInt(args[1]);
				alpha = Double.parseDouble(args[2]);
				beta = Double.parseDouble(args[3]);
				rho = Double.parseDouble(args[4]);
				elitism_factor = Double.parseDouble(args[5]);
				optimal = Integer.parseInt(args[6]);
				stop_percent = Double.parseDouble(args[7]);
				filename = args[8];
		} catch(NullPointerException e) {
			System.out.println("Please verify your inputs and try again");
			System.exit(0);
		}
	}
}
