package com.njinfotech.algorithmvisualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Method;


public class LearnActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;
    Boolean treeMode;

    public TextView commandWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        // get command window - where messages are displayed
        commandWindow = (TextView)findViewById(R.id.learnActTextViewId);

        // start out in tree mode
        treeMode = true;

        // set correct text for switch view button
        setSwitchViewButtonText();

        // upon loading draw a test graph to see if everything is working
        myGraph = new Graph(this, R.id.learnActCanvasId);

        // generate graph
        myGraph.generate(false);

        // execute algorithm
        kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algorithm with fresh graph and steps & wait for input
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);

        // draw graph and edge list and wait till user proceed
        draw();

        // disable previous button and switch button
        setButtonStates(false, true, false);
    }

    // -------------------------------- BUTTON CALLBACK FUNCTIONS --------------------------------
    public void switchView(View view) {
        // toggle mode
        treeMode = !treeMode;

        // execute last step once again to display everything
        //executeStep(currStep-1, false);

        // draw
        draw();
        setSwitchViewButtonText();
    }

    public void quitActivity(View view) {
        this.finish();
    }

    public void nextStep(View view) {
        if (currStep < kruskal.steps.size()) {
            // run currStep
            executeStep(currStep, false);
            currStep++;

            // draw
            draw();
            setButtonStates();
        }
    }

    public void previousStep(View view) {
        if(currStep > 0){
            graphReset(myGraph);
            currStep--;
            for(int i = 0; i < currStep - 1; i++)
                executeStep(i, true);

            if (currStep-1 >= 0) {
                executeStep(currStep - 1, false);

                // draw
                draw();
                setButtonStates();
            } else {
                // we are going back to the initial graph - very beginning
                // draw
                draw();
                setButtonStates(false, true, false);
            }
        }
    }

    public void setSwitchViewButtonText() {
        Button  btn = (Button)findViewById(R.id.btnLearnSwitchView);

        if (treeMode) {
            btn.setText(getResources().getString(R.string.btnLearnSwitchViewTree));
        } else {
            btn.setText(getResources().getString(R.string.btnLearnSwitchViewGraph));
        }

    }

    public void setButtonStates(Boolean prev, Boolean next, Boolean switchView) {
        // enable/disable the right buttons
        Button btnPrev = (Button)findViewById(R.id.btnLearnPrevStep);
        Button btnNext = (Button)findViewById(R.id.btnLearnNextStep);
        Button btnSwitch = (Button)findViewById(R.id.btnLearnSwitchView);

        btnPrev.setEnabled(prev);
        btnNext.setEnabled(next);
        btnSwitch.setEnabled(switchView);
    }

    public void setButtonStates() {
        if (currStep == 0)
            setButtonStates(false, true, false);
        else if (currStep == kruskal.steps.size())
            setButtonStates(true, false, true);
        else
            setButtonStates(true, true, true);
    }

    // --------------------------------- DRAWING RELATED FUNCTIONS --------------------------------
    private void graphReset(Graph g){
        for(Node n: g.nodes){
            n.parent = null;
        }
        kruskal.MSTEdges.clear();
    }

    public void drawMST() {
        Edge[] mstEdges = new Edge[kruskal.MSTEdges.size()];
        for (int j=0; j<kruskal.MSTEdges.size(); j++) {
            mstEdges[j] = kruskal.G.edges[kruskal.MSTEdges.get(j)];
        }
        kruskal.G.draw(mstEdges);
    }

    public void draw() {
        if (currStep == 0) {
            // initial - only draw graph
            myGraph.draw();
        } else {
            if (treeMode) {
                // draw forest of trees
                kruskal.G.drawForest();
            } else {
                if (currStep == kruskal.steps.size()) {
                    // we are done - draw MST as an actual tree
                    drawMST();
                } else {
                    // draw mst with original graph positions
                    drawMST();
                }
            }
        }

        kruskal.G.drawEdgeList(kruskal.currEdgeInd, treeMode);
    }

    // ------------------------------------ ALGORITHM RELATED FUNCTIONS ----------------------------
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
    }
}
