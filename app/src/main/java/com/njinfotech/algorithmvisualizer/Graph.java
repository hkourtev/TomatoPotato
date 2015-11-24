package com.njinfotech.algorithmvisualizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Graph {
    private Activity act;
    private int layoutId;

    public Boolean directed;
    public Edge[] edges;
    public Node[] nodes;
    public int[][] adjMatrix;
    public int[] margins;   // 0 = left margin, 1 = top margin, 2 = right margin, 3 = bottom
    Point[] nodePostions;

    private Point screenSize;
    private Display display;
    private Bitmap canvasBg;
    private RelativeLayout drawSpace;
    private Canvas canvas;

    private Set<Integer> edgeWeights = new TreeSet<Integer>();

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

    /*
    // generate graph from provided adjacency list -- incomplete, node positions not calculated
    public void generate(int numNodes, int[][]tmpEdgeList, int radius, int[] screenMargins, Boolean isDirected) {
        directed = isDirected;
        margins = screenMargins;
        int numEdges = tmpEdgeList.length;

        // instantiate nodes and edges
        nodes = new Node[numNodes];
        edges = new Edge[numEdges];

        // generate starting adjacency matrix - set to all =false
        adjMatrix = new int[numNodes][numNodes];
        for (int k=0; k<numNodes; k++) {
            for (int m=0; m<numNodes; m++) {
                adjMatrix[k][m] = -1000000;
            }
        }

        // set true for edges that exist
        for (int k=0; k<numEdges; k++) {
            adjMatrix[tmpEdgeList[k][0]][tmpEdgeList[k][1]] = tmpEdgeList[k][2];
            adjMatrix[tmpEdgeList[k][1]][tmpEdgeList[k][0]] = tmpEdgeList[k][2];
        }

        // create nodes
        Point tmpPnt = new Point(0,0);
        for (int k=0; k<numNodes; k++) {
            nodes[k] = new Node(canvas, Integer.toString(k), 0, 0, radius, act.getResources().getColor(R.color.nodeColor), tmpPnt, null);
        }

        // loop over the adjacency matrix and create edges
        int edgeCount=0;
        for (int p=0; p<numNodes; p++) {
            if (directed) {
                // create all edges
                for (int h = 0; h < numNodes; h++) {
                    if (adjMatrix[p][h] != -1000000) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed, adjMatrix[p][h], 5, act.getResources().getColor(R.color.edgeColor));
                        edgeCount++;
                    }
                }
            } else {
                // use only half of adj matrix
                for (int h=p; h<numNodes; h++) {
                    if (adjMatrix[p][h] != -1000000) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed, adjMatrix[p][h], 5, act.getResources().getColor(R.color.edgeColor));
                        edgeCount++;
                    }
                }
            }
        }
    }*/

    // simplified generate function which loads values from the resource files rather than pass manually
    public void generate(Boolean isDirected) {
        int[] screenMargins = act.getResources().getIntArray(R.array.screenMargins);
        for(int i = 0; i < screenMargins.length; i++){
            screenMargins[i] += 150;
        }
        generate(act.getResources().getInteger(R.integer.graphNumRows),
                act.getResources().getInteger(R.integer.graphNumCols),
                act.getResources().getInteger(R.integer.nodeRadius),
               screenMargins,
                act.getResources().getInteger(R.integer.jitter),
                isDirected);
    }

    // generate a pseudo-random graph that fits well on our limited screen space
    public void generate(int numRows, int numCols, int radius, int[] screenMargins, int jitter, Boolean isDirected) {
        // initialize variables
        directed = isDirected;
        margins = screenMargins;
        int numNodesToRemove = act.getResources().getInteger(R.integer.numNodesToRemove);
        int numEdgesToRemove = act.getResources().getInteger(R.integer.numEdgesToRemove);
        int numEdgesToAdd = act.getResources().getInteger(R.integer.numEdgesToAdd);

        Random randNumGen = new Random();
        int numNodes = numCols*numRows;     // num nodes = row x col
        Point nodeSpacing = new Point();
        nodeSpacing.x = (screenSize.x - numCols*radius*2 - margins[0] - margins[2])/(numCols-1);
        nodeSpacing.y = (screenSize.y - numRows*radius*2 - margins[1] - margins[3])/(numRows-1);

        // to store positions of the nodes
        nodePostions = new Point[numNodes];
        for (int y = 0; y < numRows; y++) {
            for (int x=0; x<numCols; x++) {
                // generate node positions to uniformly fit the space on the screen and
                // add x and y direction jitter = -jitter to +jitter
                nodePostions[y*numCols + x] = new Point();
                nodePostions[y*numCols + x].x = margins[0] + (x*2+1)*radius + x*nodeSpacing.x + (randNumGen.nextInt(jitter*2) - jitter);
                nodePostions[y*numCols + x].y = margins[1] + (y*2+1)*radius + y*nodeSpacing.y + (randNumGen.nextInt(jitter*2) - jitter);
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
        adjMatrix = new int[numNodes][numNodes];
        for (int k=0; k<numNodes; k++) {
            for (int m=0; m<numNodes; m++) {
                adjMatrix[k][m] = -1000000;
            }
        }

        // set true for edges that exist
        for (int k=0; k<numEdges; k++) {
            adjMatrix[tmpEdgeList[k][0]][tmpEdgeList[k][1]] = 1;
        }

        // remove some nodes
        int numEdgesRemoved = 0;
        for (int w=0; w<numNodesToRemove; w++) {
            numEdges = numEdges - removeNode(randNumGen.nextInt(numNodes), numNodes);
            numNodes--;
        }

        // get rid of overlapping edges

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
            nodes[k] = new Node(canvas, Integer.toString(k), 0, 0, radius,
                    act.getResources().getColor(R.color.nodeFillColor),
                    act.getResources().getColor(R.color.nodeBorderColor),
                    act.getResources().getInteger(R.integer.nodeBorderThickness),
                    act.getResources().getColor(R.color.nodeLabelFontColor),
                    act.getResources().getInteger(R.integer.nodeLabelFontSize),
                    act.getResources().getColor(R.color.nodeRankFontColor),
                    act.getResources().getInteger(R.integer.nodeRankFontSize),
                    nodePostions[k], null);
        }

        // loop over the adjacency matrix and create edges
        int edgeCount=0;
        for (int p=0; p<numNodes; p++) {
            if (directed) {
                // create all edges
                for (int h = 0; h < numNodes; h++) {
                    if (adjMatrix[p][h] != -1000000) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed,
                                genEdgeWeight(),
                                act.getResources().getInteger(R.integer.edgeLineThickness),
                                act.getResources().getColor(R.color.edgeColor),
                                act.getResources().getColor(R.color.edgeWeightFontColorFill),
                                act.getResources().getColor(R.color.edgeWeightFontColorBorder),
                                act.getResources().getInteger(R.integer.edgeWeightFontSize));
                        edgeCount++;
                    }
                }
            } else {
                // use only half of adj matrix
                for (int h=p; h<numNodes; h++) {
                    if (adjMatrix[p][h] != -1000000) {
                        edges[edgeCount] = new Edge(canvas, nodes[p], nodes[h], directed,
                                genEdgeWeight(),
                                act.getResources().getInteger(R.integer.edgeLineThickness),
                                act.getResources().getColor(R.color.edgeColor),
                                act.getResources().getColor(R.color.edgeWeightFontColorFill),
                                act.getResources().getColor(R.color.edgeWeightFontColorBorder),
                                act.getResources().getInteger(R.integer.edgeWeightFontSize));
                        edgeCount++;
                    }
                }
            }
        }
    }

    // remove node from adj matrix
    // returns number of edges removed. edges that have a geographically opposing edges are
    // combined so a combination only reduces the # of edges by 1
    private int removeNode(int ind, int numNodes) {
        // assuming node positions in matrix format
        //  0   1   2   3
        //  4   5   6   7
        //  8   9   10  11

        // get edges
        List<Integer> neighbors = getNodeNeighbors(ind, numNodes);
        int numEdgesRemoved = 0;
        int[] edge1Dir, edge2Dir;
        Boolean hasOpposing;

        // take care of edges
        // find every 2 opposing edges and connect them and if an edge doesn't have an opposing edge
        // just remove it.
        for (int f=neighbors.size()-1; f>=0; f--) {
            // pick first edge - to neoughbor(f)
            edge1Dir = getEdgeDirection(ind, neighbors.get(f));
            hasOpposing = false;

            // loop through the rest of the edges and see if we can find opposing edge
            for (int l=neighbors.size()-1; l>=0; l--) {
                if (l!=f) { // no point in testing same edge
                    edge2Dir = getEdgeDirection(ind, neighbors.get(l));

                    // test if opposing
                    if (edge1Dir[0]+edge2Dir[0] == 0 && edge1Dir[1]+edge2Dir[1] == 0) { // opposing
                        // connect nodes f->l and l->f in adj matrix
                        adjMatrix[neighbors.get(f)][neighbors.get(l)] = 1;
                        adjMatrix[neighbors.get(l)][neighbors.get(f)] = 1;

                        // remove both neighbors from list
                        hasOpposing = true;
                        neighbors.remove(f); f--;
                        neighbors.remove(l); l--;
                        break;
                    }
                }
            }

            // if no opposing was found
            if (!hasOpposing) { // remove neighbor f
                neighbors.remove(f);
            }

            numEdgesRemoved++;
        }

        // update adj matrix
        updateAdjMatrixRemoveNode(ind, numNodes);
        updateNodePositionsRemoveNode(ind, numNodes);

        Log.d("LearnActivity", "Removing node" + ind);

        // return double the number since by default we have a directed graph
        return numEdgesRemoved*2;
    }

    private void updateNodePositionsRemoveNode(int ind, int numNodes) {
        Point[] nodePos = new Point[numNodes-1];

        for (int u=0; u<numNodes-1; u++) {
            if (u >= ind) {
                nodePos[u] = nodePostions[u+1];
            } else {
                nodePos[u] = nodePostions[u];
            }
        }

        nodePostions = nodePos;
    }

    public void emptyGraphDraw(){

        canvas.drawColor(Color.WHITE);

        // draw all nodes
        for (int i=0; i<nodes.length; i++) {
            nodes[i].draw();
        }
    }

    // remove row and column = ind
    private void updateAdjMatrixRemoveNode(int ind, int numNodes) {
        int[][] adjMat = new int[numNodes-1][numNodes-1];

        for (int u=0; u<numNodes-1; u++) {
            for (int p=0; p<numNodes-1; p++) {
                if (u >= ind && p >= ind) {
                    adjMat[u][p] = adjMatrix[u+1][p+1];
                } else {
                    if (u >= ind) {
                        adjMat[u][p] = adjMatrix[u+1][p];
                    } else if (p >= ind) {
                        adjMat[u][p] = adjMatrix[u][p+1];
                    } else {
                        adjMat[u][p] = adjMatrix[u][p];
                    }
                }
            }
        }

        // replace adj matrix
        adjMatrix = adjMat;
    }

    private List<Integer> getNodeNeighbors(int ind, int numNodes) {
        List<Integer> neighbors = new ArrayList<Integer>();

        for (int j=0; j<numNodes; j++) {
            if (adjMatrix[ind][j] != -1000000) {
                neighbors.add(j);
            }
        }
        return neighbors;
    }

    // generate random edge weights which do not repeat
    private int genEdgeWeight() {
        Random w = new Random();
        int newWeight = w.nextInt(30);

        if (!edgeWeights.isEmpty()) {
            while (edgeWeights.contains(newWeight)) {
                newWeight = w.nextInt(30);
            }
        }

        edgeWeights.add(newWeight);
        return newWeight;
    }

    // get edge's geographical position
    private int[] getEdgeDirection(int fromNode, int toNode) {
        Point fPos = getNodePos(fromNode);
        Point tPos = getNodePos(toNode);
        int[] dir = new int[] {tPos.x - fPos.x, tPos.y - fPos.y};
        return dir;
    }

    // get node position in terms of row and colum
    private Point getNodePos(int nodeIndex) {
        Point nodePos = new Point();

        int numCols = act.getResources().getInteger(R.integer.graphNumCols);
        int numRows = act.getResources().getInteger(R.integer.graphNumRows);
        for (int y=0; y<numRows; y++) {
            if (nodeIndex<(y+1)*numCols) {
                nodePos.x = nodeIndex - numCols*y;
                nodePos.y = y;
                break;
            }
        }

        return nodePos;
    }

    // get node by label
    public Node getNode(String lbl) {
        for (int j=0; j<nodes.length; j++) {
            if (nodes[j].label == lbl) return nodes[j];
        }
        return null;
    }

    // draws the graph
    public void draw() {
        draw(this.edges);
    }

    // draws the graph with the provided edges
    public void draw(Edge[] edges) {
        // blank the canvas
        canvas.drawColor(Color.WHITE);

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
