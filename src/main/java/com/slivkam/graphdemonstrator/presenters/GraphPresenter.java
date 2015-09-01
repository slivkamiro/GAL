package com.slivkam.graphdemonstrator.presenters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.slivkam.graphdemonstrator.model.EdgeAdapter;
import com.slivkam.graphdemonstrator.model.GraphAdapter;
import com.slivkam.graphdemonstrator.model.VertexAdapter;
import com.slivkam.graphdemonstrator.views.CanvasObject;

/**
 *
 * @author Miroslav
 *
 */
public class GraphPresenter extends Presenter {
	
	/**
	 *
	 * @author Miroslav
	 * Interface that must implement every View that this presenter manages.
	 */
	public interface GraphEditor {

		PresenterFactory getPresenterFactory();
		
		/**
		 * Canvas to presenter connectors.
		 * @param point
		 */
		void canvasMousePressed(Point point);
		void canvasMouseReleased(Point point);
		void canvasMouseDragged(Point point);
		
		/**
		 * Draw canvas object specified.
		 * @param o
		 */
		void drawObject(CanvasObject o);
		//public void drawShape(final Shape s);
		//public void drawDirectedEdge(Point p1, Point p2);

		/**
		 * Edit canvas object specified.
		 * @param o
		 */
		void editObject(CanvasObject o);

		/**
		 * Remove object painted to graph on position specified.
		 * @param p position
		 */
		void removeObjectCloseTo(Point p);

		/**
		 * Remove last object painted.
		 */
		void removeLast();
		//public void removeVertexCloseTo(Point p);
		//public void removeEdgeCloseTo(Point p);

		/**
		 * Move with vertex on position.
		 * @param p position.
		 */
		void moveVertex(Point p);

		/**
		 * Gets all object from canvas.
		 * @return list of canvas objects.
		 */
		List<CanvasObject> getObjects();

		/**
		 * Set objects to be painted.
		 * @param objects list of canvas objects.
		 */
		void setObjects(List<CanvasObject> objects);

		/**
		 * Clean canvas.
		 */
		void clean();
	}

	/**
	 *
	 * @author Miroslav
	 * Enumeration of modes of graph editor. NONE means that application is not in editing mode.
	 */
	public enum EditorOptions {
		VERTEX,
		EDGE,
		EDIT,
		REMOVE,
		NONE
	}

	private GraphEditor editor;

	protected GraphAdapter graph;

	private EditorOptions mode = EditorOptions.NONE;

	private Point start;
	private Point end;

	/**
	 * Default constructor. Present empty graph.
	 */
	private GraphPresenter() {
		super();
		graph = new GraphAdapter();
	}
	
	@Inject
	public GraphPresenter(GraphEditor view) {
		this();
		this.editor = view;
	}

	/**
	 * Sets view to this presenter.
	 * @param editor
	 */
	public void setView(GraphEditor editor) {
		this.editor = editor;
	}
	
	@Override
	public View getView() {
		return (View) this.editor;
	}

	/**
	 * Where user releases mouse button.
	 * @param point
	 */
	public void endPoint(Point point) {
		switch (mode) {
		case EDGE:
			end = point;
			// add edge to model and draw it on canvas
			VertexAdapter out = getVertexOnPosition(start);
			VertexAdapter in = getVertexOnPosition(end);
			editor.removeLast();
			// TODO in == out
			if (out != null && in != null && !in.equals(out)) {
				EdgeAdapter e = graph.addEdge(out, in);
				e.setPoints(out, in);
				editor.drawObject(e);
			}
			break;
		case VERTEX:
			// add vertex to model
			VertexAdapter v = graph.addVertex();
			v.setAttribute("PositionX", String.valueOf(point.x));
			v.setAttribute("PositionY", String.valueOf(point.y));
			// draw vertex to canvas
			editor.drawObject(v);
			break;
		case EDIT:
		case REMOVE:
		case NONE:
			break;
		}

	}

	/**
	 * Where user pressed mouse button.
	 * @param point
	 */
	public void startPoint(Point point) {
		VertexAdapter v = null;
		EdgeAdapter e = null;
		switch (mode) {
		case EDGE:
			start = point;
			end = point;
			editor.drawObject(new IntermediateEdge(start,end));
			//editor.drawShape(new Line2D.Double(start, end));
			break;
		case EDIT:
			v = getVertexOnPosition(point);
			if (v != null) {
				this.populateDialog(v, this.editor.getPresenterFactory().createVertexPresenter());
				editor.editObject(v);
				break;
			}
			e = getEdgeCloseTo(point);
			if (e != null) {
				this.populateDialog(e, this.editor.getPresenterFactory().createEdgePresenter());
				editor.editObject(e);
			}
			break;
		case REMOVE:
			v = getVertexOnPosition(point);
			if (v != null && v.getEdges().size() == 0) {
				graph.removeVertex(v);
				editor.removeObjectCloseTo(point);
				break;
			} else if (v != null && v.getEdges().size() != 0) {
				// For simplicity user have to remove edges first
				this.populateDialog("First remove edges!", null);
				break;
			}
			e = getEdgeCloseTo(point);
			if (e != null) {
				graph.removeEdge(e);
				editor.removeObjectCloseTo(point);
			}
			break;
		case VERTEX:
		case NONE:
			break;
		}

	}

