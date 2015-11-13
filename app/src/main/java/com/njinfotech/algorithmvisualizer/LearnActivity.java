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

        // generate undirected graph, of 12 nodes arranged in 3 rows and 4 col with 58 directed
        myGraph.generate(3, 4, 100, new int[] {150, 150, 450, 150}, 100, false);
        myGraph.draw();
    }

}
