package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Graph;

public abstract class Algorithm extends Observable implements Runnable {

	/**
	 * Output graph variable.
	 */
	private GraphAdapter g;

	private Map<String,String> properties;

	public Algorithm() {
		properties = new HashMap<String,String>();
	}

	protected void addProperty(String name, String value) {
		properties.put(name, value);
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public boolean propertyExists(String name) {
		return properties.containsKey(name);
	}

	public List<String> getPropertiesName() {
		return new ArrayList<String>(properties.keySet());
	}

	public List<String> getPropertiesValue() {
		return new ArrayList<String>(properties.values());
	}

	public void run() {
		doStep();
		if (g != null) {
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * This should be called when new step was executed.
	 * @param g
	 */
	protected void setOutput(Graph g) {
		if(g != null) {
			this.g = new GraphAdapter(g);
			this.g.recomputeEdgesCoords();
		} else {
			this.g = null;
		}
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
