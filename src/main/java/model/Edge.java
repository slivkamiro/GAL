package model;

public class Edge {
	
	private Vertex out;
	private Vertex in;
	
	private int weight;
	
	public Edge(Vertex out, Vertex in) {
		this.out = out;
		this.in = in;
		
		in.addEdge(this);
		out.addEdge(this);
	}
	
	public Edge(Vertex out, Vertex in, int w) {
		this(out,in);
		weight = w;
	}
	
	public Vertex getOutVertex() {
		return out;
	}
	
	public Vertex getInVertex() {
		return in;
	}
	
	public int getWeight() {
		return weight;
	}

}
