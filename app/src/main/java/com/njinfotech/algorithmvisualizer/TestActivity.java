package com.njinfotech.algorithmvisualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class TestActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;
    int activeEdgeInd = -1;
    Boolean b = true;
    LinearLayout ll ;
    public TextView commandWindow;

    Action currentAction = null;

    // canvas class needed for touch detection

    private boolean inRange(int A, int B, int range){
        return A + range >= B && A - range <= B;
    }

    //This will only be used to select nodes. Nothing else.
    //Edges lets move those to a separate activity.
    //There is literally 0 space on the screen currently, there is no way we can cram anything more on the screen.

    // First you select a node it flips a boolean value inside of it.
    // Also the node gets highlighted. Then you go to the menu select some operation. That operation depending on how many nodes
    // it needs, will iterate over all the nodes and select the first K(the number of nodes the operation needs) that have
    // the select boolean value set to true.
    public boolean onTouchEvent(MotionEvent e)
    {
        int xpos=(int) e.getX();
        int ypos=(int) e.getY();

        if(e.getAction() == MotionEvent.ACTION_UP){
            for(Node n: kruskal.G.nodes){
                if(inRange(n.position.x, xpos, (int)n.radius) && inRange(n.position.y, ypos,(int)n.radius)){
                    if (currentAction != null) {
                        if (currentAction.numRcvdParam < currentAction.numExpectParam) {
                            currentAction.addParam(n.label);

                            if (currentAction.numRcvdParam < currentAction.numExpectParam) {
                                // we still need to select more parameters
                                commandWindow.setText("You have successfully selected node (" + n.label + "). \n\n" +
                                        "Please select " + (currentAction.numExpectParam - currentAction.numRcvdParam) +
                                        " more node(s) or press 'Cancel'");
                            } else {
                                // if we now have all paramters - enable check step button
                                commandWindow.setText("You have successfully selected node (" + n.label + "). \n\n" +
                                        "No more parameters required for this action. \n\n" +
                                        "Press 'Check Step' or 'Cancel'");

                                setButtonStates(false, false, false, false, false, true, true);
                            }
                        } else {
                            // msg that you cannot select any more nodes
                            commandWindow.setText("Warning: You have selected the maximum number of nodes for this action. \n\n" +
                                    "Press 'Check Step' or 'Cancel'");

                            setButtonStates(false, false, false, false, false, true, true);
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        commandWindow = (TextView)findViewById(R.id.testActTextViewId);

        // upon loading draw a test graph to see if everything is working
        myGraph = new Graph(this, R.id.testActCanvasId);

        // generate graph and draw graph and wait till user proceeds
        myGraph.generate(false);
        myGraph.draw();

        ll = (LinearLayout)this.findViewById(R.id.linearLayout);
        ll.setVisibility(LinearLayout.GONE);

        // display initial instrucitons in box

        // disable buttons makeset, seledge, union, check, cancel, until we begin
        setButtonStates(true, false, false, false, false, false, false);
    }

    private void openMenu(){
        ll.setVisibility(LinearLayout.GONE);
        b = false;
    }

    private void closeMenu(){
        ll.setVisibility(LinearLayout.VISIBLE);
        b = true;
    }
    public void showMenu(View view){

            if (b) {
                openMenu();
            } else {
                closeMenu();
            }
    }

    public void quitActivity(View view) {
        this.finish();
    }

    public void startTest(View view) {
        // execute algorithm
        kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algorithm with fresh graph and steps & wait for input
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);

        myGraph.emptyGraphDraw();
        closeMenu();

        // init active edge index locally because the one stored in the graph get reset
        activeEdgeInd = kruskal.G.activeEdgeInd;

        // disable start button and enable action buttons, except for CHECK STEP
        // we enable that only after an action
        setButtonStates(false, true, true, true, true, false, false);
    }

    public void stepCheck(View view) {
        //
        if (currentAction != null) {
            if (currentAction.numRcvdParam == currentAction.numExpectParam) {
                // compare action to current step
                if (currentAction.command.equals("_Union") && kruskal.steps.get(currStep+1).command.equals("_Union")) {
                    // special case for union to avoid having a button for _AddMST command
                    if (kruskal.steps.get(currStep+1).compare(currentAction.command, currentAction.parameters)) {
                        // execute add mstedge step
                        executeStep(currStep, false);
                        currStep++;

                        // execute the union step
                        executeStep(currStep, false);
                        currStep++;

                        // if next step is increase rank execute that automatically as well
                        if (kruskal.steps.get(currStep).command.equals("_IncreaseRank")) {
                            executeStep(currStep, false);
                            currStep++;
                        }
                    } else {
                        // display warning message and cancel current action
                        commandWindow.setText("Error: Wrong parameters provided. Try again.");
                    }
                } else {
                    if (kruskal.steps.get(currStep).compare(currentAction.command, currentAction.parameters)) {
                        // execute step
                        executeStep(currStep, false);
                        currStep++;
                    } else {
                        // display warning message and cancel current action
                        commandWindow.setText("Error: Wrong parameters provided. Try again.");
                    }
                }

                // clear action - ready for next
                currentAction = null;

                if (currStep == kruskal.steps.size() - 1) {
                    // execute the last step (_DONE) automatically & disable all buttons
                    executeStep(currStep, false);
                    currStep++;

                    // set up buttons
                    setButtonStates(false, false, false, false, false, false, false);
                } else {
                    // set up buttons
                    setButtonStates(false, true, true, true, true, false, false);
                }
            }
        }
    }

    public void stepUnion(View view) {
        currentAction = new Action("_Union", 2);
        commandWindow.setText("Action Union(_,_) selected. \n\n" +
                "Select the roots of the 2 sets that should be united.");

        setButtonStates(false, false, false, false, false, false, true);
    }

    public void stepMakeSet(View view) {
        // display instructions in text box - select node to make set
        // we can say those need to be selected in lexicographic order.
        // then wait for action
        currentAction = new Action("_MakeSet", 1);
        commandWindow.setText("Action MakeSet(_) selected. \n\nSelect a node to make set with. " +
                "Please note that nodes should be provided in ascending order.");

        setButtonStates(false, false, false, false, false, false, true);
    }

    public void stepSortEdges(View view) {
        // select next edge, user doesn't need to select anything
        currentAction = new Action("_SortEdges", 0);

        commandWindow.setText("Press 'Check Step' or 'Cancel'");

        setButtonStates(false, false, false, false, false, true, true);
    }

    public void stepSelectEdge(View view) {
        // select next edge, user doesn't need to select anything
        currentAction = new Action("_SelectEdge", 1);
        currentAction.addParam(Integer.toString(activeEdgeInd+1));

        commandWindow.setText("Edge (" + kruskal.G.edges[kruskal.G.activeEdgeInd + 1].startNode.label +
                "," + kruskal.G.edges[kruskal.G.activeEdgeInd + 1].endNode.label +
                ") successfully selected. \n\nPress 'Check Step' or 'Cancel'");

        setButtonStates(false, false, false, false, false, true, true);
    }

    public void stepCancel(View view) {
        // cancel action
        commandWindow.setText("Action Cancelled. Select new action and try again.");
        currentAction = null;

        setButtonStates(false, true, true, true, true, true, false);
    }

    public void setButtonStates(Boolean start, Boolean makeSet, Boolean sortEdges, Boolean selEdge,
                                Boolean union, Boolean checkStep, Boolean cancel) {
        // enable/disable the right buttons
        Button btnStart = (Button)findViewById(R.id.btnTestStart);
        Button btnMakeSet = (Button)findViewById(R.id.btnTestMakeSet);
        Button btnSortEdges = (Button)findViewById(R.id.btnTestSortEdges);
        Button btnSelEdge = (Button)findViewById(R.id.btnTestSelectEdge);
        Button btnUnion = (Button)findViewById(R.id.btnTestUnion);
        Button btnCheckStep = (Button)findViewById(R.id.btnTestCheck);
        Button btnCancel = (Button)findViewById(R.id.btnTestCancel);

        btnStart.setEnabled(start);
        btnMakeSet.setEnabled(makeSet);
        btnSortEdges.setEnabled(sortEdges);
        btnSelEdge.setEnabled(selEdge);
        btnUnion.setEnabled(union);
        btnCheckStep.setEnabled(checkStep);
        btnCancel.setEnabled(cancel);
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

        // draw tree
        kruskal.G.draw(mstEdges);
        kruskal.G.drawEdgeList(kruskal.currEdgeInd);
    }
}
