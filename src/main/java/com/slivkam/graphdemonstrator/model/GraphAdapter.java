package com.slivkam.graphdemonstrator.model;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.slivkam.graphdemonstrator.swingcomponents.CanvasObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

/**
 *
 * @author Miroslav
 * Serves as adapter to real model.
 */
public class GraphAdapter{


    private Graph graph;

    private Integer vId;
    private Integer eId;

    /**
     * Create whole new graph in model.
     */
    public GraphAdapter() {
        this.vId = 1;
        this.eId = 1;
        this.graph = new TinkerGraph();
    }

    /**
     * Takes already created graph in model and uses it as initialization.
     * @param g model graph.
     */
    public GraphAdapter(Graph g) {
        this.graph = g;
        this.vId = this.getMaxVertexId() + 1;
        this.eId = this.getMaxEdgeId() + 1;
    }

    /**
     * Adds vertex to model and return created vertex wrapped into VertexAdapter.
     * @return just created vertex wrapped into VertexAdapter.
     */
    public VertexAdapter addVertex(Point p) {
        return new VertexAdapter(this.graph.addVertex(String.valueOf(this.vId++)),p);
    }

    /**
     * Adds vertex with id specified and return created vertex wrapped into VertexAdapter.
     * @param id id of new vertex.
     * @return vertex created wrapped into VertexAdapter.
     */
    public VertexAdapter addVertex(String id, Point p) {
        return new VertexAdapter(this.graph.addVertex(id),p);
    }

    /**
     * Adds new edge to model based on origin and destination vertices.
     * @param out origin vertex wrapped in VertexAdapter.
     * @param in destination vertex wrapped in VertexAdapter.
     * @return new edge wrapped in EdgeAdapter.
     */
    public EdgeAdapter addEdge(VertexAdapter out, VertexAdapter in) {
        EdgeAdapter e = new EdgeAdapter(
                this.graph.addEdge(String.valueOf(this.eId++), out.getVertex(), in.getVertex(), "1"));
        e.setWeight("1");
        return e;
    }

    /**
     * Gets all vertices in model.
     * @return list of vertices wrapped in VertexAdapter.
     */
    public List<VertexAdapter> getVertices() {
        List<VertexAdapter> vertices = new ArrayList<VertexAdapter>();
        for (Vertex v : this.graph.getVertices()) {
            vertices.add(new VertexAdapter(v));
        }
        return vertices;
    }

    /**
     * Removes vertex from a model.
     * @param v vertex to be removed wrapped in VertexAdapter.
     */
    public void removeVertex(VertexAdapter v) {
        this.graph.removeVertex(v.getVertex());

    }

    /**
     * Gets edge based on origin and destination vertices.
     * @param v1 origin vertex wrapped in VertexAdapter.
     * @param v2 destination vertex wrapped in VertexAdapter.
     * @return edge wrapped in EdgeAdapter.
     */
    public EdgeAdapter getEdge(VertexAdapter v1, VertexAdapter v2) {
        for (Edge e : v1.getVertex().getEdges(Direction.OUT, "1")) {
            if (e.getVertex(Direction.IN).equals(v2.getVertex()))
                return new EdgeAdapter(e);
        }
        return null;
    }

    /**
     * Removes edge from a model.
     * @param e edge to be removed wrapped in EdgeAdapter.
     */
    public void removeEdge(EdgeAdapter e) {
        this.graph.removeEdge(e.getEdge());

    }

    /**
     * Reads graphml formatted graph from a file.
     * @param f File with a graph definition.
     * @throws IOException
     */
    public void read(File f) throws IOException {
        GraphMLReader reader = new GraphMLReader(this.graph);
        InputStream is = new BufferedInputStream(new FileInputStream(f));
        reader.inputGraph(is);

        if (!this.checkVerticesCoords()) {
            throw new IOException("Missing positional attributes for some of the vertices.");
        }

        if (!this.checkEdgesProperties()) {
            throw new IOException("Missing weight property on some edge.");
        }

        this.vId = this.getMaxVertexId() + 1;
        this.eId = this.getMaxEdgeId() + 1;
    }

    private boolean checkVerticesCoords() {
        for (Vertex v : this.graph.getVertices()) {
            if (v.getProperty("PositionX") == null || v.getProperty("PositionY") == null) {
                return false;
            }
        }

        return true;
    }

    private boolean checkEdgesProperties() {
        for (Edge e : this.graph.getEdges()) {
            if (e.getProperty("weight") == null) {
                return false;
            }
        }
        this.recomputeEdgesCoords();
        return true;
    }

    private int getMaxEdgeId() {
        int max = 1;
        for (Edge e : this.graph.getEdges()) {
            max = Integer.parseInt(e.getId().toString()) > max ? Integer.parseInt(e.getId().toString()) : max;
        }
        return max;
    }

    private int getMaxVertexId() {
        int max = 1;
        for (Vertex v : this.graph.getVertices()) {
            max = Integer.parseInt(v.getId().toString()) > max ? Integer.parseInt(v.getId().toString()) : max;
        }
        return max;
    }

    /**
     * Writes graph as graphml formated file.
     * @param f Output file.
     * @throws IOException
     */
    public void write(File f) throws IOException {
        GraphMLWriter writer = new GraphMLWriter(this.graph);
        writer.outputGraph(f.getAbsolutePath());

    }

    /**
     * Gets all objects in graph ready to be painted.
     * TODO should this be here?
     * @return list of canvas objects.
     */
    public List<CanvasObject> getAll() {
        List<CanvasObject> o = new ArrayList<CanvasObject>();
        for (VertexAdapter v : this.getVertices()) {
            o.add(v);
        }
        for (EdgeAdapter e : this.getEdges()) {
            //            int x1 = Integer.parseInt(e.getEdge().getProperty("startX").toString());
            //            int y1 = Integer.parseInt(e.getEdge().getProperty("startY").toString());
            //            int x2 = Integer.parseInt(e.getEdge().getProperty("endX").toString());
            //            int y2 = Integer.parseInt(e.getEdge().getProperty("endY").toString());
            //            e.setPoints(new Point(x1,y1), new Point(x2,y2),
            //                    e.getEdge().getVertex(Direction.IN).equals(e.getEdge().getVertex(Direction.OUT)));
            e.setPoints(e.getOutVertex(),e.getInVertex());
            e.setLabel(e.getEdge().getProperty("weight").toString());
            o.add(e);
        }
        return o;
    }

    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Tries to recompute missing edges coordinates.
     */
    public void recomputeEdgesCoords() {
        for(EdgeAdapter e : this.getEdges()) {
            e.setPoints(e.getOutVertex(),e.getInVertex());
        }

    }

    /**
     * Gets all edges from model.
     * @return list of edges wrapped in EdgeAdapter.
     */
    public List<EdgeAdapter> getEdges() {
        List<EdgeAdapter> edges = new ArrayList<EdgeAdapter>();
        for(Edge e : this.graph.getEdges()) {
            edges.add(new EdgeAdapter(e));
        }
        return edges;
    }
}
