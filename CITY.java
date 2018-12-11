/*
 * Authors: Luca Ostertag-Hill, Tom Lucy, Jake Rourke
 * Date: 11/7/2018
 * 
 * This class defines a CITY object for use in Ant Colony Optimization on the TSP.
 * Each CITY represents a city node as described in a TSP problem instances. For
 * our purposes, all cities are connected to each other, so there is no need to
 * city track of city connections. As such, each CITY object contains an integer
 * city ID number, a double value of the CITY's x-coordinate, and a double value
 * of the CITY's y-coordinate.
 * 
 */

public class CITY {
	
	int number;
	double x_coor, y_coor;
	
	/* Purpose: Initialize a CITY object
	 * Parameters: The city's integer ID number, a double x-coordinate,
	 * and a double y-coordinate
	 * Return: N/A
	 */
	public CITY(int number, double x_coor, double y_coor) {

		this.number = number - 1;
		this.x_coor = x_coor;
		this.y_coor = y_coor;
	}

	/*
	 * Getters and Setters
	 */
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getX_coor() {
		return x_coor;
	}

	public void setX_coor(double x_coor) {
		this.x_coor = x_coor;
	}

	public double getY_coor() {
		return y_coor;
	}

	public void setY_coor(double y_coor) {
		this.y_coor = y_coor;
	}

}
