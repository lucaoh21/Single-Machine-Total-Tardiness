/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 11/7/2018
 * 
 * This class defines an ANT object for use in Ant Colony Optimization on the TSP.
 * Each ANT object can hold a tour of cities, represented as an array of integers,
 * a score of that tour of cities, represented as a double, an integer number of
 * cities in the TSP problem instance, and an array of path distances between cities
 * in the problem instance, represented as a 2D array of doubles.
 * 
 */

public class ANT {
	
	int[] tour;
	double tour_score;
	int num_cities;
	double[][] legDistances;
	
	/* Purpose: Initialize an ANT object
	 * Parameters: A given TSP object and an integer number of cities
	 * Return: N/A
	 */
	public ANT(int num_cities, TSP tsp) {
		
		this.num_cities = num_cities;
		this.tour = new int[num_cities];
		this.legDistances = tsp.getTsp_distance();
		tour_score = Double.MAX_VALUE;
	}
	
	/* Purpose: Score the ANT's tour
	 * Parameters: None
	 * Return: None
	 */
	public void scoreTour() {
		
		int city1, city2;
		tour_score = 0;
		
		for (int i = 0; i < num_cities-1; i++) {
			city1 = tour[i];
			city2 = tour[i+1];
			if (city1 < city2) {
				tour_score += legDistances[city2][city1];
			}
			else {
				tour_score += legDistances[city1][city2];
			}
		}
	}

	/*
	 * Getters and Setters
	 */

	public int[] getTour() {
		return tour;
	}

	public void setTour(int[] tour) {
		this.tour = tour.clone();
	}

	public double getTour_score() {
		return tour_score;
	}

	public void setTour_score(double tour_score) {
		this.tour_score = tour_score;
	}
	

}
