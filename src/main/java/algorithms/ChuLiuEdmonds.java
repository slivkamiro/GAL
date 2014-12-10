package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class ChuLiuEdmonds extends Algorithm {
	
	private enum Phase { 
		A,	// examine edges, shrink cycles 
		B 	// expand cycle nodes
	};
	
	private enum Branching { 
		MIN,	// minimum branching 
		MAX 	// maximum branching
	};
	
	private Phase phase;
	private Branching branching;
	
	private Vertex root;	
	private Graph workingGraph;
	private Vertex cycleVertex;
	
	private Set<Vertex> vertices;	 // V	
	private Set<Vertex> vertexBucket;// BV
	private Set<Edge> edgeBucket;	 // BE
	
	private Set<Edge> cycle;
	private Set<Vertex> cycleVertices;
	
	private Vector<Graph> workingGraphs;
	private Vector<Set<Edge>> cycles;

	private boolean cycleToShrink;
	private boolean doneFlag;

	public ChuLiuEdmonds() {
		this.branching = Branching.MIN;
		
		this.doneFlag = false;
		this.cycleToShrink = false;
		
		this.root = null;
		this.phase = Phase.A;
		this.cycleVertex = null;
		
		this.cycle = new HashSet<Edge>();
		
		this.vertices = new HashSet<Vertex>();		
		this.vertexBucket = new HashSet<Vertex>();	
		this.edgeBucket = new HashSet<Edge>();		
		
		this.workingGraphs = new Vector<Graph>();
		this.cycles = new Vector<Set<Edge>>();
	}
	
	/**
	 * Constructor.
	 * @param graph
	 */
	public ChuLiuEdmonds(Graph graph) {
		this();
		this.setGraph(graph);
	}
	
	/**
	 * Constructor.
	 * @param graph
	 * @param b	MIN or MAX branching.
	 */
	public ChuLiuEdmonds(Graph graph, Branching b) {
		this(graph);
		this.branching = b;
	}
	
	/**
	 * Constructor.
	 * @param graph
	 * @param root
	 */
	public ChuLiuEdmonds(Graph graph, Vertex root) {
		this(graph);
		this.root = root;
	}
	
	/**
	 * This method sets graph that should be algorithm performed on.
	 * @param g
	 */
	public void setGraph(Graph g){
		this.workingGraph = g;
		
		// Iterable to set
		for (Vertex v : g.getVertices()) {
			this.vertices.add(v);
		}
	}


	
	public void execute(){
		System.out.println("Algorithm execution called.");
		while ( ! this.doneFlag ) {
			doStep();
			
			// debug
			printStatus();
		}
	}
	
	public void doStep() {
		if (this.cycleToShrink){
			System.out.println("Reconstruction! - cycle shrinking");
			reconstructWorkingGraph();
			updateBuckets();
			this.cycleToShrink = false;
		}
		else if ( this.phase == Phase.A ) {
			doStepA();
		}
		else {
			doStepB();
		}
		
		publishSubGraph();
		
		if (this.cycles.size() == 0 && this.phase == Phase.B){		
			this.doneFlag = true;
			return;
		}
		
	}
	
	public void doStepA() {
		System.out.println("[A] Step called.");
		
		Vertex v = null;
		Edge e = null;
		
		Iterator<Vertex> it = this.vertices.iterator();
		if ( it.hasNext() ) {
			
			v = it.next();
			it.remove();
			this.vertexBucket.add(v);
			
			// debug
			System.out.println(
					"  vert : " + (String) v.getId());
			
			e = getMinIncomingEdge(v);
			if ( e == null ){
				return;
			}
			
			//debug
			System.out.println(
					"  edge : " + edgeToString(e) + " w=" + e.getProperty("weight"));
			
			// find cycle in BV U {e}
			this.cycle = new HashSet<Edge>();
			getCycle(e);
			if (this.cycle.size() != 0){
//				// debug
//				printStatus();
				this.cycleToShrink = true;
			}
			
			// TODO - change in alg.
			if (this.cycle.size() == 0){
				this.edgeBucket.add(e);
			}
			
		}	
		
		if (this.vertices.size() == 0){
			this.phase = Phase.B;
		}
	}

	private void reconstructWorkingGraph(){
		this.workingGraphs.add(this.workingGraph);
		this.cycles.add(this.cycle);
		
		System.out.println("cycle pushed");
		System.out.println("graph pushed");
		
		
		this.cycleVertices = new HashSet<Vertex>();
		for (Edge e : this.cycle){
			this.cycleVertices.add(e.getVertex(Direction.OUT));
		}
		
		Graph g = new TinkerGraph();
		this.cycleVertex = g.addVertex(new String("cycle" + this.cycles.size()));
		cycleVertex.setProperty("cycleId", this.cycles.size());
		setVertexCoords(this.cycleVertex, this.cycleVertices.iterator().next());

		for (Vertex v : this.workingGraph.getVertices()){
			if (!this.cycleVertices.contains(v)){
				Vertex newv = g.addVertex(v.getId());
				setVertexCoords(newv, v);
			}
		}
		
		HashSet<Edge> newEdgeBucket = new HashSet<Edge>();
		
		for (Edge e : this.workingGraph.getEdges()){
			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();
		
			boolean wasInBucket = this.edgeBucket.remove(e);
			System.out.println(edgeToString(e) + " was in bucekt " + (wasInBucket?"true":"false"));
			
			if (this.cycleVertices.contains(e.getVertex(Direction.IN)) &&
					this.cycleVertices.contains(e.getVertex(Direction.OUT))){
				// inner cycle edge but not part of cycle
				continue;
			}
					
			if (this.cycle.contains(e)){
				continue;
			}
			
			Vertex inV = g.getVertex(inId);
			Vertex outV = g.getVertex(outId);
			
			Edge newEdge = null;
			
			if (this.cycleVertices.contains(e.getVertex(Direction.OUT))){
				// add edge of type (ui,x) - ui is cycle node
				newEdge = cycleVertex.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
			}
			else if (this.cycleVertices.contains(e.getVertex(Direction.IN))){
				// add edge of type (x, ui)
				newEdge = outV.addEdge(e.getLabel(), cycleVertex);
				// TODO
				Integer oldWeight = Integer.parseInt((String)e.getProperty("weight"));
				Integer cycleEdgeWeight = getCycleInnerEdgeWeight(e.getVertex(Direction.IN));
			 		
				Integer newWeight = oldWeight - cycleEdgeWeight;
				System.out.print(edgeToString(newEdge) + " c="+newWeight+" ["+oldWeight+" - "+cycleEdgeWeight+"]\n");
				newEdge.setProperty("weight", ""+newWeight);
			}
			else{
				// add edge of type (x, y)
				newEdge = outV.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
			}
			if (wasInBucket){
				newEdgeBucket.add(newEdge);
			}
		}
		
		this.workingGraph = g;
		this.edgeBucket = newEdgeBucket;
	}
	
	private Integer getCycleInnerEdgeWeight(Vertex v){
		for (Edge e : this.cycle){
			if (e.getVertex(Direction.IN).equals(v)){
				return Integer.parseInt((String)e.getProperty("weight"));
			}
		}
		
		System.out.println("Warning - cycle incident edge not found!");
		return null;
	}
			
	private void updateBuckets(){
		// BV
		Iterator<Vertex> it = this.vertexBucket.iterator();
		while ( it.hasNext() ) {
			if (this.cycleVertices.contains(it.next())){
				it.remove();
			}
		}
		
		// V
		this.vertices = new HashSet<Vertex>();
		for (Vertex v : workingGraph.getVertices()) {
			if (!this.vertexBucket.contains(v)){
					this.vertices.add(v);
			}
		}
	}
	
	private Edge getMinIncomingEdge(Vertex v){
		Edge edge = null;
		Integer minWeight = 0;
		
		String s = "";
	 	for ( Edge e : v.getEdges(Direction.IN) ){
	 		Integer w = Integer.parseInt((String)e.getProperty("weight"));
			s += edgeToString(e) + " c="+w+"\n";
	 		
	 		if (edge == null){
	 			minWeight = w;
	 			edge = e;
	 			continue;
	 		}
	 		
	 		if (w < minWeight){
				minWeight = w;
				edge = e;
			}	
	 	}
		System.out.print(s);

	 	return minWeight <= 0 ? null : edge;
	}
	
	private Edge getMaxIncomingEdge(Vertex v){
		Edge edge = null;
		Integer maxWeight = 0;
		
	 	for ( Edge e : v.getEdges(Direction.IN) ){
	 		Integer w = (Integer)e.getProperty("weight");
	 		
	 		if (edge == null){
	 			maxWeight = w;
	 			edge = e;
	 			continue;
	 		}
	 		
	 		if (w > maxWeight){
				maxWeight = w;
				edge = e;
			}	
	 	}
	 	return maxWeight <= 0 ? null : edge;
	}
	
	/* getCycle wrapper */
	private void getCycle(Edge goalEdge){
		unmarkBucketEdges();
		
		goalEdge.setProperty("mark", "marked");
		for (Edge ee : goalEdge.getVertex(Direction.IN).getEdges(Direction.OUT)){
			if (!this.edgeBucket.contains(ee)){
				continue;
			}
			else{
				ee.setProperty("mark", "marked");
				getCycle(ee, goalEdge);
				if(this.cycle.size() != 0){
					this.cycle.add(ee);
					break;
				}
			}	
		}
	}
	
	private void getCycle(Edge e, final Edge goalEdge){
		for (Edge ee : e.getVertex(Direction.IN).getEdges(Direction.OUT)){
			if (!this.edgeBucket.contains(ee) && !ee.equals(goalEdge)){
				continue;
			}
			if (ee.getProperty("mark") == "marked"){
				this.cycle.add(ee);
				return;
			}
			else{
				ee.setProperty("mark", "marked");
				getCycle(ee, goalEdge);
				if(this.cycle.size() != 0){
					this.cycle.add(ee);
					return;
				}
			}	
		}
	}
	
	private void unmarkBucketEdges(){
		for (Edge e : this.edgeBucket){
			e.setProperty("mark", "unmarked");
		}
	}
	
	
	public void doStepB() {
		System.out.println("[B] Step called - cycle expansion.");
		
		// cycle node i
		this.cycleVertex = getCycleVertex();
		boolean outTreeRoot = isOutTreeRoot(this.cycleVertex);
		
		// cycle edges i-1
		this.cycle = this.cycles.lastElement();
		this.workingGraph = this.workingGraphs.lastElement();
		
		// working graph i-1
		this.cycles.remove(this.cycle);
		this.workingGraphs.remove(this.workingGraph);
		
		Graph g = new TinkerGraph();
		
		// reconstruct BV
		System.out.println("[B] RECONSTRUCT BV");
		HashSet<Vertex> newVertexBucket = new HashSet<Vertex>();

		for (Vertex v : this.vertexBucket){
			if (!v.equals(cycleVertex)){
				Vertex newv = g.addVertex(v.getId());
				setVertexCoords(newv, v);
				newVertexBucket.add(newv);
			}
		}
		
		this.cycleVertices = new HashSet<Vertex>();
		
		for (Edge e : this.cycle){
			Vertex tmp = e.getVertex(Direction.OUT);
			Vertex newv = g.addVertex(tmp.getId());
			setVertexCoords(newv, tmp);
			newVertexBucket.add(newv);
			this.cycleVertices.add(tmp);
		}
		
		this.vertexBucket = newVertexBucket;
		
		// reconstruct BE
		System.out.println("[B] RECONSTRUCT BE");
		
		HashSet<Edge> newEdgeBucket = new HashSet<Edge>();
		Edge cycleIncidentEdge = null; // from gi-1, there is always only one
		
		for (Edge e : this.edgeBucket){
			
			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();
			Vertex inV = g.getVertex(inId);
			Vertex outV = g.getVertex(outId);
			
			Edge newEdge = null;
			
			if ( e.getVertex(Direction.IN).equals(cycleVertex) ){
				// (x,ui)
				Object weight = null;
				Vertex iv = null;
				
				for (Edge ee : this.workingGraph.getEdges()){
					Object tmpOutId = ee.getVertex(Direction.OUT).getId();
					iv = ee.getVertex(Direction.IN);
					if (this.cycleVertices.contains(iv) && tmpOutId.equals(outId)){
						weight = ee.getProperty("weight");
						cycleIncidentEdge = ee;
						break;
					}
				}
				
				newEdge = outV.addEdge(e.getLabel(), g.getVertex(iv.getId()));
				newEdge.setProperty("weight", weight);
			}
			else if ( e.getVertex(Direction.OUT).equals(cycleVertex)) {
				// (ui,x)
				Object weight = null;
				Vertex ov = null;
				
				for (Edge ee : this.workingGraph.getEdges()){
					Object tmpInId = ee.getVertex(Direction.IN).getId();
					ov = ee.getVertex(Direction.OUT);
					if (this.cycleVertices.contains(ov) && tmpInId.equals(inId)){
						weight = ee.getProperty("weight");
						break;
					}
				}
				
				newEdge =  g.getVertex(ov.getId()).addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", weight);
			}
			else {
				newEdge = outV.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
			}
			newEdgeBucket.add(newEdge);
		}
		
		// iterate also over cycle edges
		Edge inneCycleMaxEdge = getInnerCycleMaxEdge();
		Vertex cycleIncidentVertex = null;
		if (cycleIncidentEdge != null) {
			cycleIncidentVertex = cycleIncidentEdge.getVertex(Direction.IN);
		}
		
		for (Edge e : this.cycle){
			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();
			Vertex inV = g.getVertex(inId);
			Vertex outV = g.getVertex(outId);
			
			Edge newEdge = null;
			
			// ci is out-tree root
			if (outTreeRoot && e.equals(inneCycleMaxEdge)) {
				continue;
			}
			// check cycle incident edge
			else if (e.getVertex(Direction.IN).equals(cycleIncidentVertex)) {
				continue;
			}
			else {
				newEdge = outV.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
				newEdgeBucket.add(newEdge);
			}
			
		}
		
		this.edgeBucket = newEdgeBucket;		
	}
	
	private Edge getInnerCycleMaxEdge(){
		Edge maxEdge = null;
		Integer max = -1;
		for (Edge e : this.cycle){
			Integer ew = Integer.parseInt((String)e.getProperty("weight"));
			if (ew > max){
				max = ew;
				maxEdge = e;
			}
		}
		return maxEdge;
	}
	
	private boolean isOutTreeRoot(Vertex v){
		int size = 0;
		for(Edge e : v.getEdges(Direction.IN)) { 
			if (this.edgeBucket.contains(e)) { size++;} 
		}
		return (size == 0);
	}
	
	private Vertex getCycleVertex(){
		for (Vertex v : this.workingGraph.getVertices()){
			String vid = (String)v.getId();
			if (vid.contains("cycle") && v.getProperty("cycleId").equals(this.cycles.size())){
				System.out.println("cycle vertex found " + vid + " " + v.getProperty("cycleId"));
				return v;
			}
			System.out.println(vid + " " + v.getProperty("cycleId") + " " + this.cycles.size());
		}
		System.err.println("ERROR - cycle representing vertex not found!!");
		return null;
	}
	
	private void setVertexCoords(Vertex a, Vertex b){
		a.setProperty("PositionX", b.getProperty("PositionX"));
		a.setProperty("PositionY", b.getProperty("PositionY"));
	}
	
	
	public void printStatus(){
		System.out.print(
		"-----------------------------------------------\n" +
		"| STATUS - stage " + (this.phase == Phase.A ? "A" : "B") + "\n"+
		"-----------------------------------------------\n"
		);
		
		String sV = "";
		for (Vertex v : this.vertices){
			sV += v.getId() + "["+v.getProperty("PositionX") +", "+v.getProperty("PositionY")+"], ";
		}
		System.out.print("V = { " + sV + " }\n");
		

		String sBV = "";
		for (Vertex v : this.vertexBucket){
			sBV += v.getId() + "["+v.getProperty("PositionX") +", "+v.getProperty("PositionY")+"], ";
		}
		System.out.print("BV = { " + sBV + " }\n");
		

		String sBE = "";
		for (Edge e : this.edgeBucket){
			sBE += edgeToString(e) + "["+e.getProperty("startX") +", "+e.getProperty("endX")+"], ";
		}
		System.out.print("BE = { " + sBE + " }\n");
		
		String sC = "";
		for (Edge e : this.cycle){
			sC += edgeToString(e) + ", "; 
		}
		if (this.cycle.size() == 0){ sC = "None"; }
		System.out.print("C = { " + sC + " }\n");
	
		System.out.print("-----------------------------------------------\n");
	}
	
	public String edgeToString(Edge e){
		return "(" + e.getVertex(Direction.OUT).getId() + "," + e.getVertex(Direction.IN).getId() + ")";
	}

	public void publishSubGraph(){
		
		if (this.doneFlag){
			this.setOutput((Graph)null);
			return;
		}
		
		// construct subgraph from BE
		Graph g = new TinkerGraph();
		Edge edge = null;
		Vertex vertexIn = null; 
		Vertex vertexOut = null;
		
		for (Edge e : this.edgeBucket){
			vertexIn = g.getVertex(e.getVertex(Direction.IN).getId()); 
			vertexOut = g.getVertex(e.getVertex(Direction.OUT).getId());
			
			if (vertexIn == null ){
				vertexIn = g.addVertex(e.getVertex(Direction.IN).getId());
				this.setVertexCoords(vertexIn, e.getVertex(Direction.IN));
			}
			if (vertexOut == null ){
				vertexOut = g.addVertex(e.getVertex(Direction.OUT).getId());
				this.setVertexCoords(vertexOut, e.getVertex(Direction.OUT));
			}
			
			edge = vertexOut.addEdge(e.getLabel(), vertexIn);
			edge.setProperty("weight", e.getProperty("weight"));
			
			vertexIn = null; 
			vertexOut = null;
			
		}
		
		this.setOutput(g);
	}
}


