package com.njinfotech.algorithmvisualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;


public class LearnActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // upon loading draw a test graph to see if everything is working
        myGraph = new Graph(this, R.id.learnActCanvasId);

        // ------------------------ KRUSKAL's -----------------------------------------------------
        // generate graph from adjacency list
        // {u,v,weight}
        /*int tmpAdjList[][] = {{0,1,4}, {0,7,8}, {1,2,8}, {1,7,11},
                {2,3,7}, {2,5,4}, {2,8,2}, {3,4,9}, {3,5,14}, {4,5,10},
                {5,6,2}, {6,7,1}, {6,8,6}, {7,8,7}};

        // generate graph for kruskal's algorithm
        myGraph.generate(9, tmpAdjList, 100, new int[] {150, 150, 450, 150}, false);
        */
        myGraph.generate(3, 4, 70, new int[]{150, 150, 450, 150}, 100, false);

        // draw graph and wait till user proceeds
        myGraph.draw();

        // execute algorithm
        kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algorithm with fresh graph and steps
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);
    }

    public void nextStep(View view) {
        if (currStep < kruskal.steps.size()) {
            // run currStep
            executeStep(currStep);

            currStep++;
        }
    }

    public void executeStep(int p) {
        // execute step using reflection
        try {
            Method method = kruskal.getClass().getMethod(kruskal.steps.get(p).command, kruskal.steps.get(p).parameters);

            switch (kruskal.steps.get(p).arguments.length) {
                case 0:
                    try {
                        method.invoke(kruskal);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {
                        method.invoke(kruskal, kruskal.steps.get(p).arguments[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        method.invoke(kruskal, kruskal.steps.get(p).arguments[0], kruskal.steps.get(p).arguments[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // display message
        Log.d("LearnActivity", kruskal.steps.get(p).description);

        Edge[] mstEdges = new Edge[kruskal.MSTEdges.size()];
        for (int j=0; j<kruskal.MSTEdges.size(); j++) {
            mstEdges[j] = kruskal.G.edges[j];
        }

        // draw trees
        kruskal.G.draw(mstEdges);
    }
}
