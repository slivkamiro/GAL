package model;

import java.awt.Point;
import java.awt.geom.Point2D;
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
			// some deviation given
			if(Math.pow(p.x-x,2)+Math.pow(p.y-y, 2) <= 110)
				return v;
		}
		return null;
	}

	public void removeVertex(Vertex v) {
		vertices.remove(v);
		
	}

	public Vertex getVertexOnPosition(Point2D point) {
		Point p = new Point();
		p.x = (int)point.getX();
		p.y = (int)point.getY();
		return getVertexOnPosition(p);
	}

	public Edge getEdge(Vertex v1, Vertex v2) {
		for(Edge e : edges) {
			if(e.getOutVertex() == v1 && e.getInVertex() == v2) 
				return e;
		}
		return null;
	}

}
