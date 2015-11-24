package com.njinfotech.algorithmvisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

public class TestActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    int currStep = 0;

    public TextView commandWindow;


    // canvas class needed for touch detection
    public class ourCanvas extends RelativeLayout
    {
        // extra variables go here


        // constructor
        public ourCanvas(Context context)
        {
            super(context);

        }

        // not sure what this does
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);


        }

        // touch detection goes here
        public boolean onTouchEvent(MotionEvent e)
        {
            int xpos=(int) e.getX();
            int ypos=(int) e.getY();
            switch (e.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    break;

            }
            return false;
        }
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

        // display initial instrucitons in box

        // disable buttons makeset, seledge, union, check, cancel, until we begin
    }

    private void graphReset(Graph g){
        for(Node n: g.nodes){
            n.parent = null;
        }
        kruskal.MSTEdges.clear();
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

        // draw trees
        kruskal.G.draw(mstEdges);
    }
}
