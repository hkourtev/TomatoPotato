package com.njinfotech.algorithmvisualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Method;


public class LearnActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;

    public TextView commandWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        commandWindow = (TextView)findViewById(R.id.learnActTextViewId);

        // upon loading draw a test graph to see if everything is working
        myGraph = new Graph(this, R.id.learnActCanvasId);

        // generate graph and draw graph and wait till user proceeds
        myGraph.generate(false);
        myGraph.draw();

        // execute algorithm
        kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algorithm with fresh graph and steps & wait for input
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);

        // draw edge list
        kruskal.G.drawEdgeList(kruskal.currEdgeInd);
    }

    public void nextStep(View view) {
        if (currStep < kruskal.steps.size()) {
            // run currStep
            executeStep(currStep, false);

            currStep++;
        }
    }

    public void quitActivity(View view) {
        this.finish();
    }

    private void graphReset(Graph g){
        for(Node n: g.nodes){
            n.parent = null;
        }
        kruskal.MSTEdges.clear();
    }

    public void previousStep(View view) {
        if(currStep > 1){
        graphReset(myGraph);
        currStep--;
        for(int i = 0; i < currStep - 1; i++)
            executeStep(i, true);

        executeStep(currStep - 1, false);}
    }

    public void executeStep(int p, boolean supressMessage) {
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
        if(!supressMessage) {
            commandWindow.setText(kruskal.steps.get(p).description);
        }

        Edge[] mstEdges = new Edge[kruskal.MSTEdges.size()];
        for (int j=0; j<kruskal.MSTEdges.size(); j++) {
            mstEdges[j] = kruskal.G.edges[kruskal.MSTEdges.get(j)];
        }

        // draw graph and edge list
        kruskal.G.draw(mstEdges);
        kruskal.G.drawEdgeList(kruskal.currEdgeInd);
    }
}
