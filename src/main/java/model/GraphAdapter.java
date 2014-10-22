package model;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import views.CanvasObject;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

public class GraphAdapter{
	
	
	private Graph graph;
	
	private Integer vId;
	private Integer eId;
	
	public GraphAdapter() {
		vId = 1;
		eId = 1;
		graph = new TinkerGraph();
	}
	
	public GraphAdapter(Graph g) {
		graph = g;
		// Does it matter if ids are redundant
		vId = 100;
		eId = 100;
	}
	
	public VertexAdapter addVertex() {
		return new VertexAdapter(graph.addVertex(String.valueOf(vId++)));
	}
	
	public VertexAdapter addVertex(String id) {
		return new VertexAdapter(graph.addVertex(id));
	}
	
	public EdgeAdapter addEdge(VertexAdapter out, VertexAdapter in) {
		EdgeAdapter e = new EdgeAdapter(
				graph.addEdge(String.valueOf(eId++), out.getVertex(), in.getVertex(), "1"));
		e.setWeight("1");
		return e;
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

	public void read(File f) throws IOException {
		GraphMLReader reader = new GraphMLReader(graph);
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		reader.inputGraph(is);
	}

	public void write(File f) throws IOException {
		GraphMLWriter writer = new GraphMLWriter(graph);
	    writer.outputGraph(f.getAbsolutePath());
		
	}

	public List<CanvasObject> getAll() {
		List<CanvasObject> o = new ArrayList<CanvasObject>();
		for(VertexAdapter v : getVertices()) {
			o.add(v);
		}
		for(Edge e : graph.getEdges()) {
			EdgeAdapter ea = new EdgeAdapter(e);
			int x1 = Integer.parseInt(e.getProperty("startX").toString());
			int y1 = Integer.parseInt(e.getProperty("startY").toString());
			int x2 = Integer.parseInt(e.getProperty("endX").toString());
			int y2 = Integer.parseInt(e.getProperty("endY").toString());
			ea.setPoints(new Point(x1,y1), new Point(x2,y2));
			ea.setLabel(e.getProperty("weight").toString());
			o.add(ea);
		}
		return o;
	}
	
	public Graph getGraph() {
		return graph;
	}

}