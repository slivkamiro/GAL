package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	private List<Vertex> vertices;
	private List<Edge> edges;
	
	private String id;
	
	private int vId;
	
	public Graph(String id) {
		vId = 1;
		this.id = id;
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
	}
	
	public void setGraphId(String id) {
		this.id = id;
	}
	
	public Vertex addVertex() {
		return this.addVertex(String.valueOf(vId++));
	}
	
	public Vertex addVertex(String id) {
		Vertex v = new Vertex(id);
		vertices.add(v);
		return v;
	}
	
	public Edge addEdge(Vertex out, Vertex in) {
		Edge e = new Edge(out,in);
		edges.add(e);
		return e;
	}

	public Vertex getVertexOnPosition(Point p) {
		for(Vertex v : vertices) {
			int x = Integer.parseInt(v.getAttribute("PositionX"));
			int y = Integer.parseInt(v.getAttribute("PositionY"));
			if((p.x >= x-20 && p.x <= x+20) &&
					(p.y >= y-20 && p.y <= y+20))
				return v;
		}
		return null;
	}

}
