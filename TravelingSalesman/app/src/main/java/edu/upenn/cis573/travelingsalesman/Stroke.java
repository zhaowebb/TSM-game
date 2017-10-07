package edu.upenn.cis573.travelingsalesman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by webb on 9/23/17.
 */

public class Stroke {
    boolean valid;
    int color = Color.YELLOW;
    int width = 10;
    ArrayList<Point> path = new ArrayList<>();

    public Stroke(){

    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Point> getPath() {
        return path;
    }

    public void setPath(ArrayList<Point> path) {
        this.path = path;
    }

    public void addPoints(Point p){
        path.add(p);
    }

    public void clearPath(){
        path.clear();
    }

    public void paint(Canvas canvas){
        Paint paint = new Paint();
        if (path.size() > 1) {
                for (int i = 0; i < path.size()-1; i++) {
                    int x1 = path.get(i).x;
                    int y1 = path.get(i).y;
                    int x2 = path.get(i+1).x;
                    int y2 = path.get(i+1).y;

                    paint.setColor(color);
                    paint.setStrokeWidth(width);
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
            }
    }
}
