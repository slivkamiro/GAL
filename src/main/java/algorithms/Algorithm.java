package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Graph;

/**
 *
 * @author Miroslav
 *
 */
public abstract class Algorithm extends Observable implements Runnable {

	/**
	 * Output graph variable.
	 */
	private GraphAdapter g;

	private Map<String,String> properties;

	/**
	 * Default constructor.
	 */
	public Algorithm() {
		properties = new HashMap<String,String>();
	}

	/**
	 * Adding some property that algorithm wants to be published alongside with graph.
	 * @param name Property name.
	 * @param value Property value.
	 */
	protected void addProperty(String name, String value) {
		properties.put(name, value);
	}

	/**
	 * Retrieve property by name.
	 * @param name Name of the property.
	 * @return Property value.
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Check if property with a name given exists.
	 * @param name Name of the property.
	 * @return true if property exists, false otherwise.
	 */
	public boolean propertyExists(String name) {
		return properties.containsKey(name);
	}

	/**
	 * Gets names of all properties.
	 * @return List of names.
	 */
	public List<String> getPropertiesName() {
		return new ArrayList<String>(properties.keySet());
	}

	/**
	 * Gets values of all properties.
	 * @return List of values.
	 */
	public List<String> getPropertiesValue() {
		return new ArrayList<String>(properties.values());
	}

	/**
	 * Remove all properties. Names and values.
	 */
	public void clearProperties() {
		properties.clear();
	}

	/**
	 * This is called by executor.
	 */
	@Override
	public void run() {
		doStep();
		if (g != null) {
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Sets graph to be published. This should be called when new step was executed.
	 * @param g Graph to be published.
	 */
	protected void setOutput(Graph g) {
		if(g != null) {
			this.g = new GraphAdapter(g);
			this.g.recomputeEdgesCoords();
		} else {
			this.g = null;
		}
	}

	/**
	 * Sets graph to be published. This should be called when new step was executed.
	 * @param g Graph to be published.
	 */
	protected void setOutput(GraphAdapter g) {
		this.g = g;
		if(this.g != null) {
			this.g.recomputeEdgesCoords();
		}
	}

	public GraphAdapter getGraph() {
		return g;
	}

	/**
	 * This method sets graph that should be algorithm performed on.
	 * @param g
	 */
	public abstract void setGraph(GraphAdapter g);

	/**
	 * This method performs one step in algorithm.
	 * In this method should be set new output graph.
	 */
	protected abstract void doStep();
}
