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
    public int rank;
    public double radius;

    public Node parent;
    public Paint nodeColor;
    public Point position;

    // blank constructor - not assigning any values
    public Node() {

    }

    public Node(Canvas canv, String lbl, int rnk, double val, double rad, int color, Point pos, Node parent) {
        canvas = canv;
        label = lbl;
        value = val;
        rank = rnk;
        radius = rad;
        position = pos;
        nodeColor = new Paint();
        //make it parent.
        this.parent = this;
        setNodeColor(color);

    }

    // draw node
    public void draw() {
        // draw the circle
        canvas.drawCircle(position.x, position.y, (float)radius, nodeColor);
        Paint text = new Paint();
        text.setTextSize(30);
        canvas.drawText(label, position.x, position.y, text);
        if(this.parent == this){
            Paint line = new Paint();
            line.setStyle(Paint.Style.STROKE);
            line.setStrokeWidth(7);
            line.setColor(0xFFAA0000);
            /* for drawing a rectangle
            line.setStrokeWidth(7);
            line.setColor(0xffff0000);

            canvas.drawLine((int)(position.x - radius - 15), (int)(position.y -radius - 15), (int)(position.x - radius-15), (int)(position.y + radius + 15), line);
            canvas.drawLine((int)(position.x - radius - 15), (int)(position.y - radius - 15), (int)(position.x + radius + 15), (int)(position.y - radius - 15), line);
            canvas.drawLine((int)(position.x - radius - 15), (int)(position.y + radius + 15), (int)(position.x + radius + 15), (int)(position.y + radius + 15), line);
            canvas.drawLine((int)(position.x + radius + 15), (int)(position.y -radius - 15), (int)(position.x + radius + 15), (int)(position.y + radius + 15), line);
            */
            canvas.drawCircle(position.x, position.y, (float)radius + 10, line);
        }
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
