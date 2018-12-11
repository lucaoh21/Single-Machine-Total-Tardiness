import java.util.Random;

public class GeneticAlgorithm {
	
	private String testFile;
	
    private int populationSize;
    private float mutationProb;
    private int numGenerations;

    private String selectionType;
    private String crossoverMethod;
    private float crossoverProb;

    private int numVariables;
    private int numClauses;
    
    public GeneticAlgorithm() {}

	public void RunGA(String[] args, int[][] clauses, int numVariables, int numClauses) {
		
		Random rand = new Random();
		
		this.numVariables = numVariables;
		this.numClauses = numClauses;
		
		ReadArguments(args);
		
		Population population = new Population(numVariables, populationSize);
		Population newPopulation = new Population(populationSize);
		
		Individual[] parents = new Individual[2];
		Individual[] children = new Individual[2];
		
		FitNode[] fitnessArray;
		FitNode best;
		
		int count;
		
		for (int i = 0; i < numGenerations; ++i) {
			
			count = 0;
			
			fitnessArray = GenerateFitnessArray(population, clauses);
			if (numClauses - fitnessArray[populationSize-1].GetFitness() == 0) {
				algorithmDone(population.GetIndividual(fitnessArray[populationSize-1].GetPopIndex()), 
						fitnessArray[populationSize-1].GetFitness(), i + 1);
			}
			
			if (i % 50 == 0) {
				best = fitnessArray[populationSize-1];
				System.out.println("Generation " + i + ": " + "Best fitness so far is " + best.GetFitness() + 
						", Assignment is " + population.GetIndividual(best.GetPopIndex()).GetAssignments());
			}
		
			if (crossoverMethod.equals("1c")) {
				
				if (selectionType.equals("rs")) {
					
					while (count < populationSize) {
						
						parents = rankSelection(population, fitnessArray, clauses);
						
						if (rand.nextDouble() < crossoverProb) {
							children = onePointCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}
				
				else if (selectionType.equals("ts")) {
					
					while (count < populationSize) {
						
						parents = tournamentSelection(population, fitnessArray, clauses);
						
						if (rand.nextDouble() < crossoverProb) {
							children = onePointCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}
				
				else if (selectionType.equals("bs")) {
					
					while (count < populationSize) {
						
						parents = boltzmannSelection(population, fitnessArray, clauses);
						
						if (rand.nextDouble() < crossoverProb) {
							children = onePointCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}	
			}
			
			else if (crossoverMethod.equals("uc")) {
				
				if (selectionType.equals("rs")) {
					
					while (count < populationSize) {
						
						parents = rankSelection(population, fitnessArray, clauses);
						
						if (rand.nextDouble() < crossoverProb) {
							children = uniformCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}
				
				else if (selectionType.equals("ts")) {
					
						while (count < populationSize) {
						
						parents = tournamentSelection(population, fitnessArray, clauses);
						
						if (rand.nextDouble() < crossoverProb) {
							children = uniformCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}
				
				else if (selectionType.equals("bs")) {

					while (count < populationSize) {

						parents = boltzmannSelection(population, fitnessArray, clauses);

						if (rand.nextDouble() < crossoverProb) {
							children = uniformCrossover(parents[0], parents[1]);
						}
						else {
							children[0] = parents[0];
							children[1] = parents[1];
						}
						
						newPopulation.SetIndividual(children[0], count);
						++count;
						if (count < populationSize) {
							newPopulation.SetIndividual(children[1], count);
							++count;
						}
					}
					population = mutation(newPopulation);
				}	
			}
		}
		
		// find and report best individual after all generations
		fitnessArray = GenerateFitnessArray(population, clauses);
        algorithmDone(population.GetIndividual(fitnessArray[populationSize-1].GetPopIndex()), 
        		fitnessArray[populationSize-1].GetFitness(), numGenerations-1);
		
	}
	
	// generate and sort an array of fitnesses
	public FitNode[] GenerateFitnessArray(Population population, int[][] clauses) {
		
		FitNode[] fitnessArray = new FitNode[populationSize];
		FitNode indieFit, temp;
		Individual indie;
		int fitness;
		
		// create fitness nodes
		for (int i = 0; i < populationSize; ++i) {
			indie = population.GetIndividual(i);
			fitness = fitnessOf(indie, clauses);
			indieFit = new FitNode(i, fitness);
			fitnessArray[i] = indieFit;
			
		}
		
		// sort fitness array
		for (int j = 0; j < populationSize; ++j) {
			for (int k = j+1; k < populationSize; ++k) {
				if (fitnessArray[j].GetFitness() > fitnessArray[k].GetFitness()) {
					temp = fitnessArray[k];
					fitnessArray[k] = fitnessArray[j];
					fitnessArray[j] = temp;
				}
			}
		}
		
		return fitnessArray;
	}
	
	// calculate the fitness of an individual
	public int fitnessOf(Individual indie, int[][] clauses){

        int fitness = 0;

        for (int i = 0; i < numClauses; ++i) {
            // break out of clause once a single satisfying assignment found
            for (int j = 0; j < clauses[i].length; ++j) {
                int variableIndex = Math.abs(clauses[i][j])-1;
                // if not, need false to satisfy
                if (clauses[i][j] < 0) {
                    if (indie.GetVariable(variableIndex) == '0') {
                   	 ++fitness;
                        break;
                    }
                }
                // need true to satisfy
                else {
                    if (indie.GetVariable(variableIndex) == '1') {
                   	 ++fitness;
                        break;
                    }
                }
            }
        }

        return fitness;
    }
	
	//return two individuals that have undergone rank selection
    public Individual[] rankSelection(Population pop, FitNode[] fitnessArray, int[][] clauses) {

    	Random rand = new Random();
    	
    	double index, sumRanks = 0, count = 0;
    	double prob;
        Individual[] selected = new Individual[2];
    	
    	for (int x = 0; x < populationSize; ++x) {
    		sumRanks += x;
    	}
    	
    	while (count < 2) {
        	index = rand.nextInt(populationSize);
        	prob = rand.nextDouble();

    		if (prob < (index/sumRanks)) {
    			selected[(int)count] = pop.GetIndividual(fitnessArray[(int)index].GetPopIndex());
    			++count;
    		}
    	}
        return selected;
    }
    
    // return two individuals that have undergone tournament selection
    public Individual[] tournamentSelection(Population pop, FitNode[] fitnessArray, int[][] clauses) {

        Random rand = new Random();

        int count = 0;
        int index1, index2, fit1, fit2;
        Individual one, two;
        Individual[] selected = new Individual[2];

        while (count < 2) {
        	index1 = (int)(populationSize * rand.nextDouble());
        	index2 = (int)(populationSize * rand.nextDouble());

        	if (index1 != index2) {
        	
        		one = pop.GetIndividual(fitnessArray[index1].GetPopIndex());
        		two = pop.GetIndividual(fitnessArray[index2].GetPopIndex());
        	
        		fit1 = fitnessArray[index1].GetFitness();
        		fit2 = fitnessArray[index2].GetFitness();
        	
        		if (fit1 > fit2) {
        			selected[count] = one;
        			++count;
        		}
        		else if (fit2 >= fit1) {
        			selected[count] = two;
        			++count;
        		}
        	}
        }
        return selected;
    }
    
    // return two individuals that have undergone tournament selection
    public Individual[] boltzmannSelection(Population pop, FitNode[] fitnessArray, int[][] clauses){

        Random rand = new Random();

        int popIndex, fitness, index, count = 0;
        double prob, expFit, sumOfProbs = 0.0;
        double[] relativeArray = new double[populationSize];
        Individual[] selected = new Individual[2];
        
        for (int i = 0; i < populationSize; ++i) {
        	popIndex = fitnessArray[i].GetPopIndex();
        	fitness = fitnessArray[i].GetFitness();
        	expFit = Math.exp(fitness/10);
        	relativeArray[popIndex] = expFit;
        }

        for(int i = 0; i < relativeArray.length; i++){
            sumOfProbs += relativeArray[i];
        }
        
        while(count < 2){

        	index = rand.nextInt(populationSize);
            prob = rand.nextDouble();

            if (prob < (relativeArray[index]/sumOfProbs)) {
            	selected[count] = pop.GetIndividual(index);
            	++count;
            }
        }
        return selected;
    }
	
    // returns an array of two new individuals made through one point crossover
    public Individual[] onePointCrossover(Individual p1,Individual p2){
    	
    	Random rand = new Random();
    	
    	Individual[] childrenArray = new Individual[2];
    	String c1p1, c1p2, c2p1, c2p2;
    	
        String parent1Assignments = p1.GetAssignments();
        String parent2Assignments = p2.GetAssignments();
        
        int length = parent1Assignments.length();
        int crossPoint = rand.nextInt(length);

        c1p1 = parent1Assignments.substring(0, crossPoint);
        c1p2 = parent1Assignments.substring(crossPoint);
        
        c2p1 = parent2Assignments.substring(0, crossPoint);
        c2p2 = parent2Assignments.substring(crossPoint);
        
        Individual c1 = new Individual(c1p1+c2p2);
        Individual c2 = new Individual(c2p1+c1p2);
        
        childrenArray[0] = c1;
        childrenArray[1] = c2;
        
       return childrenArray;
    }
	
    // returns an array of two new individuals made through uniform crossover
    public Individual[] uniformCrossover(Individual p1, Individual p2){
    	
        Random rand = new Random();
        
        String firstParentAssignments = p1.GetAssignments();
        String secondParentAssignments = p2.GetAssignments();
        
        int length = firstParentAssignments.length();
        
        char[] arr1 = firstParentAssignments.toCharArray();
        char[] arr2 = secondParentAssignments.toCharArray();
        
        char[] child = new char[numVariables];
        
        // with 50/50 chance, either assign children to have char of c1 or char of c2
        for(int i = 0; i < length; i++){ //
            if(rand.nextInt(2) == 1){
                child[i] = arr1[i];
            }
            else {
            	child[i] = arr2[i];
            }
        }
        
        String builder1 = "";
        String builder2 = "";
        
        for(int i = 0; i < length; i++){
            builder1 += child[i];
            builder2 += child[i];
        }
        
        Individual[] childrenArray = new Individual[2];
        
        Individual c1 = new Individual(builder1);
        Individual c2 = new Individual(builder2);

        childrenArray[0] = c1;
        childrenArray[1] = c2;

        return childrenArray;

    }
    
    // potentially mutate each characteristic for each individual in population
    public Population mutation(Population pop){
    	
        Random rand = new Random();
        
        Population newPop = new Population(populationSize);
        char[] indAssignment = new char[numVariables];
        
        for (int i = 0; i < populationSize; i++) {
            indAssignment = pop.GetIndividual(i).GetAssignments().toCharArray();
            for (int j = 0; j < indAssignment.length; j++){
                if (rand.nextDouble() <= mutationProb) {
                   if (indAssignment[j] == '0') {
                        indAssignment[j] = '1';
                    } else {
                        indAssignment[j] = '0';
                    }
                }
            }
            
            Individual ind = pop.GetPopulation()[i];
            ind.SetAssignments(String.valueOf(indAssignment));
            newPop.SetIndividual(ind, i);
        }
        return newPop;
    }
    
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
}
