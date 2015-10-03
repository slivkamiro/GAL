package com.slivkam.graphdemonstrator.swingcomponents;

import java.awt.Point;
import java.util.List;

public interface Connectable {

    /**
     * Computes touch point of this Connectable object and Arrow defined by two points.
     * @param start Arrow start point
     * @param end Arrow end point
     * @param def Default value if no touch point found
     * @return Touch point or default value.
     */
    Point getTouchPoint(Point start, Point end, Point def);

    /**
     *
     * @return Center point of this connectable object.
     */
    Point getCenterPoint();

    void addConnection(CurvedArrow connection);
    void removeConnection(CurvedArrow connection);

    List<CurvedArrow> getConnections();

}
