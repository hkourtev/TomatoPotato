package com.njinfotech.algorithmvisualizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Graph {
    private Activity act;
    private int layoutId;

    public Edge[] edges;
    public Node[] nodes;
    public Boolean[][] adjMatrix;

    private Point screenSize;
    private Display display;
    private Bitmap canvasBg;
    private RelativeLayout drawSpace;
    private Canvas canvas;

    public Graph() {

    }

    public Graph(Activity ptr, int curLayoutId) {
        act = ptr;
        layoutId = curLayoutId;

        initDrawSpace();
    }

    // initialize the background and canvas, screen size and so on
    // this is necessary so we can pass a drawable canvas to the nodes and edges
    // this way their draw functions can draw to the rigth canvas
    public void initDrawSpace() {
        // get current screen size
        display = act.getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);

        // create a paintable bitmap
        canvasBg = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBg);
    }

    // generate pseudo-random graph
    public void generate() {
        int numNodes = 12;
        int numEdges = 29;

        // generate starting adjacency matrix
        adjMatrix = new Boolean[numNodes][numNodes];

        // remove some nodes

        // remove some edges

        // add some random edges

        // update final num nodes and edges

        // instantiate nodes and edges
        Point[] nodePositions = new Point[numNodes];
        nodes = new Node[numNodes];
        edges = new Edge[numEdges];

    }

    // just to test if basic drawing works
    public void generateTest() {
        nodes = new Node[2];
        edges = new Edge[1];

        Point tmpNodePosA = new Point();
        Point tmpNodePosB = new Point();

        tmpNodePosA.x = 200;
        tmpNodePosA.y = 200;
        nodes[0] = new Node(canvas, "A", 0, 100, false, act.getResources().getColor(R.color.colorGreen), tmpNodePosA);

        tmpNodePosB.x = 400;
        tmpNodePosB.y = 500;
        nodes[1] = new Node(canvas, "B", 0, 100, false, act.getResources().getColor(R.color.colorRed), tmpNodePosB);

        edges[0] = new Edge(canvas, nodes[0], nodes[1], false, 0, 5, act.getResources().getColor(R.color.colorPrimary));
    }

    // draws the graph
    public void draw() {
        // draw all nodes
        for (int i=0; i<nodes.length; i++) {
            nodes[i].draw();
        }

        // draw all edges
        for (int j=0; j<edges.length; j++) {
            edges[j].draw();
        }

        // set the layout background to the bitmap we have been drawing on
        drawSpace = (RelativeLayout) act.findViewById(layoutId);
        drawSpace.setBackgroundDrawable(new BitmapDrawable(canvasBg));
    }
}
