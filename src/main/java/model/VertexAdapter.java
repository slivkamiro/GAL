package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class VertexAdapter {

	private Vertex v;
	
	public VertexAdapter(Vertex v) {
		this.v = v;
	}
	
	public Vertex getVertex() {
		return v;
	}
	
	public void addEdge(EdgeAdapter e) {
		v.addEdge("1", e.getInVertex().getVertex());
	}
	
	public void setAttribute(String name, String value) {
		v.setProperty(name,value);
	}
	
	public String getAttribute(String name) {
		return v.getProperty(name).toString();
	}

	public String getId() {
		return (String) v.getId();
	}

	public Map<String,String> getAttributes() {
		Map<String,String> attributes = new HashMap<String,String>();
		for(String key : v.getPropertyKeys()) {
			attributes.put(key, v.getProperty(key).toString());
		}
		return attributes;
	}

	public List<EdgeAdapter> getEdges() {
		List<EdgeAdapter> edges = new ArrayList<EdgeAdapter>();
		for(Edge e : v.getEdges(Direction.BOTH, "1")) {
			edges.add(new EdgeAdapter(e));
		}
		return edges;
	}
}
