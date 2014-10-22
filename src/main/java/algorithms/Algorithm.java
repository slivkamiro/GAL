package algorithms;

import java.util.Observable;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Graph;

public abstract class Algorithm extends Observable implements Runnable {
	
	private GraphAdapter g;

	/**
	 * Override this.
	 */
	public void run() {
		// TODO Auto-generated method stub

	}
	
	public void setOutput(Graph g) {
		this.g = new GraphAdapter(g);
	}
	
	public GraphAdapter getGraph() {
		return g;
	}
	
	public abstract void setGraph(Graph g);

}
