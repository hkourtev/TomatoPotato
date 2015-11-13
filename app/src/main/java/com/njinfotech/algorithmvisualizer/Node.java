package com.njinfotech.algorithmvisualizer;

import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;


/**
 * Created by hkourtev on 11/12/15.
 */
public class Node {
    private Canvas canvas;

    public String label;
    public double value;
    public double radius;
    public Boolean root;

    public Paint nodeColor;
    public Point position;

    // blank constructor - not assigning any values
    public Node() {

    }

    public Node(Canvas canv, String lbl, double val, double rad, Boolean isRoot, int color, Point pos) {
        canvas = canv;
        label = lbl;
        value = val;
        radius = rad;
        root = isRoot;
        position = pos;
        nodeColor = new Paint();
        setNodeColor(color);
    }

    // draw node
    public void draw() {
        // draw the circle
        canvas.drawCircle(position.x, position.y, (float)radius, nodeColor);

        // draw label/node name, value, etc
    }

    // whenever we need to select node
    public void select() {

    }

    // set node color
    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setNodeColor(int newNodeColor) {
        nodeColor.setColor(newNodeColor);
    }

    // for some algorithms nodes need to store a value
    public void setValue(float newValue) {
        value = newValue;
    }

    // return # of neighbors - needs to access the adjacency matrix somehow
    public int getNumNeighbors() {
        int numNbrs = 0;

        return numNbrs;
    }
}
