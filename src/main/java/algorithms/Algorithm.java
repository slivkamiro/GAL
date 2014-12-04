package algorithms;

import java.util.Observable;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Graph;

public abstract class Algorithm extends Observable implements Runnable {

	/**
	 * Output graph variable.
	 */
	private GraphAdapter g;

	public void run() {
		doStep();
		setChanged();
		notifyObservers();
	}

	protected void setOutput(Graph g) {
		this.g = new GraphAdapter(g);
	}

	protected void setOutput(GraphAdapter g) {
		this.g = g;
	}

	public GraphAdapter getGraph() {
		return g;
	}

	/**
	 * This method sets graph that should be algorithm performed on.
	 * @param g
	 */
	public abstract void setGraph(Graph g);

	/**
	 * This method performs one step in algorithm.
	 * In this method you should set new output graph.
	 */
	protected abstract void doStep();
}
