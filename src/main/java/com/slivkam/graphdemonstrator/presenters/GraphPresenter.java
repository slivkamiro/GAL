package com.slivkam.graphdemonstrator.presenters;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.slivkam.graphdemonstrator.model.EdgeAdapter;
import com.slivkam.graphdemonstrator.model.GraphAdapter;
import com.slivkam.graphdemonstrator.model.VertexAdapter;
import com.slivkam.graphdemonstrator.swingcomponents.CanvasObject;
import com.slivkam.graphdemonstrator.swingcomponents.Connectable;
import com.slivkam.graphdemonstrator.swingcomponents.CurvedArrow;

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

        void removeObject(CanvasObject o);

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

    private Point movingVertexPosition;

    /**
     * Default constructor. Present empty graph.
     */
    private GraphPresenter() {
        super();
        this.graph = new GraphAdapter();
        this.movingVertexPosition = null;
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
        switch (this.mode) {
        case EDGE:
            this.end = point;
            // add edge to model and draw it on canvas
            VertexAdapter out = this.getVertexOnPosition(this.start);
            VertexAdapter in = this.getVertexOnPosition(this.end);
            if (out != null && in != null) {
                this.editor.removeLast();
                EdgeAdapter e = this.graph.addEdge(out, in);
                e.setPoints(out, in);
                this.editor.drawObject(e);
            }
            break;
        case VERTEX:
            if (this.movingVertexPosition != null) {
                this.movingVertexPosition = null;
                break;
            }
            // add vertex to model
            VertexAdapter v = this.graph.addVertex(point);
            // draw vertex to canvas
            this.editor.drawObject(v);
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
        switch (this.mode) {
        case EDGE:
            this.start = point;
            this.end = point;
            v = this.getVertexOnPosition(this.start);
            if (v == null) break;
            this.editor.drawObject(new CurvedArrow(v,new ConnectablePoint(this.end)));
            //editor.drawShape(new Line2D.Double(start, end));
            break;
        case EDIT:
            v = this.getVertexOnPosition(point);
            if (v != null) {
                this.populateDialog(v, this.editor.getPresenterFactory().createVertexPresenter());
                this.editor.editObject(v);
                break;
            }
            e = this.getEdgeCloseTo(point);
            if (e != null) {
                this.populateDialog(e, this.editor.getPresenterFactory().createEdgePresenter());
                this.editor.editObject(e);
            }
            break;
        case REMOVE:
            v = this.getVertexOnPosition(point);
            if (v != null && v.getEdges().size() == 0) {
                this.graph.removeVertex(v);
                this.editor.removeObjectCloseTo(point);
                break;
            } else if (v != null && v.getEdges().size() != 0) {
                for (EdgeAdapter edgeToRemove : v.getEdges()) {
                    this.graph.removeEdge(edgeToRemove);
                }
                this.graph.removeVertex(v);
                this.editor.clean();
                this.editor.setObjects(this.graph.getAll());
                break;
            }
            e = this.getEdgeCloseTo(point);
            if (e != null) {
                this.graph.removeEdge(e);
                this.editor.removeObjectCloseTo(point);
            }
            break;
        case VERTEX:
            // check if there is already vertex at this position
            // if there is one, lets move it
            v = this.getVertexOnPosition(point);
            if (v == null) break;
            this.movingVertexPosition = point;
        case NONE:
            break;
        }

    }

    private EdgeAdapter getEdgeCloseTo(Point point) {
        //Point2D p1 = null, p2 = null;
        EdgeAdapter e = null;
        for (CanvasObject o : this.editor.getObjects()) {
            if (o instanceof EdgeAdapter) {
                if (((EdgeAdapter) o).contains(point)) {
                    e = (EdgeAdapter) o;
                    //p1 = ((Line2D)((EdgeAdapter) o).getShape()).getP1();
                    //p2 = ((Line2D)((EdgeAdapter) o).getShape()).getP2();
                    break;
                }
            }
        }

        //        if (p1 == null || p2 == null )
        //            return null;
        //
        //        VertexAdapter v1 = this.getVertexOnPosition(p1);
        //        VertexAdapter v2 = this.getVertexOnPosition(p2);
        //
        //        if (v1 == null || v2 == null)
        //            return null;
        //
        //
        //        if ((e = this.graph.getEdge(v1,v2)) == null) {
        //            e = this.graph.getEdge(v2,v1);
        //        }

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
        switch (this.mode) {
        case EDGE:
            this.end = point;
            final VertexAdapter out = this.getVertexOnPosition(this.start);
            if (out != null) {
                this.editor.removeLast();
                this.editor.drawObject(new CurvedArrow(out,new ConnectablePoint(this.end)) );
            }
            break;
        case EDIT:
            // TODO: drag vertex, together with edges
        case REMOVE:
        case VERTEX:
            if (this.movingVertexPosition == null) break;
            VertexAdapter v = this.getVertexOnPosition(this.movingVertexPosition);
            this.editor.removeObject(v);
            v.setAttribute("PositionX", String.valueOf(point.x));
            v.setAttribute("PositionY", String.valueOf(point.y));
            this.editor.drawObject(v);
            for (EdgeAdapter e : v.getEdges()) {
                this.editor.removeObject(e);
                this.editor.drawObject(e);
            }
            this.movingVertexPosition = point;
        case NONE:
            break;
        }

    }

    private void tryRemoveEdge(EdgeAdapter e) {
        //TODO this does not work yet
        e.initShape();
        Shape curve = e.getShape();
        Point p = new Point((int)((CubicCurve2D)curve).getCtrlX1(), (int)((CubicCurve2D)curve).getCtrlY1());
        if (this.getEdgeCloseTo(p)!= null) {
            this.editor.removeObjectCloseTo(p);
        }
        else {
            p = new Point((int)((CubicCurve2D)curve).getCtrlX2(), (int)((CubicCurve2D)curve).getCtrlY2());
            if (this.getEdgeCloseTo(p)!= null) {
                this.editor.removeObjectCloseTo(p);
            } else {
                p =  new Point((int)((CubicCurve2D)curve).getX1(), (int)((CubicCurve2D)curve).getY1());
                if (this.getEdgeCloseTo(p)!= null) {
                    this.editor.removeObjectCloseTo(p);
                } else {
                    p =  new Point((int)((CubicCurve2D)curve).getX2(), (int)((CubicCurve2D)curve).getY2());
                    this.editor.removeObjectCloseTo(p);
                }

            }
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
        return this.getVertexOnPosition(p);
    }

    /**
     * Gets vertex near position specified.
     * @param p specifies position.
     * @return Vertex near that position.
     */
    public VertexAdapter getVertexOnPosition(Point p) {
        for (VertexAdapter v : this.graph.getVertices()) {
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
        return this.graph;
    }

    /**
     * Sets graph to be presented.
     * @param graph
     */
    public void setGraph(GraphAdapter graph) {
        this.graph = graph;
        this.editor.clean();
        this.editor.setObjects(graph.getAll());

    }

    /**
     * Set graph to be presented from file.
     * @param f file that holds definition of graph in graphml format.
     */
    public void setGraph(File f) {
        GraphAdapter s = this.graph;
        this.editor.clean();
        this.graph = new GraphAdapter();

        try {
            this.graph.read(f);
        } catch (IOException e) {
            this.populateDialog("Could not read graph from selected file.\n" + e.getMessage(), null);
            this.graph = s;
            e.printStackTrace();
        } finally {
            this.editor.setObjects(this.graph.getAll());
        }

    }

    /**
     * Saves graph to a file in graphml format.
     * @param f output file.
     */
    public void saveGraph(File f) {
        try {
            this.graph.write(f);
        } catch (IOException e){
            this.populateDialog("Could not save graph.", null);
        }
    }

    /**
     *
     * @author Miroslav
     *
     */
    private class ConnectablePoint implements Connectable {

        private Point center;

        public ConnectablePoint(Point c) {
            this.center = c;
        }

        @Override
        public Point getTouchPoint(Point start, Point end, Point def) {
            return this.center;
        }

        @Override
        public Point getCenterPoint() {
            return this.center;
        }

        @Override
        public List<CurvedArrow> getConnections() {
            return null;
        }

        @Override
        public void addConnection(CurvedArrow connection) {

        }

        @Override
        public void removeConnection(CurvedArrow connection) {

        }
    }
}
