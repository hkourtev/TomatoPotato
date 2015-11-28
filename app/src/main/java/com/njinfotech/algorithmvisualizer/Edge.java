package com.njinfotech.algorithmvisualizer;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Edge implements Cloneable, Comparable {
    private Canvas canvas;          // reference to the layout contrainer (drawing surface) in activity

    public Boolean directed;        // whether edge is directed
    public float weight;            // weight or capacity

    public float lineThickness;     // edge line thicness in pixels
    public Point startLocation;     // line start coordinates
    public Point endLocation;       // line end coordinates
    public Paint edgeLineColor;     // line color and stroke
    public Paint edgeWeightColorFill;       // edge rank font and color
    public Paint edgeWeightColorBorder;     // edge rank font and color

    public Node startNode;          // start node
    public Node endNode;            // end node

    // blank constructor
    public Edge() {

    }

    // constructor
    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public Edge(Canvas canv, Node startNode, Node endNode, Boolean isDirected,
                float edgeWeight, float edgeLineThickness, int edgeColor,
                int weightFontColorFill, int weightFontColorBorder, int weightFontSize) {
        canvas = canv;
        directed = isDirected;
        weight = edgeWeight;
        edgeLineColor = new Paint();
        edgeWeightColorFill = new Paint();
        edgeWeightColorBorder = new Paint();
        lineThickness = edgeLineThickness;
        this.startNode = startNode;
        this.endNode = endNode;
        initEdgeCoord(startNode, endNode);
        setEdgeColor(edgeColor, edgeLineThickness);
        setEdgeWeightFont(weightFontColorFill, weightFontColorBorder, weightFontSize);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // in order to be able to copy object by value if needed
        Edge cloned = (Edge)super.clone();
        cloned.startNode = (Node)cloned.startNode.clone();
        cloned.endNode = (Node)cloned.endNode.clone();
        return cloned;
    }

    // initializes edge coordinates after node and everything else has been assigned
    public void initEdgeCoord(Node startNode, Node endNode) {
        // initialize variables
        startLocation = new Point();
        endLocation = new Point();

        // calculate start and end position using node positions coordinates
        // first calculate position vector of end node in reference to start node
        Point edgeDirVector = new Point();
        edgeDirVector.x = endNode.position.x - startNode.position.x;
        edgeDirVector.y = endNode.position.y - startNode.position.y;

        // get position vector lenght, to be used later to calculate unit vector
        double edgeDirLen = Math.sqrt(Math.pow(edgeDirVector.x,2) + Math.pow(edgeDirVector.y,2));

        // set start point to start node center + radius (so that we start drawing from border not center
        startLocation.x = startNode.position.x + (int)(((double)edgeDirVector.x/edgeDirLen)*startNode.radius);
        startLocation.y = startNode.position.y + (int)(((double)edgeDirVector.y/edgeDirLen)*startNode.radius);

        // set end location = start location + position vector - radius
        endLocation.x = startNode.position.x + edgeDirVector.x - (int)(((double)edgeDirVector.x/edgeDirLen)*startNode.radius);
        endLocation.y = startNode.position.y + edgeDirVector.y - (int)(((double)edgeDirVector.y/edgeDirLen)*startNode.radius);
    }

    // draw the line to the screen
    public void draw() {
        // draw the line
        canvas.drawLine(startLocation.x, startLocation.y, endLocation.x, endLocation.y, edgeLineColor);

        // need to draw an arrow at the end
        if (directed) {

        }

        // displace edge weight label so it is not on top of the edge
        Point edgeMidPoint = new Point((startLocation.x + endLocation.x)/2,
                (startLocation.y + endLocation.y)/2);
        Point orthoVector = new Point(-(endLocation.y-startLocation.y), endLocation.x-startLocation.x);
        double edgeLength = Math.sqrt(Math.pow(orthoVector.x,2) + Math.pow(orthoVector.y, 2));
/*        Point displacement = new Point(edgeMidPoint.x +
                (int)(((edgeVector.y)/edgeLength) < 0.5 ? (edgeVector.y*2)/edgeLength : (edgeVector.y)/edgeLength),
                edgeMidPoint.y +
                        (int)(((-edgeVector.x)/edgeLength) < 0.5 ? ((-edgeVector.x*2)/edgeLength) : ((-edgeVector.x)/edgeLength))); */
        double dispX = (orthoVector.y)*lineThickness*5/edgeLength;
        double dispY = (orthoVector.x)*lineThickness*5/edgeLength;
        Point displacement = new Point((int)dispX + edgeMidPoint.x,
                (int)dispY + edgeMidPoint.y);

        // draw weight
        canvas.drawText((int)weight+"", edgeMidPoint.x, edgeMidPoint.y, edgeWeightColorBorder);
        canvas.drawText((int)weight+"", edgeMidPoint.x, edgeMidPoint.y, edgeWeightColorFill);
    }

    // if we ever need to select an edge - may be needed for user interaction
    public void select() {

    }

    // set line color
    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setEdgeColor(int newColor, float newLineThickness) {
        edgeLineColor.setColor(newColor);
        edgeLineColor.setStrokeWidth(newLineThickness);
    }

    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setEdgeWeightFont(int colorFill, int colorBorder, int size) {
        edgeWeightColorFill.setColor(colorFill);
        edgeWeightColorFill.setTextSize(size);
        edgeWeightColorFill.setTextAlign(Paint.Align.CENTER);

        edgeWeightColorBorder.setColor(colorBorder);
        edgeWeightColorBorder.setTextSize((int) (size * 1.2));
        edgeWeightColorBorder.setTextAlign(Paint.Align.CENTER);
        edgeWeightColorBorder.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    public int compareTo(Object another) {
        Edge e = (Edge)another;
        float anotherWeight = e.weight, thisWeight = weight;
        return anotherWeight > thisWeight ? 1 : anotherWeight == thisWeight ? 0 : -1;
    }
}
