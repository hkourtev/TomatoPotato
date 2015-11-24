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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class TestActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;
    Boolean b = true;
    LinearLayout ll ;
    public TextView commandWindow;


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
        //Log.d("asd", xpos + " " + ypos);
        if(e.getAction() == MotionEvent.ACTION_UP){

            for(Node n: myGraph.nodes){
                if(inRange(n.position.x, xpos,60) && inRange(n.position.y, ypos,60)){
                    Log.d("asd", n.label);
                    n.select();
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

        ll = (LinearLayout)this.findViewById(R.id.linearlayout);
        ll.setVisibility(LinearLayout.GONE);

        // display initial instrucitons in box

        // disable buttons makeset, seledge, union, check, cancel, until we begin
    }

    private void graphReset(Graph g){
        for(Node n: g.nodes){
            n.parent = null;
        }
        kruskal.MSTEdges.clear();
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


        // disable start button and enable action buttons, except for CHECK STEP
        // we enable that only after an action
    }

    public void stepCheck(View view) {
        //
    }

    public void stepUnion(View view) {
        //
    }

    public void stepMakeSet(View view) {
        // display instructions in text box - select node to make set
        // we can say those need to be selected in lexicographic order.
        // then wait for action
    }

    public void stepSelectEdge(View view) {
        //
    }

    public void stepSelectCancel(View view) {
        //
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
    }
}
