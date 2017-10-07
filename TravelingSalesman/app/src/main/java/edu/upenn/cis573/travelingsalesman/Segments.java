package edu.upenn.cis573.travelingsalesman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by webb on 9/23/17.
 */

public class Segments {
    private ArrayList<LineSegment> paths = new ArrayList<>();

    public Segments(){

    }

    public boolean contains(LineSegment path){
//        return existed.contains(LineSegment);
        for(LineSegment ls : paths){
            if(ls.equals(path)){
                return true;
            }
        }
        return false;
    }

    public boolean addPath(LineSegment path){
        if(!contains(path) && path.getStart() != path.getEnd()){
            paths.add(path);
            return true;
        }
        return false;
    }

    public void panit(Canvas canvas){
            Paint paint = new Paint();
            for (int i = 0; i < paths.size(); i++) {
                LineSegment start = paths.get(i);
                LineSegment end = paths.get(i);
                paint.setColor(Color.RED);
                paint.setStrokeWidth(10);
                canvas.drawLine(start.getStart().x, start.getStart().y, end.getEnd().x, end.getEnd().y, paint);
        }
    }

    public void remove(){
        paths.remove(paths.size()-1);
    }

    public ArrayList<LineSegment> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<LineSegment> paths) {
        this.paths = paths;
    }

    public int getSize() {
        return paths.size();
    }

    public void clearPath(){
        paths.clear();
    }

    public boolean isOneCircuit(int spinnerNum){
        if(paths.size() == spinnerNum){
            HashSet<Point> set = new HashSet<>();

            for(LineSegment line : paths){
                Point start = line.getStart();
                Point end = line.getEnd();

                if(set.contains(start) && set.contains(end)){
                    if(set.size() != spinnerNum){
                        return false;
                    }
                    else {
                        HashMap<Point, Integer> connections = new HashMap<Point, Integer>();
                        for (LineSegment path : paths) {
                            Point p1 = path.getStart();
                            Point p2 = path.getEnd();
                            Integer value = connections.get(p1);
                            if (value == null)
                                value = 0;
                            value++;
                            connections.put(p1, value);

                            value = connections.get(p2);
                            if (value == null)
                                value = 0;
                            value++;
                            connections.put(p2, value);
                        }

                        for (int v : connections.values()) {
                            if (v != 2) {
                                return false;
                            }
                        }
                    }
                }
                else {
                    set.add(start);
                    set.add(end);
                }

            }
            return true;
        }

        return false;
    }
}
