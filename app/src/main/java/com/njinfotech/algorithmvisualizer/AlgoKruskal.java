package com.njinfotech.algorithmvisualizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hkourtev on 11/21/15.
 */
public class AlgoKruskal {

    private StepRecorder sr;
    private Graph G;
    private int currEdgeInd;

    // -----------------------------------CONSTRUCTORS ---------------------------------------------
    // when first running algorithm pass graph
    public AlgoKruskal(Graph graph, StepRecorder sr) {
        try {
            G = (Graph) graph.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sr = sr;
        currEdgeInd = -1;
    }

    public void generateSteps(){
        MST();
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
            sr.addStep("_union", "Unite the sets that node " + x.label + " belongs to (tree with root " +
                    xRoot.label + ") and " + y.label + " belongs to (tree with root " +
                    yRoot.label + ") by making node " + xRoot.label +
                    " parent of node " + yRoot.label, new String[]{xRoot.label, yRoot.label});

        } else {
            if (xRoot.rank == yRoot.rank) {
                // unite
                Link(xRoot, yRoot);
                sr.addStep("_union", "Unite the sets that node " + x.label + " belongs to (tree with root " +
                        xRoot.label + ") and " + y.label + " belongs to (tree with root " +
                        yRoot.label + ") by making node " + xRoot.label +
                        " parent of node " + yRoot.label, new String[] {xRoot.label,yRoot.label});

                // x root rank == y root rank, increase x root rank by 1
                xRoot.rank++;
                sr.addStep("_IncreaseRank", "Since the root of the parent tree (node " +
                        xRoot.label + ") has the same rank as the root of the tree being appended (node " +
                        yRoot.label + "), we increase the rank of node " + xRoot.label, new String[] {xRoot.label});
            }
            else {
                // y root rank > x root rank, make y parent of x
                Link(yRoot, xRoot);
                sr.addStep("_union","Unite the sets that node " + y.label + " belongs to (tree with root " +
                                yRoot.label + ") and " + x.label + " belongs to (tree with root " +
                                xRoot.label + ") by making node " + yRoot.label +
                                " parent of node " + xRoot.label,new String[]{yRoot.label, xRoot.label});

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


    // execute algorithm and generate MST edges list and steps along the way
    public void MST() {
        // make a set for each node
        for (int i = 0; i < G.nodes.length; i++) {
            MakeSet(G.nodes[i]);
            sr.addStep("_makeSet", "Create set of 1 with root node " + G.nodes[i].label, new String[]{G.nodes[i].label});
        }

        // sort edges
        //It's throwing some error when I try to use the quick sort here. So...
        Arrays.sort(G.edges);

        sr.addStep("_SortEdges", "Sort all graph edges by weight in ascending order", new String[]{});

        for (int i = 0; i < G.edges.length; i++) {
            // select edge
            sr.addStep("_SelectEdge", "Select edge (" +
                    G.edges[i].startNode.label + "," + G.edges[i].endNode.label +
                    ")", new String[] {Integer.toString(i)});

            if(FindSet(G.edges[i].startNode) != FindSet(G.edges[i].endNode)) {
                sr.addStep("_AddMSTEdge", "Adding edge (" +
                        G.edges[i].startNode.label + "," + G.edges[i].endNode.label +
                        ") to MST", new String[] {Integer.toString(i)});
                
                // unite sets - steps for union added inside function
                Union(G.edges[i].startNode, G.edges[i].endNode);
            } else {
                sr.addStep("_SkipEdge", "Nodes " +
                        G.edges[i].startNode.label + " and " + G.edges[i].endNode.label +
                        " belong to the same set.\n\nCannot unite.\n\n" +
                        "Skipping edge.", new String[] {});
            }
        }


        sr.addStep("_Done", "All edges explored.\n\n" +
                "Minimum Weight Spanning Tree complete. \n\n" +
                "Press QUIT to go back", new String[] {});
    }
}
