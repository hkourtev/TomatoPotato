package com.njinfotech.algorithmvisualizer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Graph {
    private Activity ptr;

    public Graph(Activity ptr) {
        this.ptr = ptr;
    }

    public void generate() {
        // get current screen size
        Display display = ptr.getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        // create a paintable bitmap
        Bitmap canvasBg = Bitmap.createBitmap(screenSize.x, screenSize.y, Bitmap.Config.ARGB_8888);

        // pick color to draw with
        Paint paintGreen = new Paint();
        paintGreen.setColor(ptr.getResources().getColor(R.color.colorGreen));
        Paint paintRed = new Paint();
        paintRed.setColor(ptr.getResources().getColor(R.color.colorRed));
        Paint paintEdge = new Paint();
        paintEdge.setColor(ptr.getResources().getColor(R.color.colorPrimaryDark));
        paintEdge.setStrokeWidth(5);

        // create canvas
        Canvas canvas = new Canvas(canvasBg);
        canvas.drawCircle(150, 150, 100, paintGreen);
        canvas.drawCircle(500, 800, 100, paintRed);
        drawEdge(canvas, 150, 150, 100, 500, 800, 100, paintEdge);

        RelativeLayout drawSpace = (RelativeLayout) ptr.findViewById(R.id.learnActCanvasId);
        drawSpace.setBackgroundDrawable(new BitmapDrawable(canvasBg));
    }

    public void drawEdge(Canvas canvas, float c1x, float c1y, float c1r, float c2x, float c2y, float c2r, Paint edgeColor) {
        Point start = new Point();
        Point end = new Point();

        canvas.drawLine(c1x, c1y, c2x, c2y, edgeColor);
    }
}
