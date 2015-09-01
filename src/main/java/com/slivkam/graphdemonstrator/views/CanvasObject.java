package com.slivkam.graphdemonstrator.views;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

/**
 *
 * @author Miroslav
 * Only these object should be handles by canvas.
 */
public abstract class CanvasObject {
	private Shape shape = null;
	private String label = "";

	/**
	 * Method implemented here just draw shape that was set.
	 * You should override this if you want to draw label too or some other properties.
	 * @param g2
	 */
	public void drawObject(Graphics2D g2) {
		//TODO: where will be label placed
		// override
		g2.setColor(Color.BLACK);
		if (shape != null)
		{
			g2.draw(shape);
			//TODO: throw exception if shape is null
		}
	}

	/**
	 * Gets label.
	 * @return String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label to this object.
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets shape of this object.
	 * @return Shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Sets shape to this object.
	 * @param shape
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * This method should decide weather point is somewhere on this object.
	 * @param p point under test
	 * @return true if popint is on this object.
	 */
	public abstract boolean contains(Point p);

	/**
	 * This should be called after position of the object is known.
	 * Its purpose is to initialize Shape object which will be later painted.
	 */
	public abstract void initShape();



}
