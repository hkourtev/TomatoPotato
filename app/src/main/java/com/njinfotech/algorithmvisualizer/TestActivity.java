package com.njinfotech.algorithmvisualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;
    public TextView commandWindow;
    Boolean treeMode = true;

    DBHandler db ;
    int numConsecutiveErrors = 0, numTotalErrors = 0;
    int numErrorsTillSkip = 3;
    int sessionID;

    MediaPlayer goodSound = null, badSound = null;
    Action currentAction = null;

    private boolean inRange(int A, int B, int range){
        return A + range >= B && A - range <= B;
    }

    // First you select a node it flips a boolean value inside of it.
    // Also the node gets highlighted. Then you go to the menu select some operation. That operation depending on how many nodes
    // it needs, will iterate over all the nodes and select the first K(the number of nodes the operation needs) that have
    // the select boolean value set to true.
    public boolean onTouchEvent(MotionEvent e)
    {
        int xpos=(int) e.getX();
        int ypos=(int) e.getY();
        Point nodePos;
        int nodeRadius;

        if(e.getAction() == MotionEvent.ACTION_UP){
            for(Node n: kruskal.G.nodes){
                if (treeMode) {
                    nodePos = n.positionInTree;
                    nodeRadius = (int)n.radiusInTree;
                } else {
                    nodePos = n.position;
                    nodeRadius = (int)n.radius;
                }

                if(inRange(nodePos.x, xpos, nodeRadius) && inRange(nodePos.y, ypos, nodeRadius)){
                    if (currentAction != null) {
                        commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
                        if (currentAction.numRcvdParam < currentAction.numExpectParam) {
                            currentAction.addParam(n.label);

                            if (currentAction.numRcvdParam < currentAction.numExpectParam) {
                                // we still need to select more parameters
                                commandWindow.setText("You have successfully selected node (" + n.label + "). \n\n" +
                                        "Please select " + (currentAction.numExpectParam - currentAction.numRcvdParam) +
                                        " more node(s) or press 'Cancel'");
                            } else {
                                // if we now have all paramters - verify the step
                                if (currentAction.command.equals("_Union")) {
                                    // reset button text - since now it says Cancel Union
                                    Button thisBtn = (Button)findViewById(R.id.btnTestUnion);
                                    thisBtn.setText(getResources().getString(R.string.btnTestUnion));
                                }

                                checkStep(false);
                            }
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

        // get command window - where messages are displayed
        commandWindow = (TextView)findViewById(R.id.testActTextViewId);

        // start out in tree mode
        treeMode = true;

        // set correct text for switch view button
        setSwitchViewButtonText();

        // upon loading draw a test graph to see if everything is working
        myGraph = new Graph(this, R.id.testActCanvasId);

        // generate graph and draw graph and wait till user proceeds
        myGraph.generate(false);
        myGraph.draw();

        // disable all buttons except start until we begin
        setButtonStates(true, false, false, false);

        // init db
        sessionID = 1;
        db = new DBHandler(this);
    }

    // ----------------------------------- BUTTON FUNCTIONS ----------------------------------------
    public void quitActivity(View view) {
        this.finish();
    }

    public void startTest(View view) {
        // execute algorithm
        kruskal = new AlgoKruskal(myGraph);
        kruskal.MST();

        // reinitialize algorithm with fresh graph and steps & wait for input
        kruskal = new AlgoKruskal(myGraph, kruskal.steps);

        // execute initial steps - make sets
        executeCurrentStep(true);

        // let the user know we skipped some steps
        String notice = "NOTE: All MakeSet operations and the SortEdges operation have already "
                + "been executed automatically, as they are trivial.\n\nPlease begin by getting an " +
                "edge from the list.";
        commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
        commandWindow.setText(notice);
        Toast.makeText(getApplicationContext(), notice, Toast.LENGTH_LONG).show();

        // disable start button and enable action buttons, except for CHECK STEP
        // we enable that only after an action
        setButtonStates(false, true, true, true);

        // initialize session id
        sessionID = db.getSessionID();
    }

    public void stepUnion(View view) {
        if (currentAction == null) {
            currentAction = new Action("_Union", 2);
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText("Action Union(_,_) selected. \n\n" +
                    "Select the roots of the 2 sets that should be united.");

            Button thisBtn = (Button)view;
            thisBtn.setText(getResources().getString(R.string.btnTestCancel));
            setButtonStates(false, false, true, false);
        } else {
            // cancel action
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText("Action Cancelled. Select new action and try again.");
            currentAction = null;

            Button thisBtn = (Button)view;
            thisBtn.setText(getResources().getString(R.string.btnTestUnion));
            setButtonStates(false, true, true, true);
        }
    }

    public void stepSelectEdge(View view) {
        // check if this is the last skip edge before done
        if ((kruskal.steps.size()-2 == currStep) &&
                kruskal.steps.get(currStep).command.equals("_SkipEdge")) {
            currentAction = new Action("_SkipEdge", 0);
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText("Action SkipEdge selected.\n\nPress 'Check Step' or 'Cancel'");

            checkStep(false);
        } else {
            // check if we need to skip current edge or we are done with this and need to get next
            if (kruskal.steps.get(currStep).command.equals("_SkipEdge") &&
                    kruskal.steps.get(currStep + 1).command.equals("_SelectEdge")) {
                currentAction = new Action("_SkipEdge", 0);
                commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
                commandWindow.setText("Action SkipEdge selected.\n\nPress 'Check Step' or 'Cancel'");

                executeCurrentStep(false);
            }

            // select next edge, user doesn't need to select anything
            currentAction = new Action("_SelectEdge", 1);
            currentAction.addParam(Integer.toString(kruskal.currEdgeInd + 1));
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText("Edge (" + kruskal.G.edges[kruskal.currEdgeInd + 1].startNode.label +
                    "," + kruskal.G.edges[kruskal.currEdgeInd + 1].endNode.label +
                    ") successfully selected. \n\nPress 'Check Step' or 'Cancel'");

            checkStep(false);
        }
    }

    public void switchView(View view) {
        // toggle mode
        treeMode = !treeMode;

        // draw
        draw();
        setSwitchViewButtonText();
    }

    public void setSwitchViewButtonText() {
        Button  btn = (Button)findViewById(R.id.btnTestSwitchView);

        if (treeMode) {
            btn.setText(getResources().getString(R.string.btnTestSwitchViewTree));
        } else {
            btn.setText(getResources().getString(R.string.btnTestSwitchViewGraph));
        }

    }

    public void setButtonStates(Boolean start, Boolean selEdge, Boolean union, Boolean switchView) {
        // enable/disable the right buttons
        Button btnStart = (Button)findViewById(R.id.btnTestStart);
        Button btnSelEdge = (Button)findViewById(R.id.btnTestSelectEdge);
        Button btnUnion = (Button)findViewById(R.id.btnTestUnion);
        Button btnSwitchView = (Button)findViewById(R.id.btnTestSwitchView);

        btnStart.setEnabled(start);
        btnSelEdge.setEnabled(selEdge);
        btnUnion.setEnabled(union);
        btnSwitchView.setEnabled(switchView);
    }

    // --------------------------------- DRAWING RELATED FUNCTIONS --------------------------------
    public void drawMST() {
        Edge[] mstEdges = new Edge[kruskal.MSTEdges.size()];
        for (int j=0; j<kruskal.MSTEdges.size(); j++) {
            mstEdges[j] = kruskal.G.edges[kruskal.MSTEdges.get(j)];
        }
        kruskal.G.draw(mstEdges);
    }

    public void draw() {
        if (treeMode) {
            // draw forest of trees
            kruskal.G.drawForest();
        } else {
            if (currStep == 0) {
                // initial - only draw graph
                kruskal.G.emptyGraphDraw();
            } else if (currStep == kruskal.steps.size()) {
                // we are done - draw MST as an actual tree
                drawMST();
            } else {
                // draw mst with original graph positions
                drawMST();
            }
        }

        kruskal.G.drawEdgeList(kruskal.currEdgeInd, treeMode);
    }

    // ------------------------------- MISCELANEOUS OTHER FUNCTIONS --------------------------------
    public void playSound(Boolean good) {
        // initialize sounds
        if (goodSound == null || badSound == null) {
            goodSound = MediaPlayer.create(this, R.raw.success);
            badSound = MediaPlayer.create(this, R.raw.error);
        }

        // play the right sound
        if (good) goodSound.start();
        else badSound.start();
    }

    public void error() {
        // store new mistake
        Mistake tmpError = new Mistake(0, sessionID, currentAction.command,
                kruskal.steps.get(currStep).command);

        // play error sound
        playSound(false);

        String errorMsg = "";
        if (currentAction.command.equals(kruskal.steps.get(currStep).command)) {
            // correct step selected but wrong input provided
            errorMsg = "Error: Correct action selected but wrong parameters provided. ";
        } else {
            // wrong step selected
            errorMsg = "Error: Wrong action selected. ";
        }

        // store errors
        numConsecutiveErrors++;
        numTotalErrors++;
        if (numConsecutiveErrors == numErrorsTillSkip) {
            errorMsg = errorMsg + "\n\nYou have made " + numConsecutiveErrors +
                    " unsuccessful attempts. \n\nAutomatically advancing to next step.";

            // execute current step
            checkStep(true);
        } else {
            errorMsg = errorMsg + " Try again.\n\n" +
                    "# of errors until step is skipped: " + (numErrorsTillSkip - numConsecutiveErrors);
        }

        // display warning message and cancel current action
        commandWindow.setTextColor(getResources().getColor(R.color.errorMessage));
        commandWindow.setText(errorMsg);

        // show popup message as well
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

        // store error
        db.addError(tmpError);
    }

    // -------------------------------ALGORITHM/STEP FUNCTIONS--------------------------------------
    public void checkStep(Boolean skipStep) {
        // if skip step = true -- just execute current step
        // used in cases when the user is stuck and made several mistakes after another

        if (skipStep) {
            executeCurrentStep(false);

            if (currStep == kruskal.steps.size() - 1) {
                // execute the last step (_DONE) automatically & disable all buttons
                executeCurrentStep(false);

                // set up buttons
                setButtonStates(false, false, false, true);
            } else {
                // set up buttons
                setButtonStates(false, true, true, true);
            }
        } else if (currentAction != null) {
            if (kruskal.steps.get(currStep).compare(currentAction.command,
                    currentAction.parameters, kruskal.steps.get(currStep).orderMatters)) {
                // play success sound
                playSound(true);

                // execute step
                executeCurrentStep(false);
            } else {
                // error
                error();
            }

            // clear action - ready for next
            currentAction = null;

            if (currStep == kruskal.steps.size() - 1) {
                // execute the last step (_DONE) automatically & disable all buttons
                executeCurrentStep(false);

                // set up buttons
                setButtonStates(false, false, false, true);
            } else {
                // set up buttons
                setButtonStates(false, true, true, true);
            }
        }
    }

    public void executeCurrentStep(boolean supressMessage) {
        Boolean done = false;
        Boolean supressDraw = false;
        String msg = "";
        int numStepsExecuted = 0;

        while (!done) {
            // if this is a step that can be skipped
            if (kruskal.steps.get(currStep).pause == false) {
                // if next step != pause we will keep going so don't redraw
                if (kruskal.steps.size()-1 > currStep) {
                    if (!kruskal.steps.get(currStep+1).pause) {
                        supressDraw = true;
                    }
                    else {
                        // after this step we halt so we are done and we also need to draw
                        supressDraw = false;
                        done = true;
                    }
                }

                if (!supressMessage) {
                    // store the message for display at the end
                    if (msg.isEmpty()) msg = kruskal.steps.get(currStep).description;
                    else msg = msg + "\n\n" + kruskal.steps.get(currStep).description;
                }
            } else {
                // this is a halt step

                // if next step != pause we will keep going so don't redraw
                if (kruskal.steps.size()-1 > currStep) {
                    if (!kruskal.steps.get(currStep+1).pause) {
                        supressDraw = true;
                    }
                    else {
                        // after this step we halt so we are done and we also need to draw
                        supressDraw = false;
                        done = true;
                    }
                } else {
                    // this is the last step before done
                    supressDraw = false;
                    done = true;
                }

                if ((!done || done && numStepsExecuted > 0) && !supressMessage) {
                    // store the message for display at the end
                    if (msg.isEmpty()) msg = kruskal.steps.get(currStep).description;
                    else msg = msg + "\n\n" + kruskal.steps.get(currStep).description;
                }
            }

            executeStep(currStep, supressMessage, supressDraw);
            currStep++;
            numStepsExecuted++;
        }

        // check if
        // if we have messages - display them
        if (!msg.isEmpty() && !supressMessage) {
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText(msg);
        }
    }

    public void executeStep(int p, boolean supressMessage, boolean supressDraw) {
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
            commandWindow.setTextColor(getResources().getColor(R.color.regularMessage));
            commandWindow.setText("STEP CORRECT!\n\n" + kruskal.steps.get(p).description);
        }

        // draw
        if (!supressDraw)
            draw();

        // reset error counter
        numConsecutiveErrors = 0;
    }
}
