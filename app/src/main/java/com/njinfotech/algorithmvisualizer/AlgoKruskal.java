package com.njinfotech.algorithmvisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hkourtev on 11/21/15.
 */
public class AlgoKruskal {

    public List<Step> steps;
    public List<Integer> MSTEdges;
    public Graph G;

    // -----------------------------------CONSTRUCTORS ---------------------------------------------
    // when first running algorithm pass graph
    public AlgoKruskal(Graph graph) {
        G = graph;
        steps = new ArrayList<Step>();
        MSTEdges = new ArrayList<Integer>();
    }

    // used when replaying algorithm step by step, pass fresh graph and steps list
    public AlgoKruskal(Graph graph, List<Step> algoSteps) {
        G = graph;
        steps = algoSteps;
        MSTEdges = new ArrayList<Integer>();
        for(int i = 0; i < G.nodes.length;i++){
            G.nodes[i].parent = null;
        }
    }

    // ----------------------------------STEP FUNCTIONS---------------------------------------------
    public void _MakeSet(String nodeName) {
        MakeSet(G.getNode(nodeName));
    }

    public void _Union(String root1, String root2) {
        Link(G.getNode(root1), G.getNode(root2));
    }

    public void _IncreaseRank(String nodeName) {
        G.getNode(nodeName).rank++;
    }

    public void _SortEdges() {
        SortEdges(G.edges, 0, G.edges.length - 1);
    }

    public void _AddMSTEdge(String edgeIndex) {
        AddMSTEdge(Integer.parseInt(edgeIndex));
    }

    public void _Dummy() {
        // dummy fn that does nothing in order to be able to skip a step but still go through it and
        // display its description
    }



    // ------------------------------REGULAR ALGORIHTM FUNCTIONS------------------------------------
    public void MakeSet(Node x) {
        x.parent = x;
        x.rank = 0;
    }

    public void Union(Node x, Node y)
    {
        Node xRoot = FindSet(x);
        Node yRoot = FindSet(y);

        if (xRoot.rank > yRoot.rank) {
            // x root rank > y root rank, no need to increment
            Link(xRoot, yRoot);
            steps.add(new Step("_Union", new Class[]{String.class, String.class}, new String[]{xRoot.label, yRoot.label},
                    "Unite the sets that node " + x.label + " belongs to (tree with root " +
                            xRoot.label + ") and " + y.label + " belongs to (tree with root " +
                            yRoot.label + ") by making node " + xRoot.label +
                            " parent of node " + yRoot.label, true, false));

        } else {
            if (xRoot.rank == yRoot.rank) {
                // unite
                Link(xRoot, yRoot);
                steps.add(new Step("_Union", new Class[] {String.class, String.class}, new String[] {xRoot.label,yRoot.label},
                        "Unite the sets that node " + x.label + " belongs to (tree with root " +
                                xRoot.label + ") and " + y.label + " belongs to (tree with root " +
                                yRoot.label + ") by making node " + xRoot.label +
                                " parent of node " + yRoot.label, true, false));

                // x root rank == y root rank, increase x root rank by 1
                xRoot.rank++;
                steps.add(new Step("_IncreaseRank", new Class[] {String.class}, new String[] {xRoot.label}, "Since the root of the parent tree (node " +
                        xRoot.label + ") has the same rank as the root of the tree being appended (node " +
                        yRoot.label + "), we increase the rank of node " + xRoot.label, false, false));
            }
            else {
                // y root rank > x root rank, make y parent of x
                Link(yRoot, xRoot);
                steps.add(new Step("_Union", new Class[]{String.class, String.class}, new String[]{yRoot.label, xRoot.label},
                        "Unite the sets that node " + y.label + " belongs to (tree with root " +
                                yRoot.label + ") and " + x.label + " belongs to (tree with root " +
                                xRoot.label + ") by making node " + yRoot.label +
                                " parent of node " + xRoot.label, true, false));

            }
        }
    }

    public void Link(Node x, Node y)
    {
        y.parent = x;
    }

    public Node FindSet(Node x)
    {
        if(x != x.parent)
        {
            x.parent = FindSet(x.parent);
        }
        return x.parent;
    }

    // Randomized QuickSort Start
    public int Partition(Edge[] A, int p, int r)
    {
        float x = A[r].weight;
        int i = p-1;

        for(int j = p; j < r; j++)
        {
            if(A[j].weight <= x)
            {
                i = i+1;

                Edge e = A[i];
                A[i] = A[j];
                A[j] = e;

            }
        }

        Edge e = A[i + 1];
        A[i + 1] = A[r];
        A[r] = e;

        return i+1;
    }

    public int RandomPartition(Edge[] A, int p, int r)
    {
        Random rand = new Random();

        int i = rand.nextInt(r-p + 1) + p; // random number between p and r (inclusive both)

        Edge e = A[r];
        A[r] = A[i];
        A[i] = e;

        return Partition(A, p, r);
    }

    public void SortEdges(Edge[] A, int p, int r)
    {
        if(p < r)
        {
            int q = RandomPartition(A, p, r);
            SortEdges(A, p, q-1);
            SortEdges(A, q+1, r);
        }
    }
    // Randomized QuickSort End

    public void AddMSTEdge(int edgeIndex) {
        MSTEdges.add(edgeIndex);
    }

    // execute algorithm and generate MST edges list and steps along the way
    public void MST()
    {
        // make a set for each node

        for (int i = 0; i < G.nodes.length; i++)
        {
            MakeSet(G.nodes[i]);
            steps.add(new Step("_MakeSet", new Class[] {String.class}, new String[] {G.nodes[i].label},
                        "Create set of 1 with root node " + G.nodes[i].label, false, false));
        }

        // sort edges
        _SortEdges();
        steps.add(new Step("_SortEdges", new Class[] {}, new String[] {},
                "Sort all graph edges by weight in ascending order", true, false));

        for (int i = 0; i < G.edges.length; i++) {
            if(FindSet(G.edges[i].startNode) != FindSet(G.edges[i].endNode)) {
                // add edge to the MST 
                AddMSTEdge(i);
                steps.add(new Step("_AddMSTEdge", new Class[] {String.class}, new String[] {Integer.toString(i)}, "Adding edge (" +
                        G.edges[i].startNode.label + "," + G.edges[i].endNode.label +
                        ") to MST", false, false));
                
                // unite sets - steps for union added inside function
                Union(G.edges[i].startNode, G.edges[i].endNode);
            } else {
                steps.add(new Step("_Dummy", new Class[] {}, new String[] {}, "Nodes " +
                        G.edges[i].startNode.label + " and " + G.edges[i].endNode.label +
                        " belong to the same set.\n\nCannot unite.\n\n" +
                        "Skipping edge.", true, false));
            }
        }

        // done
        int MSTweight = 0;
        for (int j=0; j<MSTEdges.size(); j++)
            MSTweight+=G.edges[MSTEdges.get(j)].weight;

        steps.add(new Step("_Dummy()", new Class[] {}, new String[] {}, "All edges explored.\n\n" +
                "Minimum Weight Spanning Tree complete.\n\nWeight: " + MSTweight + "\n\n" +
                "Press QUIT to go back", true, false));
    }
}
