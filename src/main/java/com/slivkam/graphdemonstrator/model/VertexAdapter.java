package com.slivkam.graphdemonstrator.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slivkam.graphdemonstrator.views.CanvasObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 *
 * @author Miroslav
 * Serves as adapter to model's vertex.
 */
public class VertexAdapter extends CanvasObject{

	private Vertex v;

	/**
	 * Creates new VertexAdapter that holds model's vertex.
	 * @param v model's vertex
	 */
	public VertexAdapter(Vertex v) {
		super();
		this.v = v;
		this.setLabel(v.getId().toString());
	}

	/**
	 * Gets model's vertex.
	 * @return model's vertex.
	 */
	public Vertex getVertex() {
		return v;
	}

	/**
	 * Adds edge with this vertex as origin.
	 * @param e edge to be added wrapped in edge adapter.
	 */
	public void addEdge(EdgeAdapter e) {
		v.addEdge("1", e.getInVertex().getVertex());
	}

	/**
	 * Sets attribute of the vertex. Used for position on canvas.
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public void setAttribute(String name, String value) {
		v.setProperty(name,value);
	}

	/**
	 * Gets attribute with name specified.
	 * @param name Name of the attribute.
	 * @return
	 */
	public String getAttribute(String name) {
		return v.getProperty(name).toString();
	}

	/**
	 * Gets vertex id.
	 * @return string id. Will be number.
	 */
	public String getId() {
		return (String) v.getId();
	}

	/**
	 * Gets all attributes that this vertex has.
	 * @return Map of string keys and string attributes.
	 */
	public Map<String,String> getAttributes() {
		Map<String,String> attributes = new HashMap<String,String>();
		for (String key : v.getPropertyKeys()) {
			attributes.put(key, v.getProperty(key).toString());
		}
		return attributes;
	}

	/**
	 * Gets all edges that originate in this vertex or this vertex is it's destination.
	 * @return List of edges wrapped in EdgeAdapter.
	 */
	public List<EdgeAdapter> getEdges() {
		List<EdgeAdapter> edges = new ArrayList<EdgeAdapter>();
		for (Edge e : v.getEdges(Direction.BOTH, "1")) {
			edges.add(new EdgeAdapter(e));
		}
		return edges;
	}

	/**
	 * Removes attribute with name given.
	 * @param k Name of the attribute to be removed.
	 */
	public void deleteAttribute(String k) {
		v.removeProperty(k);

	}

	@Override
	public void drawObject(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		if (this.getShape() != null) {
			g2.draw(this.getShape());
			int x = (int) ((Ellipse2D) this.getShape()).getCenterX();
			int y = (int) ((Ellipse2D) this.getShape()).getCenterY();
			g2.drawString(this.getLabel(),x , y );

		}
	}

	@Override
	public void initShape() {
		Integer x = Integer.parseInt(v.getProperty("PositionX").toString());
		Integer y = Integer.parseInt(v.getProperty("PositionY").toString());
		Ellipse2D e = new Ellipse2D.Double(x-10.0, y-10.0, 20.0, 20.0);
		this.setShape(e);

	}

	@Override
	public boolean contains(Point p) {
		Ellipse2D vertex = (Ellipse2D) this.getShape();
		return vertex.contains(p);
		/*if(Math.pow(p.x-vertex.getCenterX(),2)+
				Math.pow(p.y-vertex.getCenterY(), 2) < 100) {
			return true;
		}
		return false;*/
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VertexAdapter) {
			VertexAdapter v = (VertexAdapter) o;
			if (v.getLabel().equals(getLabel())
					&& v.getId().equals(getId())
					&& v.getVertex().equals(getVertex()))
				return true;
		}
		return false;
	}
}
