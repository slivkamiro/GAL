package com.slivkam.graphdemonstrator.algorithms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.slivkam.graphdemonstrator.model.GraphAdapter;
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
    protected GraphAdapter graph;

    private Map<String,String> properties;

    private List<Integer> edgeIds;

    private List<Integer> vertexIds;

    /**
     * Default constructor.
     */
    public Algorithm() {
        this.properties = new LinkedHashMap<String,String>();
        this.edgeIds = new ArrayList<Integer>();
        this.vertexIds = new ArrayList<Integer>();
    }

    /**
     * Adding some property that algorithm wants to be published alongside with graph.
     * @param name Property name.
     * @param value Property value.
     */
    protected void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    /**
     * Retrieve property by name.
     * @param name Name of the property.
     * @return Property value.
     */
    public String getProperty(String name) {
        return this.properties.get(name);
    }

    /**
     * Check if property with a name given exists.
     * @param name Name of the property.
     * @return true if property exists, false otherwise.
     */
    public boolean propertyExists(String name) {
        return this.properties.containsKey(name);
    }

    /**
     * Gets names of all properties.
     * @return List of names.
     */
    public List<String> getPropertiesName() {
        return new ArrayList<String>(this.properties.keySet());
    }

    /**
     * Gets values of all properties.
     * @return List of values.
     */
    public List<String> getPropertiesValue() {
        return new ArrayList<String>(this.properties.values());
    }

    /**
     * Remove all properties. Names and values.
     */
    public void clearProperties() {
        this.properties.clear();
    }

    /**
     * This is called by executor.
     */
    @Override
    public void run() {
        this.doStep();
        if (this.graph != null) {
            this.setChanged();
            this.notifyObservers();
        }
    }

    protected void addToHistory(GraphAdapter g) {
        this.newInstanceOutput(new GraphAdapter(g));
    }

    /**
     * Sets graph to be published. This should be called when new step was executed.
     * @param g Graph to be published.
     */
    protected void addToHistory(Graph g) {
        this.newInstanceOutput(new GraphAdapter(g));
    }

    protected void endAlgorithm() {
        this.graph = null;
    }

    private void newInstanceOutput(GraphAdapter g) {
        this.graph = g;
        this.graph.highlightEdges(this.edgeIds.toArray(new Integer[0]));
        this.graph.highlightVertices(this.vertexIds.toArray(new Integer[0]));
    }

    public void highlightEdge(int edgeId) {
        this.edgeIds.add(edgeId);
    }

    public void highlightVertex(int vertexId) {
        this.vertexIds.add(vertexId);
    }

    public GraphAdapter getGraph() {
        return this.graph;
    }

    /**
     * This method sets graph that should be algorithm performed on.
     * @param g
     */
    public void setGraph(GraphAdapter g) {
        this.graph = g;
        this.init();
    }

    public abstract void init();

    /**
     * This method performs one step in algorithm.
     * In this method should be set new output graph.
     */
    protected abstract void doStep();
}
