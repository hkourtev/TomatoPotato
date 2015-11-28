package com.njinfotech.algorithmvisualizer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RelativeLayout;

import java.util.Iterator;

class DisplayManager{
  Canvas canvas;
  int screenWidth, screenHeight;
  int margins[] = new int[4];
  int radius;
  int startx, starty, endx, endy;

  public DisplayManager(int screenWidth, int screenHeight, int margins[], Canvas canvas){
    this.canvas = canvas;
    this.screenHeight = screenHeight;
    this.screenWidth = screenWidth;
    this.startx = margins[0];
    this.endx = screenWidth - margins[2];
    this.starty = margins[1];
    this.endy = screenHeight - margins[3];
  }

  public void drawNode(String label, float x, float y){
    Paint node = new Paint();
    //Todo: Use the color specified in the resource file instead of this color.
    node.setColor(0xFF0000F9);
    canvas.drawCircle(x, y, radius, node);
    canvas.drawText(label, x, y, new Paint());
  }

  public void drawEdge(Point start, Point end){
    Point edgeDirVector = new Point();
    edgeDirVector.x = end.x - start.x;
    edgeDirVector.y = end.y - start.y;

    // get position vector lenght, to be used later to calculate unit vector
    double edgeDirLen = Math.sqrt(Math.pow(edgeDirVector.x,2) + Math.pow(edgeDirVector.y,2));

    // set start point to start node center + radius (so that we start drawing from border not center
    Point startLocation = new Point(), endLocation = new Point();
    startLocation.x = start.x + (int)(((double)edgeDirVector.x/edgeDirLen)*radius);
    startLocation.y = start.y + (int)(((double)edgeDirVector.y/edgeDirLen)*radius);

    // set end location = start location + position vector - radius
    endLocation.x = start.x + edgeDirVector.x - (int)(((double)edgeDirVector.x/edgeDirLen)*radius);
    endLocation.y = start.y + edgeDirVector.y - (int)(((double)edgeDirVector.y/edgeDirLen)*radius);
    canvas.drawLine(startLocation.x, startLocation.y, endLocation.x, endLocation.y, new Paint());
  }

  public void clearCanvas(){
    canvas.drawColor(0xFFFFFFFF);
  }
  public void drawSet(SetPool sp){
    clearCanvas();
    radius = 40;
    sp.sortPool();
    Iterator I = sp.iterator();
    int left = startx;
    while(I.hasNext()){
      SetPool.set current = (SetPool.set)I.next();
      int noChildren = current.getWeight();
      float x = (float)  (2 * radius * noChildren);

      drawNode(current.getLabel(), x + left, starty + (float) 50.0);
      Point centerOfCurrent = new Point((int)x + left, starty + 50);
      Iterator J = current.iterator();
      while(J.hasNext()){
        String S = (String)J.next();
        drawNode(S, left + radius, starty + (float) 300.0);
        Point end = new Point((int) left + radius, starty + 300);
        drawEdge(centerOfCurrent, end);
        left += 4 * radius;
      }
        // Gap between two sets.
        left += 100;
    }
  }

}