package views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import presenters.GraphPresenter;
import presenters.GraphPresenter.GraphEditor;

/**
 *
 * @author Miroslav
 * Specifies panel in which can be painted some canvas object.
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, GraphEditor {

	private List<CanvasObject> objects;

	GraphPresenter presenter;
	/**
	 *
	 */

	/**
	 * Constructor.
	 * @param presenter that manages this view.
	 */
	public Canvas(GraphPresenter presenter) {
		super();
		this.presenter = presenter;
		objects = new ArrayList<CanvasObject>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

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
		presenter.startPoint(me.getPoint());

	}

	@Override
	public void mouseReleased(MouseEvent me) {
		presenter.endPoint(me.getPoint());

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		presenter.possibleEndPoint(me.getPoint());

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

	@Override
	public void drawObject(CanvasObject o) {
		o.initShape();
		objects.add(o);
		this.repaint();
	}

	//public void drawDirectedEdge(Point p1, Point p2) {
	//drawEdge(p1,p2);

	//}

	@Override
	public void removeLast() {
		objects.remove(objects.size()-1);

		this.repaint();
	}

	@Override
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

	@Override
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

	@Override
	public void moveVertex(Point x) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CanvasObject> getObjects() {
		return objects;
	}

	@Override
	public void setObjects(List<CanvasObject> objects) {
		this.objects = objects;
		for (CanvasObject o : this.objects) {
			o.initShape();
		}
		repaint();

	}


	@Override
	public void clean() {
		objects = new ArrayList<CanvasObject>();
		repaint();
	}
}
