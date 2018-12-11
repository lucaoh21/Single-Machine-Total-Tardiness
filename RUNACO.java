/*
 * Author Luca Osterag-Hill, Tom Lucy, Jake Rourke
 * Date 11/6/2018
 * 
 * This class is the main class for the ACO project. Given the user inputs,
 * the class calls either EAS or ACS, which do the iterative building.
 * 
 */

public class RUNACO {
	
	//the number of ants
	public static int num_ants;
	//the number of iterations
	public static int num_iterations;
	//the optimal cost of the tsp problem
	public static int optimal;
	//the degree of influence of the pheromone component
	public static double alpha;
	//the degree of influence of the heuristic component
	public static double beta;
	//the pheromone evaporation factor
	public static double rho;
	//the elitism factor for eas (often equal to num ants)
	public static double elitism_factor;
	public static double epsilon;
	//the base level of pheromone
	public static double base_tau;
	//probability to choose greedy selection in ACS
	public static double greedy_prob;
	//percentage of optimal to stop at
	public static double stop_percent;
	//the tsp problem
	public static String filename;
	//the algorithm to run 
	public static String algorithm;
	
	
	public static final String EAS = "eas";
	public static final String ACS = "acs";
	
	public static void main(String[] args) {
		
		readArguments(args);
		
		//if the user input specifies Elitist Ant System
		if (algorithm.equals(EAS)) {
			
			System.out.println("Num ants: " + num_ants + ", num iterations: " + num_iterations + 
					", alpha: " + alpha + ", beta: " + beta + ", rho: " + 
					rho + ", elitism factor: " + elitism_factor + ", stop percent: " + 
					stop_percent + ", optimal: " + optimal + ", filename: " + filename + ", algorithm: " + 
					algorithm);
			
			EAS eas = new EAS(num_ants, num_iterations, alpha, beta, rho, elitism_factor, stop_percent, optimal, filename);
			eas.runEAS();
		}
		
		//if the user input specifies Ant Colony System
		else if (algorithm.equals(ACS)) {
			
			System.out.println("Num ants: " + num_ants + ", num iterations: " + num_iterations + 
					", alpha: " + alpha + ", beta: " + beta + ", rho: " + 
					rho + ", epsilon: " + epsilon + ", base tau: " + 
					base_tau + ", greedy prob: " + greedy_prob + ", stop percent: " + 
					stop_percent + ", optimal: " + optimal + ", filename: " + filename + 
					", algorithm: " + algorithm);
			
			ACS acs = new ACS(num_ants, num_iterations, alpha, beta, rho, epsilon, base_tau, greedy_prob, stop_percent, optimal, filename);
			acs.runACS();
		}
	}
	
	public static void readArguments(String[] args) {
		
		algorithm = args[args.length-1];
		
		//set the arguments if the specified algorithm is eas
		if (algorithm.equals(EAS)) {
			try {
				num_ants = Integer.parseInt(args[0]);
				num_iterations = Integer.parseInt(args[1]);
				alpha = Double.parseDouble(args[2]);
				beta = Double.parseDouble(args[3]);
				rho = Double.parseDouble(args[4]);
				elitism_factor = Double.parseDouble(args[5]);
				stop_percent = Double.parseDouble(args[6]);
				optimal = Integer.parseInt(args[7]);
				filename = args[8];
			}
			catch(NullPointerException e) {
				System.out.println("Please verify your inputs and try again");
				System.exit(0);
			}
		}
		
		//set the arguments if the specified algorithm is acs
		else if (algorithm.equals(ACS)) {
			try {
				num_ants = Integer.parseInt(args[0]);
				num_iterations = Integer.parseInt(args[1]);
				alpha = Double.parseDouble(args[2]);
				beta = Double.parseDouble(args[3]);
				rho = Double.parseDouble(args[4]);
				epsilon = Double.parseDouble(args[5]);
				base_tau = Double.parseDouble(args[6]);
				greedy_prob = Double.parseDouble(args[7]);
				stop_percent = Double.parseDouble(args[8]);
				optimal = Integer.parseInt(args[9]);
				filename = args[10];
			}
			catch(NullPointerException e) {
				System.out.println("Please verify your inputs and try again");
				System.exit(0);
			}
		}
		
		else {
			System.out.println("Please input an algorithm of either eas of acs");
			System.exit(0);
		}
	}

}
