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
public class Graph implements Cloneable {
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

    int activeEdgeInd;

    private Set<Integer> edgeWeights = new TreeSet<Integer>();

    public Graph() {

    }

    public Graph(Activity ptr, int curLayoutId) {
        act = ptr;
        layoutId = curLayoutId;

        initDrawSpace();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // in order to be able to copy object by value if needed
        Graph cloned = (Graph)super.clone();
        cloned.edges = cloned.edges.clone();
        cloned.nodes = cloned.nodes.clone();
        return cloned;
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

    // simplified generate function which loads values from the resource files rather than pass manually
    public void generate(Boolean isDirected) {
        generate(act.getResources().getInteger(R.integer.graphNumRows),
                act.getResources().getInteger(R.integer.graphNumCols),
                act.getResources().getInteger(R.integer.nodeRadius),
                act.getResources().getIntArray(R.array.screenMargins),
                act.getResources().getInteger(R.integer.jitter),
                isDirected);
    }

    // generate a pseudo-random graph that fits well on our limited screen space
    public void generate(int numRows, int numCols, int radius, int[] screenMargins, int jitter, Boolean isDirected) {
        // initialize variables
        directed = isDirected;
        margins = screenMargins;
        activeEdgeInd = -1;
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
                                genEdgeWeight(edges.length),
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
                                genEdgeWeight(edges.length),
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

    // draw list of edges in provided order
    public void drawEdgeList(int currEdgeInd, Boolean treeView) {
        // calculate spacing between edges, node radius, edge length
        int leftPadding = act.getResources().getInteger(R.integer.activityLearnEdgesListLeftPadding);
        int bottomPadding = act.getResources().getInteger(R.integer.activityLearnEdgesListBottomPadding);
        int spaceAvailPerEdge = (screenSize.x-leftPadding)/edges.length;
        int nodeRadius = spaceAvailPerEdge/8;
        int edgeLength = nodeRadius*2;
        int padding = nodeRadius;

        // set colors
        Paint nodeColorActive1 = new Paint();
        Paint nodeColorActive2 = new Paint();
        Paint nodeColorExplored = new Paint();
        Paint nodeColorRegular = new Paint();
        Paint nodeLabelColor = new Paint();
        Paint edgeColorExplored = new Paint();
        Paint edgeColorRegular = new Paint();
        Paint edgeWeightColor = new Paint();

        Paint tmpEdgeColor = new Paint();
        Paint tmpNode1Color = new Paint();
        Paint tmpNode2Color = new Paint();

        nodeColorActive1.setColor(act.getResources().getColor(R.color.nodeFillColorSelected1));
        nodeColorActive2.setColor(act.getResources().getColor(R.color.nodeFillColorSelected2));
        nodeColorExplored.setColor(act.getResources().getColor(R.color.nodeFillColorExplored));
        nodeColorRegular.setColor(act.getResources().getColor(R.color.nodeFillColor));
        nodeLabelColor.setColor(act.getResources().getColor(R.color.nodeLabelFontColor));
        edgeColorExplored.setColor(act.getResources().getColor(R.color.edgeColorExplored));
        edgeColorRegular.setColor(act.getResources().getColor(R.color.edgeColor));
        edgeWeightColor.setColor(act.getResources().getColor(R.color.edgeWeightFontColorBorder));

        // calculate edge positions
        Point[] nodePos = new Point[edges.length*2];
        Point edgeMidPoint = new Point();
        for (int y=0; y<edges.length; y++) {
            nodePos[y*2] = new Point();
            nodePos[y*2+1] = new Point();
            if (y == 0) {
                // add extra padding so we don't overlap with the "Edges:" text
                nodePos[y*2].x = leftPadding + nodeRadius;
                nodePos[y*2].y = screenSize.y - bottomPadding - nodeRadius;

                nodePos[y*2+1].x = nodePos[y*2].x + nodeRadius*2 + edgeLength;
                nodePos[y*2+1].y = screenSize.y - bottomPadding - nodeRadius;
            } else {
                nodePos[y*2].x = nodePos[y*2-1].x + padding*2 + nodeRadius*2;
                nodePos[y*2].y = screenSize.y - bottomPadding - nodeRadius;

                nodePos[y*2+1].x = nodePos[y*2].x + nodeRadius*2 + edgeLength;
                nodePos[y*2+1].y = screenSize.y - bottomPadding - nodeRadius;
            }

            // calculate midpoint
            edgeMidPoint.x = (nodePos[y*2].x+nodePos[y*2+1].x)/2;
            edgeMidPoint.y = (nodePos[y*2].y+nodePos[y*2+1].y)/2;

            // draw edges with diff colors for explored, current and not yet explored
            if (y < currEdgeInd) {
                // edge has been explored
                tmpEdgeColor = edgeColorExplored;
                tmpNode1Color = nodeColorExplored;
                tmpNode2Color = nodeColorExplored;
            } else if (y == currEdgeInd) {
                // currently selected edge
                tmpEdgeColor = edgeColorRegular;
                tmpNode1Color = nodeColorActive1;
                tmpNode2Color = nodeColorActive2;

                // draw graph nodes again with right colors
                // draw real node with right color and set color back to regular
                edges[y].startNode.nodeFill = tmpNode1Color;
                edges[y].endNode.nodeFill = tmpNode2Color;

                if (treeView) {
                    edges[y].startNode.drawTreeNode();
                    edges[y].endNode.drawTreeNode();
                } else {
                    edges[y].startNode.draw();
                    edges[y].endNode.draw();
                }
                edges[y].endNode.nodeFill = nodeColorRegular;
                edges[y].startNode.nodeFill = nodeColorRegular;
            } else {
                // not yet explored
                tmpEdgeColor = edgeColorRegular;
                tmpNode1Color = nodeColorRegular;
                tmpNode2Color = nodeColorRegular;
            }

            // draw edge and weight
            canvas.drawLine(nodePos[y * 2].x, nodePos[y * 2].y,
                    nodePos[y * 2 + 1].x, nodePos[y * 2 + 1].y, tmpEdgeColor);
            canvas.drawText((int) edges[y].weight + "",
                    edgeMidPoint.x, edgeMidPoint.y, edgeWeightColor);

            // draw the from node and label
            canvas.drawCircle(nodePos[y*2].x, nodePos[y*2].y, (float) nodeRadius, tmpNode1Color);
            canvas.drawText(edges[y].startNode.label,
                    nodePos[y * 2].x, nodePos[y * 2].y, nodeLabelColor);

            // draw the to node and label
            canvas.drawCircle(nodePos[y*2+1].x, nodePos[y*2+1].y, (float) nodeRadius, tmpNode2Color);
            canvas.drawText(edges[y].endNode.label,
                    nodePos[y*2+1].x, nodePos[y*2+1].y, nodeLabelColor);
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
    private int genEdgeWeight(int numEdges) {
        Random w = new Random();

        // we are trying to assign unique weight to each edge so we need numEdges values
        // add 1 to make sure we don't have 0 as value
        int newWeight = w.nextInt(numEdges)+1;

        if (!edgeWeights.isEmpty()) {
            while (edgeWeights.contains(newWeight)) {
                newWeight = w.nextInt(numEdges)+1;
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

    //-------------------------------------FOREST/TREE RELATED FUNCTIONS --------------------------
    // draws the forest
    public void drawForest() {
        // draw nodes that are not in a set in the right corner
        // figure out vertical space available
        int verticalSpace = screenSize.y - margins[1] - margins[3];
        int defaultRadius = act.getResources().getInteger(R.integer.nodeRadius);
        int horizontalSpace;
        int maxHeight=0, tmpHeight=0;
        int totalWidth=0, tmpWidth=0;
        Point rootNodeRadius = new Point();
        Point tmpPoint = new Point(); tmpPoint.x = 0; tmpPoint.y = 0;
        Point topLeft = new Point();
        int availSpace;

        // blank the canvas
        canvas.drawColor(Color.WHITE);

        // vertical space = totalNumNodes*2*radius + totalNumNodes-1*radius
        // r = verticalspace/(2*totalNumNodes + totalNumNodes - 1);
        int nodeRadius = verticalSpace/(2*nodes.length + nodes.length -1);

        // figure out number of sets -- loop over nodes get the ones who are their own roots
        // store roots in array
        List<Node> roots = new ArrayList<>();
        List<Node> notInASet = new ArrayList<>();
        for (int p=0; p<nodes.length; p++) {
            if (nodes[p].parent == null) {
                if (tmpPoint.x == 0 && tmpPoint.y == 0) {
                    // init tmpPoint to first possible drawing position
                    tmpPoint.x = screenSize.x - margins[2] - nodeRadius;
                    tmpPoint.y = margins[1] + nodeRadius;
                } else {
                    // just increment from previous postion
                    tmpPoint.y = tmpPoint.y + nodeRadius*3;
                }
                nodes[p].positionInTree = new Point(tmpPoint);
                nodes[p].radiusInTree = nodeRadius;
                notInASet.add(nodes[p]);
            } else if (nodes[p].parent == nodes[p]) {
                // get height of each rooted tree and
                tmpHeight = getNodeHeight(nodes[p]);
                if (tmpHeight > maxHeight) maxHeight = tmpHeight;

                // get width of each rooted tree and add up for max width
                tmpWidth = getSubTreeWidth(nodes[p]);
                totalWidth = totalWidth + tmpWidth;
                roots.add(nodes[p]);
            }
        }

        // at this point the nodes not in a set are ready to be drawn
        // we need to figure out the positions of the trees
        if (notInASet.size() == 0) {
            // no need to draw nodes on the right, we can use all the space
            horizontalSpace = screenSize.x - margins[0] - margins[2];
        } else {
            // need to leave space for the nodes on the right
            horizontalSpace = screenSize.x - margins[0] - margins[2]*2 - nodeRadius*2;
        }

        // calculate max possible root node radius based on available space.
        // we have both horizontal and vertical restricitons so we calculate 2 radii and pick
        // the smaller one so that everythign fits

        // horizontal diameter = available space / (totalWidth + numTrees -1)
        //      we add numTrees-1, so there is a node diameter of spacing between trees
        // horizontal radius = diameter/2
        rootNodeRadius.x = horizontalSpace/(2*(totalWidth + roots.size() - 1));
        rootNodeRadius.y = verticalSpace/(2*(maxHeight + maxHeight-1));
        if (rootNodeRadius.x > rootNodeRadius.y) rootNodeRadius.y = rootNodeRadius.x;
        else if (rootNodeRadius.x < rootNodeRadius.y) rootNodeRadius.x = rootNodeRadius.y;

        // still radius shouldn't be larger than the default radius
        if (defaultRadius < rootNodeRadius.x) {
            rootNodeRadius.x = defaultRadius;
            rootNodeRadius.y = defaultRadius;
        }

        // calculate node positions and store their new radiusInTree
        // AND draw nodes and edges
        topLeft.x = margins[0];
        topLeft.y = margins[1];

        for (int k=0; k<roots.size(); k++) {
            availSpace = (int)((double)horizontalSpace*(double)roots.get(k).subTreeWidth/(double)totalWidth);
            calcSubTreeNodePositions(roots.get(k), rootNodeRadius.x, availSpace, totalWidth, topLeft);
            topLeft.x = topLeft.x + availSpace;
        }

        // draw nodes that are not in a set
        for (int k=0; k<notInASet.size(); k++)
            notInASet.get(k).drawTreeNode();
    }

    public void calcSubTreeNodePositions(Node currNode, int currNodeRadius, int availSpace,
                                         int wholeTreeWidth, Point topLeft) {
        Point tmpTopLeft = new Point(topLeft);
        Edge tmpEdge = new Edge();
        int leftOffset, newAvailSpace;

        // calculate position of current node in the middle of available space
        currNode.positionInTree = new Point();
        currNode.radiusInTree = currNodeRadius;

        currNode.positionInTree.x = tmpTopLeft.x + availSpace/2;
        currNode.positionInTree.y = tmpTopLeft.y;

        // draw parent node
        currNode.drawTreeNode();

        // recurse into each child and repeat
        List<Node> children = getNodeChildren(currNode);
        tmpTopLeft.y = tmpTopLeft.y + currNodeRadius*4;

        for (int j=0; j<children.size(); j++) {
            // figure out space needed depending on subtree total width
            newAvailSpace = (int)((double)availSpace * (double)children.get(j).subTreeWidth/(double)currNode.subTreeWidth);

            // calculate subtree node positions
            calcSubTreeNodePositions(children.get(j), currNodeRadius, newAvailSpace, wholeTreeWidth, tmpTopLeft);

            // draw edge to child - child node itself has been drawn during the recursion
            tmpEdge.drawTreeEdge(canvas, currNode.positionInTree, children.get(j).positionInTree,
                    act.getResources().getInteger(R.integer.edgeLineThickness),
                    act.getResources().getColor(R.color.edgeColor), currNodeRadius);

            // advance top left coordinate for next node
            tmpTopLeft.x = tmpTopLeft.x + newAvailSpace;
        }
    }

    public int getSubTreeWidth(Node currNode) {
        List<Node> children = getNodeChildren(currNode);
        int width = 0;

        if (children.size() > 0) {
            // recurse down getting each child's subtree width
            for (int u = 0; u < children.size(); u++) {
                width = width + getSubTreeWidth(children.get(u));
                //if (tmp > max) max = tmp;
            }
            //if (children.size() > max) max = children.size();

            // update current node subtree width with the max and return it
            currNode.subTreeWidth = width;
            return width;
        } else {
            // update subtree width in node, so that we can use it during drawing
            currNode.subTreeWidth = 1;
            return 1;
        }
    }

    public int getNodeHeight(Node currNode) {
        List<Node> children = getNodeChildren(currNode);
        int max = 0, tmp = 0;

        if (children.size() > 0) {
            // recurse down getting each child's height
            for (int u = 0; u < children.size(); u++) {
                tmp = getNodeHeight(children.get(u)) + 1;
                if (tmp > max) max = tmp;
            }

            // update current node with max height and return it
            currNode.heightInTree = max;
            return max;
        } else {
            // update node height, so that we can use it during drawing
            currNode.heightInTree = 1;
            return 1;
        }
    }

    public List<Node> getNodeChildren(Node currNode) {
        List<Node> children = new ArrayList<>();

        for (int h=0; h<nodes.length; h++) {
            if (nodes[h].parent == currNode && nodes[h] != currNode) {
                children.add(nodes[h]);
            }
        }

        // store # of children for later use
        currNode.numChildren = children.size();

        return children;
    }
}
