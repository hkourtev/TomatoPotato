package com.njinfotech.algorithmvisualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.lang.reflect.Method;


public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // upon loading draw a test graph to see if everything is working
        Graph myGraph = new Graph(this, R.id.learnActCanvasId);

        // ------------------------ KRUSKAL's -----------------------------------------------------
        // generate graph from adjacency list
        // {u,v,weight}
/*        int tmpAdjList[][] = {{0,1,4}, {0,7,8}, {1,2,8}, {1,7,11},
                {2,3,7}, {2,5,4}, {2,8,2}, {3,4,9}, {3,5,14}, {4,5,10},
                {5,6,2}, {6,7,1}, {6,8,6}, {7,8,7}};

        // generate graph for kruskal's algorithm
        myGraph.generate(9, tmpAdjList, 100, new int[] {150, 150, 450, 150}, false);

        // draw graph and wait till user proceeds

        // execute algorithm
        AlgoKruskal kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algrorithm with fresh graph and steps
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);

        // loop through each step, halting and waiting for input by the user where necessary and
        // displaying the correct messages as we go along
        for (int p=0; p<kruskal.steps.size(); p++) {
            // execute step using reflection
            try {
                Method method = kruskal.getClass().getMethod(kruskal.steps.get(p).command);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // display message

            // draw trees
            kruskal.G.draw();

            // if not step.pause - display step, wait a bit and continue with next
            if (kruskal.steps.get(p).pause) {

            } else {

            }
        }

        // done with steps, wait till user proceeds and draw the MST
        // update original graph edges with MST edge list


        // draw graph
        myGraph.draw();*/
        // ------------------------ /KRUSKAL's ----------------------------------------------------

        // generate undirected graph, of 12 nodes arranged in 3 rows and 4 col with 58 directed
        myGraph.generate(3, 4, 70, new int[] {150, 150, 450, 150}, 100, false);
        myGraph.draw();
    }

}
