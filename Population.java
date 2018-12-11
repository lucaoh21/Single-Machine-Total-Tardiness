
public class Population {
	
	private Individual[] population;
	
	public Population (int populationSize) {
		population = new Individual[populationSize];
	}
	
	public Population (int numVariables, int populationSize) {
		
		population = new Individual[populationSize];
		
		for (int i = 0; i < populationSize; ++i) {
			population[i] = new Individual(numVariables);
		}
	}
	
	public Population (Individual[] individuals) {
		population = individuals;
	}
	
	public Individual[] GetPopulation() {
		return population;
	}
	
	public void SetPopulation(Individual[] population) {
		this.population = population;
	}
	
	public Individual GetIndividual(int index) {
		return population[index];
	}
	
	public void SetIndividual(Individual individual, int index) {
		population[index] = individual;
	}
	
	public void AddIndividual(Individual individual) {
		for (int i = 0; i < population.length; ++i) {
			if (population[i] == null) {
				population[i] = individual;
				break;
			}
		}
	}
	
	public int GetSize() {
		return population.length;
	}

}
