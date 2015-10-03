package com.slivkam.graphdemonstrator.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slivkam.graphdemonstrator.swingcomponents.Circle;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 * @author Miroslav
 * Serves as adapter to model's vertex.
 */
public class VertexAdapter extends Circle{

    private Vertex v;

    /**
     * Creates new VertexAdapter that holds model's vertex.
     * @param v model's vertex
     * @param c center of vertex
     */
    public VertexAdapter(Vertex v, Point c) {
        super(c);
        this.v = v;
        this.setLabel(v.getId().toString());
        v.setProperty("PositionX", String.valueOf(c.x));
        v.setProperty("PositionY", String.valueOf(c.y));
    }

    /**
     * Creates Vertex adapter from vertex that already has defined position.
     * @param v
     */
    public VertexAdapter(Vertex v) {
        this(v,new Point(Integer.valueOf(v.getProperty("PositionX")),
                Integer.valueOf(v.getProperty("PositionY"))));
    }

    /**
     * Gets model's vertex.
     * @return model's vertex.
     */
    public Vertex getVertex() {
        return this.v;
    }

    /**
     * Adds edge with this vertex as origin.
     * @param e edge to be added wrapped in edge adapter.
     */
    public void addEdge(EdgeAdapter e) {
        this.v.addEdge("1", e.getInVertex().getVertex());
    }

    /**
     * Sets attribute of the vertex. Used for position on canvas.
     * @param name Attribute name.
     * @param value Attribute value.
     */
    public void setAttribute(String name, String value) {
        this.v.setProperty(name,value);
    }

    /**
     * Gets attribute with name specified.
     * @param name Name of the attribute.
     * @return
     */
    public String getAttribute(String name) {
        return this.v.getProperty(name);
    }

    /**
     * Gets vertex id.
     * @return string id. Will be number.
     */
    public String getId() {
        return (String) this.v.getId();
    }

    /**
     * Gets all attributes that this vertex has.
     * @return Map of string keys and string attributes.
     */
    public Map<String,String> getAttributes() {
        Map<String,String> attributes = new HashMap<String,String>();
        for (String key : this.v.getPropertyKeys()) {
            attributes.put(key, this.v.getProperty(key).toString());
        }
        return attributes;
    }

    /**
     * Gets all edges that originate in this vertex or this vertex is it's destination.
     * @return List of edges wrapped in EdgeAdapter.
     */
    public List<EdgeAdapter> getEdges() {
        List<EdgeAdapter> edges = new ArrayList<EdgeAdapter>();
        for (Edge e : this.v.getEdges(Direction.BOTH, "1")) {
            EdgeAdapter ea = new EdgeAdapter(e);
            ea.setPoints(ea.getOutVertex(),ea.getInVertex());
            ea.setLabel(e.getProperty("weight"));
            edges.add(ea);
        }
        return edges;
    }

    /**
     * Removes attribute with name given.
     * @param k Name of the attribute to be removed.
     */
    public void deleteAttribute(String k) {
        this.v.removeProperty(k);

    }

    @Override
    public void drawObject(Graphics2D g2) {
        super.drawObject(g2);
        g2.setColor(Color.BLACK);
        if (this.getShape() != null) {
            int x = this.getCenterPoint().x;
            int y = this.getCenterPoint().y;
            g2.drawString(this.getLabel(),x , y );

        }
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof VertexAdapter) {
            VertexAdapter v = (VertexAdapter) o;
            if (v.getLabel().equals(this.getLabel())
                    && v.getId().equals(this.getId())
                    && v.getVertex().equals(this.getVertex()))
                return true;
        }
        return false;
    }
}
