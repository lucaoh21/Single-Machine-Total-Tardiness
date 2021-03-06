import java.util.ArrayList;
import java.util.Collections;

/*
 * Author Luca Osterag-Hill, Tom Lucy, Jake Rourke
 * Date 12/15/2018
 * 
 * This class is the main class for the HYBRID project. Given the user inputs,
 * the class calls either GA, EAS, or the hybrid, which do the iterative building.
 * 
 */

public class SMTWTP_HYBRID {
	
	public static int num_iterations;
	public static int max_generations;
	//the problem
	public static String filename;
	//algorithm to run (options are "eas" "ga" "both")
	public static String command;
	
	public static String ACO = "eas";
	public static String GA = "ga";
	public static String HYBRID = "both";
	
	public static int num_ants = 50;
	public static double alpha = 1.0;
	public static double beta = 6.0;
	public static double rho = 0.001;
	public static double elitism_factor = 100;
		
	public static int population_size = 100;
	public static double mutation_prob = 0.6;
	public static double crossover_prob = 0.9;
	
	
	public static int num_jobs;
	
	public static SMTWTP smtwtp;
	
	public static void main(String[] args) {
				
		readArguments(args);
		
		smtwtp = new SMTWTP(filename);
		num_jobs = smtwtp.getNum_jobs();
		int[][] best_eas_solutions = new int[population_size][num_jobs];
		
		//if just the ACO algorithm is selected
		if(command.equals(ACO)) {
			
			EAS eas = new EAS(num_ants, num_iterations, alpha, beta, rho, elitism_factor, smtwtp);
			
			for (int i = 0; i < population_size; i++) {
				best_eas_solutions[i] = eas.runEAS();
			}
			
			System.out.println(eas.getBest_all_time());
			
		}
		
		//if just the GA algorithm is selected
		else if(command.equals(GA)) {
			
			//create a randomized workflow
			for (int i = 0; i < population_size; i++) {
				
				ArrayList<Integer> a = new ArrayList<>(11);
				for (int j = 0; j < num_jobs; j++){                           
				    a.add(j);
				}
				Collections.shuffle(a);
				
				int[] arr = new int[num_jobs];
				for(int j = 0; j < num_jobs; j++) {
					arr[j] = a.get(j);
				}
				best_eas_solutions[i] = arr.clone();
				
			}
			
			GA genetic_algorithm = new GA(population_size, mutation_prob, max_generations, crossover_prob);
			genetic_algorithm.RunGA(best_eas_solutions, smtwtp);
			
		}
		
		//if the hybrid algorithm is selected
		else if(command.equals(HYBRID)) {
			EAS eas = new EAS(num_ants, num_iterations, alpha, beta, rho, elitism_factor, smtwtp);
			
			for (int i = 0; i < population_size; i++) {
				best_eas_solutions[i] = eas.runEAS();
			}
			
			System.out.print(eas.getBest_all_time() + "	");
		
			//use workflow of the best solutions found in EAS
			GA genetic_algorithm = new GA(population_size, mutation_prob, max_generations, crossover_prob);
			genetic_algorithm.RunGA(best_eas_solutions, smtwtp);
			
		}
	}
	
	
	public static void readArguments(String[] args) {
		
		try {
				num_iterations = Integer.parseInt(args[0]);
				max_generations = Integer.parseInt(args[1]);
				filename = args[2];
				command = args[3];
				
		} catch(NullPointerException e) {
			System.out.println("Please verify your inputs and try again");
			System.exit(0);
		}
	}
}
