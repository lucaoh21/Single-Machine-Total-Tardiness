/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 11/7/2018
 * 
 * This class defines a TSP object for use in Ant Colony Optimization on the TSP.
 * A TSP object contains all of the defining information for a specific instance
 * of the TSP problem. Therefore, included in each object is an integer number of
 * cities, all of the cities in the problem, represented as an array of CITY objects,
 * the pheromone levels on each connection between cities, represented as a 2D array
 * of doubles, the distances between cities, represented as a 2D array of doubles, and
 * the numerator of the probability of each path's selection, represented as a 2D array
 * of doubles.
 * 
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class TSP {
	
	int num_cities;
	CITY[] tsp_instance;
	double[][] tsp_pheromone;
	double[][] tsp_distance;
	double[][] tsp_value;
	
	//headers for different sections of the TSP file
	public static final String NUM_CITY_SECTION = "DIMENSION";
	public static final String COORDINATES_SECTION = "NODE_COORD_SECTION";
	public static final String END_OF_COORDS_SECTION = "EOF";

	
	/* Purpose: Read in the tsp problem file and initialize a TSP object
	 * Parameters: A String filename for the tsp problem file
	 * Return: None
	 */
	public TSP(String filename) {
		
		File tspFile = new File(filename);
		
		// read in the file
		try{
			String[] line;
			int counter = 0;
			boolean coord_section = false;
			Scanner sc = new Scanner(tspFile);
			
			while(sc.hasNextLine()) {

				// get an array for each line

				line = sc.nextLine().trim().split("\\s+");
				
				// parse the number of cities from the problem file
				if (line[0].equals(NUM_CITY_SECTION)) {
					num_cities = Integer.parseInt(line[line.length-1]);
					tsp_instance = new CITY[num_cities];
				}
				
				// end on the last line of the file
				else if (line[0].equals(END_OF_COORDS_SECTION)) {
					coord_section = false;
				}
				
				// create a CITY object if the line has a city's data
				else if (coord_section == true) {
										
					CITY city = new CITY(Integer.parseInt(line[0]), 
							Double.parseDouble(line[1]), Double.parseDouble(line[2]));
					tsp_instance[counter] = city;
					counter++;
				}
				
				// signal the start of the city data section
				else if (line[0].equals(COORDINATES_SECTION)) {
					coord_section = true;
				}
			}
			sc.close();
			
			this.tsp_distance = new double[num_cities][num_cities];
			this.tsp_pheromone = new double[num_cities][num_cities];
			this.tsp_value = new double[num_cities][num_cities];
		}
		
		catch(FileNotFoundException e) {
			System.out.println(e);
			System.out.println("TSP instance file not found");
			System.exit(0);
		}
	}
	
	/* Purpose: Initialize the pheromone array to base tau
	 * Parameters: A double value of the base pheromone
	 * Return: None
	 * 
	 * Note: Only initalize the top right half of the 2D
	 * array as the problems are symmetric
	 */
	public void initializePheromone(double base_tau) {
		
		for(int i = 0; i < num_cities; i++) {
			for(int j = 0; j < i; j++) {
				tsp_pheromone[i][j] = base_tau;
			}
		}
	}
	
	/* Purpose: Initialize the distance array
	 * Parameters: None
	 * Return: None
	 * 
	 * Note: Only initialize the top right half of the 2D
	 * array as the problems are symmetric
	 */
	public void initializeDistances() {
		
		double x_term, y_term;
		CITY city1, city2;
		
		for(int i = 0; i < num_cities; i++) {
			city1 = tsp_instance[i];
			for(int j = 0; j < i; j++) {
				city2 = tsp_instance[j];
				x_term = Math.pow(city2.getX_coor() - city1.getX_coor(), 2.0);
				y_term = Math.pow(city2.getY_coor() - city1.getY_coor(), 2.0);
								
				tsp_distance[i][j] = Math.sqrt(x_term + y_term);
			}
		}
	}
	
	/* Purpose: Increase the pheromone level on a certain path
	 * Parameters: An integer value of city 1, an integer value
	 * of city 2, and a double value of the desired pheromone increase
	 * Return: None
	 */
	public void increasePheromone(int i, int j, double pheromone) {
		
		tsp_pheromone[i][j] += pheromone;
	}
	
	/* Purpose: Evaporate the pheromone level on a certain path
	 * Parameters: An integer value of city 1, an integer value
	 * of city 2, and a double value of the evaporation factor
	 * Return: None
	 */
	public void evaporatePheromone(int i, int j, double rho) {
		
		tsp_pheromone[i][j] *= 1.0 - rho;
	}
	
	/* Purpose: Caluclaute the numerator of the probability of
	 * selecting a given path based on its pheromone levels
	 * Parameters: An double value of alpha and a double value
	 * of beta
	 * Return: None
	 */
	public void calculateValue(double alpha, double beta) {
		double distance, pheromone;
		
		for(int i = 0; i < num_cities; i++) {
			for(int j = 0; j < i; j++) {
				pheromone = tsp_pheromone[i][j];
				distance = 1 / tsp_distance[i][j];
				tsp_value[i][j] = Math.pow(pheromone, alpha) * Math.pow(distance, beta);
				
			}
			
		}
	}

	/*
	 * Getters and Setters
	 */


	public int getNum_cities() {
		return num_cities;
	}

	public void setNum_cities(int num_cities) {
		this.num_cities = num_cities;
	}

	public CITY[] getTsp_instance() {
		return tsp_instance;
	}

	public void setTsp_instance(CITY[] tsp_instance) {
		this.tsp_instance = tsp_instance.clone();
	}

	public double[][] getTsp_pheromone() {
		return tsp_pheromone;
	}

	public void setTsp_pheromone(double[][] tsp_pheromone) {
		this.tsp_pheromone = tsp_pheromone.clone();
	}

	public double[][] getTsp_distance() {
		return tsp_distance;
	}

	public void setTsp_distance(double[][] tsp_distance) {
		this.tsp_distance = tsp_distance.clone();
	}

	public double[][] getTsp_value() {
		return tsp_value;
	}

	public void setTsp_value(double[][] tsp_value) {
		this.tsp_value = tsp_value;
	}
}
