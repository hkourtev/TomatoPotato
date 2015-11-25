package com.njinfotech.algorithmvisualizer;

import android.graphics.Point;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;


/**
 * Created by hkourtev on 11/12/15.
 */
public class Node implements Cloneable {
    private Canvas canvas;

    public String label;
    public double value;
    public int rank;
    public double radius;

    public Node parent = null;
    public Paint nodeFill;
    public Paint nodeBorder;
    public Paint nodeLabel;
    public Paint nodeRank;
    public Point position;
    private Boolean selected = false;

    // blank constructor - not assigning any values
    public Node() {

    }

    public Node(Canvas canv, String lbl, int rnk, double val, double rad, int nodeFillColor,
                int nodeBorderColor, int nodeBorderThickness, int nodeLabelFontColor,
                int nodeLabelFontSize, int nodeRankFontColor, int nodeRankFontSize,
                Point pos, Node prnt) {
        canvas = canv;
        label = lbl;
        value = val;
        rank = rnk;
        radius = rad;
        position = pos;
        nodeFill = new Paint();
        nodeBorder = new Paint();
        nodeLabel = new Paint();
        nodeRank = new Paint();
        parent = prnt;

        setNodeFillColor(nodeFillColor);
        setNodeBorderColor(nodeBorderColor, nodeBorderThickness);
        setNodeLabelFont(nodeLabelFontColor, nodeLabelFontSize);
        setNodeRankFont(nodeRankFontColor, nodeRankFontSize);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // in order to be able to copy object by value if needed
        Node cloned = (Node)super.clone();
        cloned.parent = (Node)cloned.parent.clone();
        return cloned;
    }

    public Boolean isSelected(){
        return selected;
    }

    public void select(){
        if(!selected) {
            Log.d("asd"," " + position.x);
            selected = true;
            canvas.drawCircle(position.x, position.y, (float) radius + 15, nodeBorder);

        }
        else{
            selected = false;
            Paint select = new Paint();
            select.setColor(0xFFFFFFFF);
            canvas.drawCircle(position.x, position.y, (float) radius + 20, select);
        }
    }
    // draw node

    public void draw() {
        // draw the node circle
        canvas.drawCircle(position.x, position.y, (float) radius, nodeFill);

        // draw circle around node to signify it is a tree root
        if(this.parent == this) {
            canvas.drawCircle(position.x, position.y, (float)radius + 2, nodeBorder);
        }

        // draw label
        canvas.drawText(label, position.x, position.y + (float) (radius*0.7), nodeLabel);

        // draw rank
        canvas.drawText("r=" + rank, position.x, (float) (position.y - radius + radius*0.7), nodeRank);
    }

    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setNodeFillColor(int color) {
        nodeFill.setColor(color);
    }

    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setNodeBorderColor(int color, int borderThickness) {
        nodeBorder.setColor(color);
        nodeBorder.setStyle(Paint.Style.STROKE);
        nodeBorder.setStrokeWidth(borderThickness);
    }

    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setNodeLabelFont(int color, int size) {
        nodeLabel.setColor(color);
        nodeLabel.setTextSize(size);
        nodeLabel.setTextAlign(Paint.Align.CENTER);
    }

    // pass the color id returned by act.getResources().getColor(R.color.{the color id})
    public void setNodeRankFont(int color, int size) {
        nodeRank.setColor(color);
        nodeRank.setTextSize(size);
        nodeRank.setTextAlign(Paint.Align.CENTER);
    }

    // return # of neighbors - needs to access the adjacency matrix somehow
    public int getNumNeighbors() {
        int numNbrs = 0;

        return numNbrs;
    }
}
