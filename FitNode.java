
public class FitNode {

	int popIndex;
	int fitness;
	
	public FitNode() {
		popIndex = 0;
		fitness = 0;
	}
	
	public FitNode(int popIndex, int fitness) {
		this.popIndex = popIndex;
		this.fitness = fitness;
	}
	
	public int GetPopIndex() {
		return popIndex;
	}
	
	public int GetFitness() {
		return fitness;
	}
	
	public void SetPopIndex(int popIndex) {
		this.popIndex = popIndex;
	}
	
	public void SetFitness(int fitness) {
		this.fitness = fitness;
	}
	
}
