package com.njinfotech.algorithmvisualizer;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Edge {
    private Canvas canvas;          // reference to the layout contrainer (drawing surface) in activity

    public Boolean directed;        // whether edge is directed
    public float weight;            // weight or capacity
    public float lineThickness;     // edge line thicness in pixels
    public Point startLocation;     // line start coordinates
    public Point endLocation;       // line end coordinates
    public Paint lineColor;         // line color

    public Node startNode;          // start node
    public Node endNode;            // end node

    // blank constructor
    public Edge() {

    }

    // constructor
    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public Edge(Canvas canv, Node startNode, Node endNode, Boolean isDirected, float edgeWeight, float edgeLineThickness, int edgeColor) {
        canvas = canv;
        directed = isDirected;
        weight = edgeWeight;
        lineColor = new Paint();
        this.startNode = startNode;
        this.endNode = endNode;
        initEdgeCoord(startNode, endNode);
        setLineColor(edgeColor);
        setLineThickness(edgeLineThickness);
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
        canvas.drawLine(startLocation.x, startLocation.y, endLocation.x, endLocation.y, lineColor);

        if (directed) {
            // need to draw an arrow at the end
        }

        // draw weight, and so on
    }

    // if we ever need to select an edge - may be needed for user interaction
    public void select() {

    }

    // set line color
    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setLineColor(int newColor) {
        lineColor.setColor(newColor);
    }

    // set edge weight
    public void setWeight(float newWeight) {
        weight = newWeight;
    }

    // set drawing line thickness
    public void setLineThickness(float newLineThickness) {
        lineColor.setStrokeWidth(newLineThickness);
    }
}
