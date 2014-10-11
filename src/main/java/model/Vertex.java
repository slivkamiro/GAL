package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vertex {

	private List<Edge> edges;
	
	private String id;
	
	private Map<String,String> attributes;
	
	public Vertex(String id) {
		edges = new ArrayList<Edge>();
		attributes = new HashMap<String,String>();
		
		this.setId(id);
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void setAttribute(String name, String value) {
		attributes.put(name,value);
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
