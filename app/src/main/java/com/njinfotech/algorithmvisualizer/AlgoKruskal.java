package com.njinfotech.algorithmvisualizer;

import java.util.Random;

/**
 * Created by hkourtev on 11/21/15.
 */
public class AlgoKruskal {

    public void MakeSet(Node x)
    {
        x.parent = x;
        x.rank = 0;
    }

    public void Union(Node x, Node y)
    {
        Link(FindSet(x), FindSet(y));
    }

    public void Link(Node x, Node y)
    {
        if (x.rank > y.rank)
        {
            y.parent = x;
        }

        else
        {
            x.parent = y;

            if(x.rank == y.rank)
            {
                y.rank = y.rank + 1;
            }
        }
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


    public int MST(Edge[] MSTEdges, Graph G)
    {
        for (int i = 0; i < G.nodes.length; i++)
        {
            MakeSet(G.nodes[i]);
        }

        SortEdges(G.edges, 0, G.edges.length - 1 );

        int k=0;

        for (int i = 0; i < G.edges.length; i++)
        {
            if(FindSet(G.edges[i].startNode) != FindSet(G.edges[i].endNode))
            {
                MSTEdges[k] = G.edges[i];
                k++;
                Union(G.edges[i].startNode, G.edges[i].endNode);
            }
        }

        return k;
    }
}
