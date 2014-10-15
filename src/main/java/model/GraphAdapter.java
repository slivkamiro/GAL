package model;

import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class GraphAdapter{
	
	
	private Graph graph;
	
	private Integer vId;
	private Integer eId;
	
	public GraphAdapter() {
		vId = 1;
		eId = 1;
		graph = new TinkerGraph();
	}
	
	public VertexAdapter addVertex() {
		return new VertexAdapter(graph.addVertex(String.valueOf(vId++)));
	}
	
	public VertexAdapter addVertex(String id) {
		return new VertexAdapter(graph.addVertex(id));
	}
	
	public EdgeAdapter addEdge(VertexAdapter out, VertexAdapter in) {
		Edge e = graph.addEdge(String.valueOf(eId++), out.getVertex(), in.getVertex(), "1");
		e.setProperty("weight", "1");
		return new EdgeAdapter(e);
	}

	public List<VertexAdapter> getVertices() {
		List<VertexAdapter> vertices = new ArrayList<VertexAdapter>();
		for(Vertex v : graph.getVertices()) {
			vertices.add(new VertexAdapter(v));
		}
		return vertices;
	}

	public void removeVertex(VertexAdapter v) {
		graph.removeVertex(v.getVertex());
		
	}


	public EdgeAdapter getEdge(VertexAdapter v1, VertexAdapter v2) {
		for(Edge e : v1.getVertex().getEdges(Direction.OUT, "1")) {
			if(e.getVertex(Direction.IN).equals(v2.getVertex()))
				return new EdgeAdapter(e);
		}
		return null;
	}

	public void removeEdge(EdgeAdapter e) {
		graph.removeEdge(e.getEdge());
		
	}

}
