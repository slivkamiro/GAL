package com.slivkam.graphdemonstrator.swingcomponents;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Circle extends CanvasObject implements Connectable{

    private Point center;

    private List<CurvedArrow> connections;

    public Circle() {
        this(null);

    }

    public Circle(Point center) {
        this.center = center;
        this.connections = new ArrayList<CurvedArrow>();
    }

    @Override
    public boolean contains(Point p) {
        return this.getShape().contains(p);
    }

    @Override
    public void initShape() {
        Ellipse2D e = new Ellipse2D.Double(this.center.getX()-10.0,
                this.center.getY()-10.0,
                20.0,
                20.0);
        this.setShape(e);

    }

    @Override
    public void drawObject(Graphics2D g2) {
        g2.setColor(this.getColor());
        if (this.getShape() != null) {
            g2.draw(this.getShape());
        }
    }

    @Override
    public Point getTouchPoint(Point start, Point end, Point def) {
        // smernica usecky
        double y0 = this.center.getY();
        double x0 = this.center.getX();
        double k = (double)(end.y - start.y) / (double)(end.x - start.x);
        double q = k*(-1*start.x)+start.y;
        double a = 1+k*k;
        double b = 2*k*(q-y0)- 2*x0;
        // c = x0*x0 + (q-y0)^2 - r*r
        double c = x0 * x0 + (q-y0) * (q-y0)-100;
        double d = b*b-4*a*c;

        if(d < 0)
            return def;

        double x1 = (-1*b + Math.sqrt(d))/(2*a);
        double x2 = (-1*b - Math.sqrt(d))/(2*a);
        double y1 = k*x1+q;
        double y2 = k*x2+q;

        if (def == start) {
            // distance from end point to x1 and x2
            if (Math.sqrt(Math.pow(x1-end.x,2)+Math.pow(y1-end.y,2)) <
                    Math.sqrt(Math.pow(x2-end.x,2)+Math.pow(y2-end.y,2)))
                return new Point((int)x1,(int)y1);
            return new Point((int)x2,(int)y2);
        }
        // distance from start point to x1 and x2
        if (Math.sqrt(Math.pow(x1-start.x,2)+Math.pow(y1-start.y,2)) <
                Math.sqrt(Math.pow(x2-start.x,2)+Math.pow(y2-start.y,2)))
            return new Point((int)x1,(int)y1);
        return new Point((int)x2,(int)y2);
    }

    @Override
    public Point getCenterPoint() {
        return this.center;
    }

    @Override
    public List<CurvedArrow> getConnections() {
        return this.connections;
    }

    @Override
    public void addConnection(CurvedArrow connection) {
        this.connections.add(connection);

    }

    @Override
    public void removeConnection(CurvedArrow connection) {
        this.connections.remove(connection);

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Circle) {
            Circle c = (Circle) o;
            return c.getCenterPoint().equals(this.center)
                    && c.getConnections().equals(this.connections);
        }
        return false;
    }
}
