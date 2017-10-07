package edu.upenn.cis573.travelingsalesman;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import java.util.*;


public class GameView extends View {

    protected Segments segments = new Segments();
    private Point firstPoint;
    protected Point[] mapPoints;
    protected int spinnerNum;
    protected int attempt = 0;
//    protected boolean isValidStroke = false;
    protected static final Point[] mapPositions;
    private Stroke stroke = new Stroke();
    // these points are all hardcoded to fit the UPenn campus map on a Nexus 5
    static {
        mapPositions = new Point[13];
        mapPositions[0] = new Point(475, 134);
        mapPositions[1] = new Point(141, 271);
        mapPositions[2] = new Point(272, 518);
        mapPositions[3] = new Point(509, 636);
        mapPositions[4] = new Point(1324, 402);
        mapPositions[5] = new Point(1452, 243);
        mapPositions[6] = new Point(1667, 253);
        mapPositions[7] = new Point(750,  670);
        mapPositions[8] = new Point(1020, 380);
        mapPositions[9] = new Point(870, 250);
        mapPositions[10] = new Point(540, 477);
        mapPositions[11] = new Point(828, 424);
        mapPositions[12] = new Point(1427, 66);
    }

    public GameView(Context context, int spinnerNum) {
        super(context);
        this.spinnerNum = spinnerNum;
        init();
    }

    public GameView(Context context, AttributeSet attributeSet, int spinnerNum) {
        super(context, attributeSet);
        this.spinnerNum = spinnerNum;
        init();
    }

    public static double calculatePathDistance(ArrayList<Point> points) {

        double total = 0;
        // get the distance between the intermediate points
        for (int i = 0; i < points.size()-1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);
            LineSegment line = new LineSegment(p1, p2);
            double dist = line.distance();
            total += dist;
        }

        // then need to go back to the beginning
        Point p1 = points.get(points.size()-1);
        Point p2 = points.get(0);
        LineSegment line = new LineSegment(p1, p2);
        double dist = line.distance();
        total += dist;

        return total;

    }

    /*
    initialize the game view;
    @para: spinnerNum is the number of points on the map selected by player in the MainActivity
     */
    protected void init() {
        //set spinner number and draw the background


        setBackgroundResource(R.drawable.campus);

        Log.v("GAME VIEW", "init");

        mapPoints = new Point[spinnerNum];

        // randomly select distinct spinner number of points from mapPositions,
        // to do so, a set is used to store existing points to make sure the next point is distinct
        Set set = new HashSet();
        Random rn = new Random();
        for (int i = 0; i < spinnerNum; i++) {
            int randomNum = rn.nextInt(mapPositions.length);
            while (set.contains(randomNum)) {
                randomNum = rn.nextInt(mapPositions.length);
            }
            set.add(randomNum);
            mapPoints[i] = mapPositions[randomNum];
        }
    }

    /*
     * This method is automatically invoked when the View is displayed.
     * It is also called after you call "invalidate" on this object.
     */
    protected void onDraw(Canvas canvas) {


        // draws the stroke
        if (stroke.isValid()) {
            stroke.paint(canvas);
        }

        segments.panit(canvas);

        // draws the points on the map
        Paint paint = new Paint();
        paint.setColor(Color.RED);

        for (int i = 0; i < mapPoints.length; i++) {
            int x = mapPoints[i].x;
            int y = mapPoints[i].y;
            canvas.drawRect(x, y, x+20, y+20, paint);
        }


        // see if user has solved the problem
        if ((segments.getSize() == mapPoints.length) && segments.isOneCircuit(spinnerNum)) {
            ArrayList<Point> shortestPath = ShortestPath.shortestPath(mapPoints);
            double shortestPathLength = calculatePathDistance(shortestPath);

            double myPathLength = 0;
            for (LineSegment path : segments.getPaths()) {
                Point p1 = path.getStart();
                Point p2 = path.getEnd();
                LineSegment line = new LineSegment(p1, p2);
                double dist = line.distance();

                myPathLength += dist;
            }

            Log.v("RESULT", "Shortest path length is " + shortestPathLength + "; my path is " + myPathLength);

            double diff = shortestPathLength - myPathLength;
            if (Math.abs(diff) < 0.01) {
                Toast.makeText(getContext(), "You found the shortest path!", Toast.LENGTH_LONG).show();
                attempt = 0;
                segments.clearPath();
            }
            else {
                attempt++;
                segments.clearPath();
                Log.i("attempt: ", String.valueOf(attempt));
                // after the 3rd failed attempt, show the solution
                if (attempt >= 3) {
                    attempt = 0;
                    // draw the solution
                    for (int i = 0; i < shortestPath.size() - 1; i++) {
                        Point a = shortestPath.get(i);
                        Point b = shortestPath.get(i + 1);
                        paint.setColor(Color.YELLOW);
                        paint.setStrokeWidth(10);
                        canvas.drawLine(a.x+10, a.y+10, b.x+10, b.y+10, paint);
                    }
                    Point a = shortestPath.get(shortestPath.size()-1);
                    Point b = shortestPath.get(0);
                    paint.setColor(Color.YELLOW);
                    paint.setStrokeWidth(10);
                    canvas.drawLine(a.x+10, a.y+10, b.x+10, b.y+10, paint);

                    Toast.makeText(getContext(), "Nope, sorry! Here's the solution.", Toast.LENGTH_LONG).show();
                }
                else {
                    int offset = (int) (Math.abs(diff) / shortestPathLength * 100);
                    // so that we don't say that the path is 0% too long
                    if (offset == 0) {
                        offset = 1;
                    }
                    Toast.makeText(getContext(), "Nope, not quite! Your path is about " + offset + "% too long.", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (segments.getSize() == mapPoints.length && !segments.isOneCircuit(spinnerNum)) {
            Toast.makeText(getContext(), "That's not a circuit! Select Clear from the menu and start over", Toast.LENGTH_LONG).show();
        }

    }

    /*
     * This method is automatically called when the user touches the screen.
     */
    public boolean onTouchEvent(MotionEvent event) {

        Point p = new Point((int)event.getX(), (int)event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            // only add the segment if the touch point is within 30 of any of the other points
            for (int i = 0; i < mapPoints.length; i++) {
                LineSegment line = new LineSegment(p, mapPoints[i]);
                double dist = line.distance();
                if (dist < 30) {
                    // the "+10" part is a bit of a fudge factor because the point itself is the
                    // upper-left corner of the little red box but we want the center
                    p.x = mapPoints[i].x+10;
                    p.y = mapPoints[i].y+10;

                    firstPoint = p;
                    stroke.setValid(true);
                    stroke.addPoints(p);
                    break;
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (stroke.isValid()) {
                stroke.addPoints(p);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (stroke.isValid()) {
                // only add the segment if the release point is within 30 of any of the other points
                for (int i = 0; i < mapPoints.length; i++) {
                    LineSegment line = new LineSegment(p, mapPoints[i]);
                    double dist = line.distance();
                    if (dist < 30) {
                        p.x = mapPoints[i].x + 10;
                        p.y = mapPoints[i].y + 10;
                        LineSegment path = new LineSegment(firstPoint, p);
                        if (firstPoint.x != p.x && firstPoint.y != p.y) {

                            if(!segments.contains(path)){
                                segments.addPath(path);
                            }
                            else{
                                Toast.makeText(getContext(), "Try to make another stroke", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                    }
                }
            }
            stroke.setValid(false);
            stroke.clearPath();

        }
        else {
            return false;
        }


        // forces a redraw of the View
        invalidate();

        return true;
    }


}
