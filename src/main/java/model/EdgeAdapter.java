package model;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

public class EdgeAdapter {
	
	private Edge e;
	
	public EdgeAdapter(Edge e) {
		this.e = e;
	}
	
	public Edge getEdge() {
		return e;
	}
	
	public VertexAdapter getOutVertex() {
		return new VertexAdapter(e.getVertex(Direction.OUT));
	}
	
	public VertexAdapter getInVertex() {
		return new VertexAdapter(e.getVertex(Direction.IN));
	}
	
	public String getWeight() {
		return e.getProperty("weight").toString();
	}

}
