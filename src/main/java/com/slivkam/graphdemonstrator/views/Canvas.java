package com.slivkam.graphdemonstrator.views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.slivkam.graphdemonstrator.presenters.GraphPresenter.GraphEditor;

/**
 *
 * @author Miroslav
 * Specifies panel in which can be painted some canvas object.
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

	private List<CanvasObject> objects;

	private GraphEditor editor; 
	
	//GraphPresenter presenter;

	/**
	 * Constructor.
	 * @param presenter that manages this view.
	 */
	public Canvas(GraphEditor editor) {
		super();
		this.editor = editor;
		//this.presenter = pFactory.create(this);
		objects = new ArrayList<CanvasObject>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

//	public GraphPresenter getPresenter() {
//		return this.presenter;
//	}
	
	private static final long serialVersionUID = 4259767900446651940L;

	@Override
	public void mouseClicked(MouseEvent me) {
		// pass
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent me) {
		this.editor.canvasMousePressed(me.getPoint());

	}

	@Override
	public void mouseReleased(MouseEvent me) {
		this.editor.canvasMouseReleased(me.getPoint());

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		this.editor.canvasMouseDragged(me.getPoint());

	}

	@Override
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawObjects((Graphics2D) g);

	}

	private void drawObjects(Graphics2D g2) {

		for (CanvasObject o : objects) {
			o.drawObject(g2);
		}

	}

	public void drawObject(CanvasObject o) {
		o.initShape();
		objects.add(o);
		this.repaint();
	}

	//public void drawDirectedEdge(Point p1, Point p2) {
	//drawEdge(p1,p2);

	//}

	public void removeLast() {
		objects.remove(objects.size()-1);

		this.repaint();
	}

	public void editObject(CanvasObject n) {
		for (CanvasObject o : objects) {
			if (o.equals(n)) {
				objects.remove(o);
				// TODO: implement vertex replacing
				n.setShape(o.getShape());
				objects.add(n);
				break;
			}
		}
		repaint();

	}

	public void removeObjectCloseTo(Point p) {
		// TODO: when object is vertex I need to remove edges too
		for (CanvasObject o : objects) {
			if (o.contains(p)) {
				objects.remove(o);
				break;
			}
		}
		repaint();

	}

	public void moveVertex(Point x) {
		// TODO Auto-generated method stub

	}

	public List<CanvasObject> getObjects() {
		return objects;
	}

	public void setObjects(List<CanvasObject> objects) {
		this.objects = objects;
		for (CanvasObject o : this.objects) {
			o.initShape();
		}
		repaint();

	}


	public void clean() {
		objects = new ArrayList<CanvasObject>();
		repaint();
	}
}
