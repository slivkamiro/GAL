package com.slivkam.graphdemonstrator.swingcomponents;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class CurvedArrow extends CanvasObject{


    private Connectable source;
    private Connectable destination;

    private boolean loop;

    public CurvedArrow() {
        this.source = null;
        this.destination = null;
        this.loop = false;
    }

    public CurvedArrow(Connectable src, Connectable dst) {
        this.source = src;
        this.destination = dst;
        this.source.addConnection(this);
        this.destination.addConnection(this);
        this.loop = src.equals(dst);
    }

    public Connectable getSource() {
        return this.source;
    }

    public void setSource(Connectable source) {
        if (this.source != null) {
            this.source.removeConnection(this);
        }
        this.source = source;
        this.source.addConnection(this);
        this.loop = source.equals(this.destination);
    }

    public Connectable getDestination() {
        return this.destination;
    }

    public void setDestination(Connectable destination) {
        if (this.destination != null) {
            this.destination.removeConnection(this);
        }
        this.destination = destination;
        this.destination.addConnection(this);
        this.loop = this.source.equals(destination);
    }

    @Override
    public boolean contains(Point p) {
        return this.getShape().contains(p);
    }

    @Override
    public void initShape() {
        CubicCurve2D l = new CubicCurve2D.Double();

        Point src =this.source.getTouchPoint(this.source.getCenterPoint(), this.destination.getCenterPoint(), this.source.getCenterPoint());
        Point dst =this.destination.getTouchPoint(this.source.getCenterPoint(), this.destination.getCenterPoint(), this.destination.getCenterPoint());

        if (this.loop) {
            src = new Point(this.source.getCenterPoint().x, this.source.getCenterPoint().y + 10);
            dst = new Point(this.destination.getCenterPoint().x + 10, this.destination.getCenterPoint().y);
        }

        double x1 = src.getX();
        double x2 = dst.getX();
        double y1 = src.getY();
        double y2 = dst.getY();
        int x = (int) (x1 + x2)/2;
        int y = (int) (y1 + y2)/2;

        Point2D cp1;
        Point2D cp2;
        if (this.loop) {
            cp2 = new Point(x+25,y);
            cp1 = new Point(x,y+25);
        } else {
            // adjust control point coords
            double cpx1;
            double cpy1;
            double cpx2;
            double cpy2;

            double dx = Math.abs(x2-x1);
            double dy = Math.abs(y2-y1);

            if (x2 > x1) {
                cpx1 = x + dy/4 - dx/4;
                cpx2 = x + dy/4 + dx/4;
            } else {
                cpx1 = x - dy/4 + dx/4;
                cpx2 = x - dy/4 - dx/4;
            }

            if (y2 > y1) {
                cpy1 = y - dx/4 - dy/4;
                cpy2 = y - dx/4 + dy/4;
            } else {
                cpy1 = y + dx/4 + dy/4;
                cpy2 = y + dx/4 - dy/4;
            }

            cp1 = new Point2D.Double(cpx1, cpy1);
            cp2 = new Point2D.Double(cpx2, cpy2);

        }

        l.setCurve(src, cp1, cp2, dst);
        this.setShape(l);


    }

    @Override
    public void drawObject(Graphics2D g2) {
        g2.setColor(this.getColor());
        if (this.getShape() != null) {

            /* draw edge */
            g2.draw(this.getShape());

            /* construct arrow */
            double x1 = ((CubicCurve2D) this.getShape()).getCtrlX2();
            double x2 = ((CubicCurve2D) this.getShape()).getX2();
            double y1 = ((CubicCurve2D) this.getShape()).getCtrlY2();
            double y2 = ((CubicCurve2D) this.getShape()).getY2();

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

        }
    }

    public Point2D getP1() {
        return ((CubicCurve2D) this.getShape()).getP1();
    }

    public Point2D getP2() {
        return ((CubicCurve2D) this.getShape()).getP2();
    }

    public Point2D getCtrlP1() {
        return ((CubicCurve2D) this.getShape()).getCtrlP1();
    }

    public Point2D getCtrlP2() {
        return ((CubicCurve2D) this.getShape()).getCtrlP1();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CurvedArrow) {
            CurvedArrow ca = (CurvedArrow) o;
            return this.source.equals(ca.getSource()) && this.destination.equals(ca.getDestination());
        }
        return false;

    }

}
