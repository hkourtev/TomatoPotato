package com.njinfotech.algorithmvisualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // upon loading draw a test graph to see if everything is working
        Graph myGraph = new Graph(this, R.id.learnActCanvasId);
        //myGraph.generateTest();

        // generate graph from adjacency list
        // {u,v,weight}
        int tmpAdjList[][] = {{0,1,4}, {0,7,8}, {1,2,8}, {1,7,11},
                {2,3,7}, {2,5,4}, {2,8,2}, {3,4,9}, {3,5,14}, {4,5,10},
                {5,6,2}, {6,7,1}, {6,8,6}, {7,8,7}};

        // generate graph for kruskal's algorithm
        myGraph.generate(9, tmpAdjList, 100, new int[] {150, 150, 450, 150}, false);
        AlgoKruskal kruskal = new AlgoKruskal();
        Edge[] mstEdges = new Edge[tmpAdjList.length];
        kruskal.MST(mstEdges, myGraph);

        // generate undirected graph, of 12 nodes arranged in 3 rows and 4 col with 58 directed
        //myGraph.generate(3, 4, 100, new int[] {150, 150, 450, 150}, 100, false);
        myGraph.draw();
    }

}
