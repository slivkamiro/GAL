package algorithms;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmManager {

	private List<Algorithm> algorithms;

	public AlgorithmManager() {
		algorithms = new ArrayList<Algorithm>();
		algorithms.add(new ChuLiuEdmonds());
		// New algorithms have to be added here
	}

	public List<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public Algorithm getAlgorithm(String name) {
		for(Algorithm a : algorithms) {
			if(a.toString().equals(name))
				return a;
		}
		return null;
	}
}
