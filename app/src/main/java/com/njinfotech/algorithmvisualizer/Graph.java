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

import java.util.Random;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Graph {
    private Activity act;
    private int layoutId;

    public Boolean directed;
    public Edge[] edges;
    public Node[] nodes;
    public Boolean[][] adjMatrix;
    public int[] margins;   // 0 = left margin, 1 = top margin, 2 = right margin, 3 = bottom

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
    public void generate(int numRows, int numCols, int radius, int[] screenMargins, int jitter, Boolean isDirected) {
        directed = isDirected;
        margins = screenMargins;
        int numNodesToRemove = 3;

        Random jitterX = new Random();
        Random jitterY = new Random();
        int numNodes = numCols*numRows;     // num nodes = row x col
        Point nodeSpacing = new Point();
        nodeSpacing.x = (screenSize.x - numCols*radius*2 - margins[0] - margins[2])/(numCols-1);
        nodeSpacing.y = (screenSize.y - numRows*radius*2 - margins[1] - margins[3])/(numRows-1);

        // to store positions of the nodes
        Point[] nodePostions = new Point[numNodes];
        for (int y = 0; y < numRows; y++) {
            for (int x=0; x<numCols; x++) {
                // generate node positions to uniformly fit the space on the screen and
                // add x and y direction jitter = -jitter to +jitter
                nodePostions[y*numCols + x] = new Point();
                nodePostions[y*numCols + x].x = margins[0] + (x*2+1)*radius + x*nodeSpacing.x + (jitterX.nextInt(jitter*2) - jitter);
                nodePostions[y*numCols + x].y = margins[1] + (y*2+1)*radius + y*nodeSpacing.y + (jitterY.nextInt(jitter*2) - jitter);
            }
        }

        // edges 58x2 ---- need to assign them a magnetic pole direction
        int tmpEdgeList[][] = {{0,1}, {0,5}, {0,4}, {1,0}, {1,4}, {1,5}, {1,6}, {1,2}, {2,1}, {2,5},
                {2,6}, {2,7}, {2,3}, {3,2}, {3,6}, {3,7}, {4,0}, {4,1}, {4,5}, {4,9}, {4,8}, {5,4},
                {5,0}, {5,1}, {5,2}, {5,6}, {5,10}, {5,9}, {5,8}, {6,5}, {6,1}, {6,2}, {6,3}, {6,7},
                {6,11}, {6,10}, {6,9}, {7,6}, {7,2}, {7,3}, {7,11}, {7,10}, {8,4}, {8,5}, {8,9},
                {9,8}, {9,4}, {9,5}, {9,6}, {9,10}, {10,9}, {10,5}, {10,6}, {10,7}, {10,11},
                {11,10}, {11,6}, {11,7}};
        int numEdges = tmpEdgeList.length;

        // generate starting adjacency matrix - set to all =false
        adjMatrix = new Boolean[numNodes][numNodes];
        for (int k=0; k<numNodes; k++) {
            for (int m=0; m<numNodes; m++) {
                adjMatrix[k][m] = false;
            }
        }

        // set true for edges that exist
        for (int k=0; k<numEdges; k++) {
            adjMatrix[tmpEdgeList[k][0]][tmpEdgeList[k][1]] = true;
        }

        // remove some nodes

        // remove some edges

        // add some random edges

        // update final num nodes and edges

        // instantiate nodes and edges
        nodes = new Node[numNodes];

        if (directed)
            edges = new Edge[numEdges];
        else
            edges = new Edge[numEdges/2];

        // create nodes
        for (int k=0; k<numNodes; k++) {
            nodes[k] = new Node(canvas, Integer.toString(k), 0, radius, false, act.getResources().getColor(R.color.nodeColor), nodePostions[k]);
        }

        // loop over the adjacency matrix and create edges
        int edgeCount=0;
        for (int p=0; p<numNodes; p++) {
            if (directed) {
                // create all edges
                for (int h = 0; h < numNodes; h++) {
                    if (adjMatrix[p][h]) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed, 0, 5, act.getResources().getColor(R.color.edgeColor));
                        edgeCount++;
                    }
                }
            } else {
                // use only half of adj matrix
                for (int h=p; h<numNodes; h++) {
                    if (adjMatrix[p][h]) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed, 0, 5, act.getResources().getColor(R.color.edgeColor));
                        edgeCount++;
                    }
                }
            }
        }
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
