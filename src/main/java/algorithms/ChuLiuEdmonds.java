package algorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import model.GraphAdapter;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class ChuLiuEdmonds extends Algorithm {

	private enum Phase {
		A,	// examine edges, shrink cycles
		B 	// expand cycle nodes
	};

	public enum Branching {
		MIN,	// minimum branching
		MAX 	// maximum branching
	};

	//private
	
	private Phase phase;
	private Branching branching;

	private Vertex root;
	protected Graph workingGraph;
	private Vertex cycleVertex;

	private Set<Vertex> vertices;	 // V
	private Set<Vertex> vertexBucket;// BV
	private Set<Edge> edgeBucket;	 // BE

	protected Set<Edge> cycle;
	private Set<Vertex> cycleVertices;

	private Vector<Graph> workingGraphs;
	private Vector<Set<Edge>> cycles;

	private boolean cycleToShrink;
	private boolean doneFlag;

	public ChuLiuEdmonds() {

		this.branching = Branching.MIN; // NOT IMPLEMENTED - only MIN is default
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
		this.setGraph(new GraphAdapter(graph));
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

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/**
	 * This method sets graph that should be algorithm performed on.
	 * @param g
	 */
	@Override
	public void setGraph(GraphAdapter g){
		this.workingGraph = g.getGraph();

		// Iterable to set
		for (Vertex v : workingGraph.getVertices()) {
			this.vertices.add(v);
		}
	}
	
	public void setBranching(Branching b) {
		this.branching = b;
	};


	/** Execute algorithm - all steps */
	public void execute(){
		System.out.println("Algorithm execution called.");
		while ( ! this.doneFlag ) {
			doStep();
		}
	}

	/** Do single step of algorithm:
	 * - examine node from V
	 * - shrink cycle to cycle node and construct graph Gi+1
	 * - expand shrinked cycle node
	 */
	@Override
	public void doStep() {

		if (this.doneFlag){
			this.setOutput((Graph)null);
			return;
		}

		if (this.cycleToShrink){
			reconstructWorkingGraph();
			updateBuckets();
			this.cycleToShrink = false;
		}
		else if ( this.phase == Phase.A ) {
			// examine node from V
			doStepA();
		}
		else {
			// expand shrinked cycle node
			doStepB();
		}

		publishSubGraph();
		// debug
		printStatus();

		if (this.vertices.size() == 0 &&  !this.cycleToShrink ){
			this.phase = Phase.B;
		}

		if (this.cycles.size() == 0 && this.phase == Phase.B && this.vertices.size() == 0){
			this.doneFlag = true;
			return;
		}

	}

	/** Examine node from V - find incident edge with min. weight */
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
			System.out.println("  vert id : " + (String) v.getId());

			// min or max
			e = getIncomingEdge(v);

			if ( e == null )
				return;

			//debug

			System.out.println("  edge : " + edgeToString(e) + " w=" + e.getProperty("weight"));

			// find cycle in BV U {e}
			this.cycle = new HashSet<Edge>();
			getCycle(e);

			this.cycleToShrink = (this.cycle.size() != 0);
			if (this.cycleToShrink){
				// public BE with cycle - difference from algorithm
				this.publishSubGraph();
			}

			this.edgeBucket.add(e);
		}
	}

	/** Choose incoming edge by weight and by selected branching */
	protected Edge getIncomingEdge(Vertex v) {
		return  (this.branching == Branching.MIN) ? getMinIncomingEdge(v) : getMaxIncomingEdge(v);
	}
	
	/** Reconstruct graph:
	 * - create graph Gi
	 * - contains cycle node Ci - overlaying cycle nodes from Gi-1
	 */
	private void reconstructWorkingGraph(){

		System.out.println("Reconstruction! - cycle shrinking");

		// save current state to stack
		this.workingGraphs.add(this.workingGraph);
		this.cycles.add(this.cycle);


		this.cycleVertices = new HashSet<Vertex>();
		for (Edge e : this.cycle){
			this.cycleVertices.add(e.getVertex(Direction.OUT));
		}

		Graph g = new TinkerGraph();

		// create Ci
		this.cycleVertex = g.addVertex("-"+this.cycles.size());
		cycleVertex.setProperty("cycleId", this.cycles.size());
		setVertexCoords(this.cycleVertex, this.cycleVertices.iterator().next());

		//
		// Constract graph Gi
		//

		for (Vertex v : this.workingGraph.getVertices()){
			if (!this.cycleVertices.contains(v)){
				Vertex newv = g.addVertex(v.getId());
				setVertexCoords(newv, v);
			}
		}

		// also prepare modified version of BE
		HashSet<Edge> newEdgeBucket = new HashSet<Edge>();

		for (Edge e : this.workingGraph.getEdges()){

			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();

			boolean wasInBucket = this.edgeBucket.remove(e);
			//System.out.println(edgeToString(e) + " was in bucekt " + (wasInBucket?"true":"false"));

			if (this.cycleVertices.contains(e.getVertex(Direction.IN)) &&
					this.cycleVertices.contains(e.getVertex(Direction.OUT))){
				// inner cycle edge but not part of cycle
				continue;
			}

			if (this.cycle.contains(e)){
				// cycle edge
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
				
				Integer oldWeight = Integer.parseInt((String)e.getProperty("weight"));
				Integer cycleEdgeWeight = getCycleInnerEdgeWeight(e.getVertex(Direction.IN));
				
				System.out.print(edgeToString(newEdge) + " c=");
				Integer newWeight = this.recalculateWeight(oldWeight, cycleEdgeWeight);
				
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

		// set Gi as working graph and update BE
		this.workingGraph = g;
		this.edgeBucket = newEdgeBucket;
	}

	/** Recalculate weight of edges incident to cycle node */
	protected Integer recalculateWeight(Integer oldWeight, Integer cycleEdgeWeight){
		System.out.print(" ["+oldWeight+" - "+cycleEdgeWeight+"]\n");
		return oldWeight - cycleEdgeWeight;
	}
	
	/** Get inner edge of cycle which is incident to vertex v. */
	private Integer getCycleInnerEdgeWeight(Vertex v){
		for (Edge e : this.cycle){
			if (e.getVertex(Direction.IN).equals(v))
				return Integer.parseInt((String)e.getProperty("weight"));
		}

		System.out.println("Warning - cycle incident edge not found!");
		return null;
	}

	/** Update set V and vertex bucket BV
	 * V: add new cycle node
	 * BV: remove cycle vertices from Gi-1
	 */
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

	/** Get incident edge of vertex v with min. weight */
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

	/** Get incident edge of vertex v with max. weight */
	private Edge getMaxIncomingEdge(Vertex v){
		Edge edge = null;
		Integer maxWeight = 0;

		for ( Edge e : v.getEdges(Direction.IN) ){
			Integer w = Integer.parseInt((String)e.getProperty("weight"));

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

	/** getCycle wrapper - call DFS to find cycle in BE */
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

	/** DFS to find cycle in BE */
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

	/** Prepare edges in BE for DFS - set property "mark" to "unmarked" */
	private void unmarkBucketEdges(){
		for (Edge e : this.edgeBucket){
			e.setProperty("mark", "unmarked");
		}
	}

	/** Expand cycle from top of cycles stack and update buckets. */
	public void doStepB() {
		System.out.println("[B] Step called - cycle expansion.");

		// cycle node ui (overlaying cycle Ci)
		this.cycleVertex = getCycleVertex();
		boolean outTreeRoot = isOutTreeRoot(this.cycleVertex);

		// cycle edges of Gi-1
		this.cycle = this.cycles.lastElement();
		this.workingGraph = this.workingGraphs.lastElement();

		// working graph Gi-1
		this.cycles.remove(this.cycle);
		this.workingGraphs.remove(this.workingGraph);

		Graph g = new TinkerGraph();

		//
		// reconstruct BV
		//
		System.out.println("[B] RECONSTRUCT BV");

		HashSet<Vertex> newVertexBucket = new HashSet<Vertex>();

		// process nodes from BV
		for (Vertex v : this.vertexBucket){
			if (!v.equals(cycleVertex)){
				Vertex newv = g.addVertex(v.getId());
				setVertexCoords(newv, v);
				newVertexBucket.add(newv);
			}
		}

		this.cycleVertices = new HashSet<Vertex>();
		// process cycle nodes
		for (Edge e : this.cycle){
			Vertex tmp = e.getVertex(Direction.OUT);
			Vertex newv = g.addVertex(tmp.getId());
			setVertexCoords(newv, tmp);
			newVertexBucket.add(newv);
			this.cycleVertices.add(tmp);
		}

		//update BV
		this.vertexBucket = newVertexBucket;

		//
		// reconstruct BE
		//
		System.out.println("[B] RECONSTRUCT BE");

		HashSet<Edge> newEdgeBucket = new HashSet<Edge>();

		Edge cycleIncidentEdge = null; // from Gi-1, there is always only one


		// process BE bucket
		for (Edge e : this.edgeBucket){

			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();
			Vertex inV = g.getVertex(inId);
			Vertex outV = g.getVertex(outId);

			Edge newEdge = null;

			if ( e.getVertex(Direction.IN).equals(cycleVertex) ){
				// (x,ui) - edge incident to cycle
				Object weight = null;
				Vertex iv = null;
				Vertex tmpiv = null;

				// find equivalent edge in Gi-1
				for (Edge ee : this.workingGraph.getEdges()){
					Object tmpOutId = ee.getVertex(Direction.OUT).getId();
					tmpiv = ee.getVertex(Direction.IN);
					if (this.cycleVertices.contains(tmpiv) && tmpOutId.equals(outId)){
						Object tmpw = ee.getProperty("weight");
						Integer oldw = 0;
						Integer neww = 0;
						if (weight == null) {
							weight = tmpw;
							oldw = Integer.parseInt((String)weight);
							iv = tmpiv;
							cycleIncidentEdge = ee;
						}
						else{
							oldw = Integer.parseInt((String)weight);
							neww = Integer.parseInt((String)tmpw);
							if (this.branching == Branching.MIN){
								weight = (oldw > neww) ? tmpw : weight;
							}
							else {
								weight = (oldw < neww) ? tmpw : weight;
							}
						}
						if (weight != null && oldw != Integer.parseInt((String)weight)){
							iv = tmpiv;
							cycleIncidentEdge = ee;
						}
					}
				}

				newEdge = outV.addEdge(e.getLabel(), g.getVertex(iv.getId()));
				newEdge.setProperty("weight", weight);
			}
			else if ( e.getVertex(Direction.OUT).equals(cycleVertex)) {
				// (ui,x)
				Object weight = null;
				Vertex ov = null;
				Vertex tmpov = null;

				System.out.println("Reconstruction (ui,x) : " + this.edgeToString(e));
				// find equivalent edge in Gi-1
				for (Edge ee : this.workingGraph.getEdges()){
					Object tmpInId = ee.getVertex(Direction.IN).getId();
					tmpov = ee.getVertex(Direction.OUT);
					if (this.cycleVertices.contains(tmpov) && tmpInId.equals(inId)){
						Object tmpw = ee.getProperty("weight");
						Integer oldw = 0;
						Integer neww = 0;
						if (weight == null) {
							weight = tmpw;
							oldw = Integer.parseInt((String)weight);
							ov = tmpov;
						}
						else{
							oldw = Integer.parseInt((String)weight);
							neww = Integer.parseInt((String)tmpw);
							if (this.branching == Branching.MIN){
								weight = (oldw > neww) ? tmpw : weight;
							}
							else {
								weight = (oldw < neww) ? tmpw : weight;
							}
						}
						if (weight != null && oldw != Integer.parseInt((String)weight)){
							ov = tmpov;
						}
					}
				}

				newEdge =  g.getVertex(ov.getId()).addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", weight);
				System.out.println("Equivalent  in   Gi-1 : " + this.edgeToString(newEdge));
			}
			else {
				newEdge = outV.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
			}
			newEdgeBucket.add(newEdge);
		}

		// process cycle edges
		Edge innerCycleExtremeEdge = this.branching == Branching.MIN ? getInnerCycleMaxEdge() : getInnerCycleMinEdge();
		Vertex cycleIncidentVertex = null;

		if (cycleIncidentEdge != null) {
			// there is incident edge to cycle Ci in Gi-1
			cycleIncidentVertex = cycleIncidentEdge.getVertex(Direction.IN);
		}

		for (Edge e : this.cycle){
			Object inId = e.getVertex(Direction.IN).getId();
			Object outId = e.getVertex(Direction.OUT).getId();
			Vertex inV = g.getVertex(inId);
			Vertex outV = g.getVertex(outId);

			Edge newEdge = null;

			// Ci is out-tree root
			if (outTreeRoot && innerCycleExtremeEdge.getVertex(Direction.IN).equals(e.getVertex(Direction.IN))
					&& innerCycleExtremeEdge.getVertex(Direction.OUT).equals(e.getVertex(Direction.OUT))) {
				//System.out.println("OUROOT");
				continue;
			}
			// ignore inner cycle edge which is incident to cycle node with existing
			//	outer (src node is not in cycle) incident edge
			else if (e.getVertex(Direction.IN).equals(cycleIncidentVertex)) {
				continue;
			}
			else {
				newEdge = outV.addEdge(e.getLabel(), inV);
				newEdge.setProperty("weight", e.getProperty("weight"));
				newEdgeBucket.add(newEdge);
			}

		}

		// update BE
		this.edgeBucket = newEdgeBucket;
	}

	/** Get edge with max. weight from cycle edges */
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
	
	/** Get edge with min. weight from cycle edges */
	private Edge getInnerCycleMinEdge(){
		Edge minEdge = null;
		Integer min = null;
		
		for (Edge e : this.cycle){
			Integer ew = Integer.parseInt((String)e.getProperty("weight"));
			if (minEdge == null || ew < min){
				min = ew;
				minEdge = e;
			}
		}
		return minEdge;
	}

	/** Check if vertex's deg-(v)==0 */
	private boolean isOutTreeRoot(Vertex v){
		System.out.println(this.workingGraph.toString());
		int size = 0;
		for(Edge e : v.getEdges(Direction.IN)) {
			for(Edge ee : this.edgeBucket) {
				if (ee.getVertex(Direction.IN).equals(e.getVertex(Direction.IN)) 
				 	&& ee.getVertex(Direction.OUT).equals(e.getVertex(Direction.OUT))) { 
					//System.out.println(" => " + this.edgeToString(e));
					size++;
				}
			}
		}
		return (size == 0);
	}

	/** Get cycle vertex from graph G - marked with property "cycleID"
	 * Value of cycleID == i, vertex == ui overalying Ci in Gi-1
	 */
	private Vertex getCycleVertex(){
		for (Vertex v : this.workingGraph.getVertices()){
			String vid = (String)v.getId();
			if (v.getProperty("cycleId") != null && v.getProperty("cycleId").equals(this.cycles.size()))
				//System.out.println("cycle vertex found " + vid + " " + v.getProperty("cycleId"));
				return v;
		}
		System.err.println("ERROR - cycle representing vertex not found!!");
		return null;
	}

	/** Copy soord properties from vertex b to vertex a. */
	private void setVertexCoords(Vertex a, Vertex b){
		a.setProperty("PositionX", b.getProperty("PositionX"));
		a.setProperty("PositionY", b.getProperty("PositionY"));
	}

	/** DEBUG - print current status of buckets. */
	public void printStatus(){
		System.out.print(
				"-----------------------------------------------\n" +
						"| STATUS - stage " + (this.phase == Phase.A ? "A" : "B") + "\n"+
						"-----------------------------------------------\n"
				);

		String sV = "";
		for (Vertex v : this.vertices){
			sV += v.getId()+", ";;
		}
		System.out.print("V = { " + sV + " }\n");


		String sBV = "";
		for (Vertex v : this.vertexBucket){
			sBV += v.getId()+", ";
		}
		System.out.print("BV = { " + sBV + " }\n");


		String sBE = "";
		for (Edge e : this.edgeBucket){
			sBE += edgeToString(e) + ", ";
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

	/** Publish graph.
	 * Construct graph from BE and send it MVC controller.
	 * If all steps of algortihm are done and published send null.
	 */
	public void publishSubGraph(){

		if (this.doneFlag){
			this.setOutput((Graph)null);
			return;
		}

		// construct subgraph from BE
		Graph g = new TinkerGraph();
		Edge edge = null;
		Vertex vertexIn;
		Vertex vertexOut;

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

		// add edges from BV - root can be in BV without edges in BE
		for (Vertex v : this.vertexBucket){
			vertexIn = g.getVertex(v.getId());

			if (vertexIn == null ){
				vertexIn = g.addVertex(v.getId());
				this.setVertexCoords(vertexIn, v);
			}

			vertexIn = null;

		}

		int i = 0;
		String sV = "";
		for (Vertex v : this.vertices){
			sV += v.getId();
			if (i != this.vertices.size() - 1){
				sV += ", ";
			}
			i++;
		}

		String sBV = "";
		i = 0;
		for (Vertex v : this.vertexBucket){
			sBV += v.getId();
			if (i != this.vertexBucket.size() - 1){
				sBV += ", ";
			}
			i++;
		}

		String sBE = "";
		i = 0;
		for (Edge e : this.edgeBucket){
			sBE += edgeToString(e);
			if (i != this.edgeBucket.size() - 1){
				sBE += ", ";
			}
			i++;
		}

		String sC = "";
		if (this.cycleToShrink) {
			i = 0;
			for (Edge e : this.cycle){
				sC += edgeToString(e);
				if (i != this.cycle.size() - 1){
					sC += ", ";
				}
				i++;
			}
			this.addProperty("C", sC);
		}
		this.addProperty("V", sV);
		this.addProperty("BV", sBV);
		this.addProperty("BE", sBE);
		this.setOutput(g);

		//System.out.println("SETS:\nV: "+sV+"\nBV: "+sBV+"\nBE: "+sBE + "\nC: "+sC);
	}
}


