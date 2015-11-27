package com.njinfotech.algorithmvisualizer;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Iterator;

/**
 * Created by aditya on 11/27/2015.
 */
public class DisplayManager {
    private int startx, starty, endx, endy, height, width;
    private float radius;
    private Canvas canvas;

    public DisplayManager(int height, int width, int [] margins, Canvas canvas){
        this.canvas = canvas;
        this.height = height;
        this.width = width;
        this.startx = margins[0];
        this.endx = width - margins[2];
        this.starty = margins[1];
        this.endy = height - margins[3];
    }

    public void drawNode(String label, float x, float y){
        Paint node = new Paint();
        canvas.drawCircle(x, y, radius, node);
        canvas.drawText(label, x, y, new Paint());
    }

    public void drawSets(SetPool sp){
        sp.sortPool();
        int childCount = sp.getTotalChildrenCount();
        radius = (endx - startx)/(2 * childCount);
        radius /= 2;
        Iterator I = sp.iterator();
        int left = startx;
        while(I.hasNext()){
            SetPool.set current = (SetPool.set)I.next();
            int noChildren = current.getWeight();
            float x = (float)0.5 * (left + (2 * noChildren));

            drawNode(current.getLabel(), x, starty + (float)50.0);
            Iterator J = current.iterator();
            while(J.hasNext()){
                String S = (String)J.next();
                drawNode(S, left + radius, starty + (float) 100.0);
                canvas.drawLine(x, starty + (float)50 + radius, left + radius, starty + (float)100 - radius, new Paint());
                left += 4 * radius;
            }

        }
    }

    public void drawTree(treePool tp){
        tp.sortPool();
        float radius1 = height/(2 * tp.getMaxHeight()), radius2 = width/(2 * tp.getMaxWidth());
        radius = radius1 > radius2 ? radius2/2 : radius1/2;
        Iterator I = tp.iterator();

        while(I.hasNext()){
            treePool.Tree current= ((treePool.Tree)I.next());
            int left = startx, top = starty, i = 0, parentX, parentY;
            int currentMaxWidth = current.getMaxWidth();
            int currentWidth = currentMaxWidth * 2;
            String lot = current.levelOrderTraversal();
            String []levels = lot.split("\\*");
            for(String level: levels){
                int noNodes = level.length();
                float gap = (currentWidth - (noNodes * radius * 2))/(noNodes);
                for(char c : level.toCharArray()){
                    drawNode(c + " ", left + radius, top + radius);
                    
                }
            }
        }

    }


}
