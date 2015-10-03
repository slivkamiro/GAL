package com.slivkam.graphdemonstrator.model;

import java.awt.Color;
import java.awt.Graphics2D;

import com.slivkam.graphdemonstrator.swingcomponents.CurvedArrow;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

/**
 *
 * @author Miroslav
 * This class serves as adapter to real model.
 */
public class EdgeAdapter extends CurvedArrow {

    private Edge e;

    /**
     * Creates new edge based on models edge.
     * @param e Models edge.
     */
    public EdgeAdapter(Edge e) {
        super();
        this.e = e;
    }

    /**
     * Gets models edge.
     * @return edge from model.
     */
    public Edge getEdge() {
        return this.e;
    }

    /**
     * Gets vertex from which this edge originates.
     * @return vertex adapter class.
     */
    public VertexAdapter getOutVertex() {
        return new VertexAdapter(this.e.getVertex(Direction.OUT));
    }

    /**
     * Gets vertex that this edge directs to.
     * @return vertex adapter class.
     */
    public VertexAdapter getInVertex() {
        return new VertexAdapter(this.e.getVertex(Direction.IN));
    }

    /**
     * Gets weight property of the edge.
     * @return string value of weight.
     */
    public String getWeight() {
        return this.e.getProperty("weight").toString();
    }

    /**
     * Sets weight property.
     * @param w String value of weight. Have to be number.
     */
    public void setWeight(String w) {
        this.e.setProperty("weight", w);
        this.setLabel(w);

    }

    /**
     * Sets starting point and end point based on origin vertex and destination vertex.
     * @param out origin vertex.
     * @param in destination vertex.
     */
    public void setPoints(VertexAdapter out, VertexAdapter in) {
        this.setSource(out);
        this.setDestination(in);
    }

    @Override
    public void drawObject(Graphics2D g2) {
        super.drawObject(g2);
        g2.setColor(Color.BLACK);
        if (this.getShape() != null) {

            double x1 = this.getCtrlP2().getX();
            double x2 = this.getP2().getX();
            double y1 = this.getCtrlP2().getY();
            double y2 = this.getP2().getY();
            int x = (int) (x1 + x2)/2;
            int y = (int) (y1 + y2)/2;

            /* draw string */
            g2.drawString(this.getLabel(),x , y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EdgeAdapter) {
            EdgeAdapter e = (EdgeAdapter) o;
            return e.getEdge().equals(this.e);
        }
        return false;

    }

}
