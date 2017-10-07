package edu.upenn.cis573.travelingsalesman;

import android.graphics.Point;

/**
 * Created by webb on 9/23/17.
 */

public class LineSegment {
    private Point start;
    private Point end;

    public Point getStart() {
        return start;
    }

    public boolean equals(LineSegment path){
        if(start.equals(path.getStart()) && end.equals(path.getEnd()) || end.equals(path.getStart()) && start.equals(path.getEnd())){
            return true;
        }
        return false;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public LineSegment(Point start, Point end){
        this.start = start;
        this.end = end;

    }

    public  double distance(){
        double dx = start.x - end.x;
        double dy = start.y - end.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist;
    }

}
