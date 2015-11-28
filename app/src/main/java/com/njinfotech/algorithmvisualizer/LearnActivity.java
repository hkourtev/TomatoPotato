package com.njinfotech.algorithmvisualizer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Method;


public class LearnActivity extends AppCompatActivity {

    Graph myGraph;
    AlgoKruskal kruskal;
    treePool tp;
    SetPool sp;
    StepRecorder sr;
    Canvas canvas;
    DisplayManager dm;
    Bitmap canvasBg;


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



        Point screenSize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(screenSize);

        // create a paintable bitmap
        canvasBg = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBg);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.learnActCanvasId);
        ll.setBackgroundDrawable(new BitmapDrawable(canvasBg));
        sp = new SetPool();
        tp = new treePool();

        sr = new StepRecorder(sp, tp, myGraph);
        dm = new DisplayManager(screenSize.x, screenSize.y, new int[]{600, 100, 100, 100}, canvas);
        // execute algorithm
        kruskal = new AlgoKruskal(myGraph, sr);
        kruskal.generateSteps();

        // disable previous button
        setButtonStates(false, true);
    }



    public void setButtonStates(Boolean prev, Boolean next) {
        // enable/disable the right buttons
        Button btnPrev = (Button)findViewById(R.id.btnLearnPrevStep);
        Button btnNext = (Button)findViewById(R.id.btnLearnNextStep);

        btnPrev.setEnabled(prev);
        btnNext.setEnabled(next);
    }

    public void quitActivity(View view) {
        this.finish();
    }

    public void nextStep(View view){
        if(sr.hasNext()) {
            sr.nextStep();
        }

        dm.drawSet(sp);
        Log.d("asd", " " + sp.getTotalSetCount());
        conclude();
    }

    private void conclude(){
        RelativeLayout drawSpace = (RelativeLayout) this.findViewById(R.id.learnActCanvasId);
        drawSpace.setBackgroundDrawable(new BitmapDrawable(canvasBg));
    }

}