	private EdgeAdapter getEdgeCloseTo(Point point) {
		Point2D p1 = null, p2 = null;
		for (CanvasObject o : editor.getObjects()) {
			if (o instanceof EdgeAdapter) {
				if (((EdgeAdapter) o).contains(point)) {
					p1 = ((Line2D)((EdgeAdapter) o).getShape()).getP1();
					p2 = ((Line2D)((EdgeAdapter) o).getShape()).getP2();
					break;
				}
			}
		}

		if (p1 == null || p2 == null )
			return null;

		VertexAdapter v1 = getVertexOnPosition(p1);
		VertexAdapter v2 = getVertexOnPosition(p2);

		if (v1 == null || v2 == null)
			return null;

		EdgeAdapter e = null;
		if ((e = graph.getEdge(v1,v2)) == null) {
			e = graph.getEdge(v2,v1);
		}

		return e;
	}

	/**
	 * Sets editor mode specified. Or unset if called second time with same mode option.
	 * @param mode Mode of the editor to be set.
	 */
	public void setEditor(EditorOptions mode) {
		this.mode = this.mode == mode ? EditorOptions.NONE : mode;
	}

	/**
	 * Where the mouse pointer is right now when mouse button is pressed.
	 * @param point
	 */
	public void possibleEndPoint(Point point) {
		switch (mode) {
		case EDGE:
			end = point;
			final VertexAdapter out = getVertexOnPosition(start);
			if (out != null) {
				editor.removeLast();
				editor.drawObject(new IntermediateEdge(EdgeAdapter.getTouchPoint(start,end,out,start),end) );
			}
			break;
		case EDIT:
			// TODO: drag vertex, together with edges
		case REMOVE:
		case VERTEX:
		case NONE:
			break;
		}

	}

	/**
	 * Gets vertex near position specified.
	 * @param point specifies position.
	 * @return Vertex near that position.
	 */
	public VertexAdapter getVertexOnPosition(Point2D point) {
		Point p = new Point();
		p.x = (int)point.getX();
		p.y = (int)point.getY();
		return getVertexOnPosition(p);
	}

	/**
	 * Gets vertex near position specified.
	 * @param p specifies position.
	 * @return Vertex near that position.
	 */
	public VertexAdapter getVertexOnPosition(Point p) {
		for (VertexAdapter v : graph.getVertices()) {
			int x = Integer.parseInt(v.getAttribute("PositionX"));
			int y = Integer.parseInt(v.getAttribute("PositionY"));
			// some deviation given
			if (Math.pow(p.x-x,2)+Math.pow(p.y-y, 2) <= 200)
				return v;
		}
		return null;
	}

	/**
	 * Gets graph that is presented.
	 * @return graph
	 */
	public GraphAdapter getGraph() {
		return graph;
	}

	/**
	 * Sets graph to be presented.
	 * @param graph
	 */
	public void setGraph(GraphAdapter graph) {
		this.graph = graph;
		editor.clean();
		editor.setObjects(graph.getAll());

	}

	/**
	 * Set graph to be presented from file.
	 * @param f file that holds definition of graph in graphml format.
	 */
	public void setGraph(File f) {
		GraphAdapter s = graph;
		editor.clean();
		graph = new GraphAdapter();

		try {
			graph.read(f);
		} catch (IOException e) {
			// TODO populate dialog
			graph = s;
			e.printStackTrace();
		} finally {
			editor.setObjects(graph.getAll());
		}

	}

	/**
	 * Saves graph to a file in graphml format.
	 * @param f output file.
	 */
	public void saveGraph(File f) {
		try {
			graph.write(f);
		} catch (IOException e){
			// TODO dialog
		}
	}

	private class IntermediateEdge extends CanvasObject {

		private Point out;
		private Point end;

		public IntermediateEdge(Point outPoint, Point endPoint) {
			out = outPoint;
			end = endPoint;
		}

		@Override
		public boolean contains(Point p) {
			// This object should be removed after final end point is set, so this method is just
			// for completeness
			Line2D l = (Line2D) this.getShape();
			if (Line2D.ptLineDist(l.getX1(), l.getY1(),l.getX2(), l.getY2(), p.x, p.y) < 10.0)
				return true;
			return false;
		}

		@Override
		public void initShape() {
			// TODO Auto-generated method stub
			this.setShape(new Line2D.Double(out, end));

		}

		@Override
		public void drawObject(Graphics2D g2) {
			// this is overrided so that arrow is drawn always
			g2.setColor(Color.BLACK);
			if (this.getShape() != null) {
				g2.draw(this.getShape());
				double x1 = ((Line2D) this.getShape()).getX1();
				double x2 = ((Line2D) this.getShape()).getX2();
				double y1 = ((Line2D) this.getShape()).getY1();
				double y2 = ((Line2D) this.getShape()).getY2();
				int x = (int) (x1 + x2)/2;
				int y = (int) (y1 + y2)/2;

				// length of the line
				double length = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
				// angle with x
				double angle = Math.atan2(y2-y1, x2-x1);

				// arrow
				GeneralPath path = new GeneralPath();
				path.moveTo((float)length, 0);
				path.lineTo((float)length - 10, -5);
				path.lineTo((float)length - 7, 0);
				path.lineTo((float)length - 10, 5);
				path.lineTo((float)length, 0);
				path.closePath();

				try {
					AffineTransform af = AffineTransform.getTranslateInstance(x1, y1);
					af.concatenate(AffineTransform.getRotateInstance(angle));
					g2.transform(af);

					Area area = new Area(path);
					g2.fill(area);

					af.invert();
					g2.transform(af);
				} catch (NoninvertibleTransformException e) {
					// TODO: what to do here?
					e.printStackTrace();
				}

				g2.drawString(this.getLabel(),x , y );

			}
		}
	}

}
