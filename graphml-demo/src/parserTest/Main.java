package parserTest;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public class Main {
	
	private static final String XML_FILE = "./res/graph.xml";
	 
	  public static void main(String[] args) throws Exception {
	    Graph graph = new TinkerGraph();
	    GraphMLReader reader = new GraphMLReader(graph);
	 
	    InputStream is = new BufferedInputStream(new FileInputStream(XML_FILE));
	    reader.inputGraph(is);
	 
	    Iterable<Vertex> vertices = graph.getVertices();
	    Iterator<Vertex> verticesIterator = vertices.iterator();
	 
	    while (verticesIterator.hasNext()) {
	 
	      Vertex vertex = verticesIterator.next();
	      Iterable<Edge> edges = vertex.getEdges(Direction.IN);
	      Iterator<Edge> edgesIterator = edges.iterator();
	 
	      while (edgesIterator.hasNext()) {
	 
	        Edge edge = edgesIterator.next();
	        Vertex outVertex = edge.getVertex(Direction.OUT);
	        Vertex inVertex = edge.getVertex(Direction.IN);
	 
	        String person = (String) outVertex.getProperty("name");
	        String knownPerson = (String) inVertex.getProperty("name");
	        int since = (Integer) edge.getProperty("since");
	 
	        String sentence = person + " " + edge.getLabel() + " " + knownPerson
	                + " since " + since + ".";
	        System.out.println(sentence);
	      }
	    }
	  }

}
