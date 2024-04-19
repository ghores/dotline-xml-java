package project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameView extends View {

    private Paint dotPaint;
    private Paint touchPaint;
    private Paint textPaint;
    private Paint linePaint;
    private int cols = 4;
    private int rows = 4;
    private int offsetY = 100;
    private int offsetX = 100;
    private int space = 150;
    private int radius = 15;

    private float touchX;
    private float touchY;

    private boolean isDebugMode = false;
    private boolean isSide1 = true;

    private ArrayList<Line> lines = new ArrayList<>();

    private static class Position {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Diff {
        public int i;
        public int j;
        public Float diff;

        public Diff(int i, int j, float diff) {
            this.i = i;
            this.j = j;
            this.diff = diff;
        }
    }

    private static class Line {
        public int i1;
        public int j1;
        public int i2;
        public int j2;
        public int playerIndex;

        public Line(int i1, int j1, int i2, int j2, int playerIndex) {
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.playerIndex = playerIndex;
        }
    }

    public GameView(Context context) {
        super(context);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }


    private void initialize() {
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        touchPaint = new Paint();
        touchPaint.setColor(Color.RED);
        touchPaint.setStyle(Paint.Style.FILL);
        touchPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#4444ff"));
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(10);
        linePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int boxWidth = (cols - 1) * space;
        int boxHeight = (rows - 1) * space;

        DisplayMetrics dp = G.context.getResources().getDisplayMetrics();
        int screenWidth = dp.widthPixels;
        int screenHeight = dp.heightPixels;

        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#222222"));

        for (Line line :
                lines) {
            connect(canvas, line);
        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position point = computePoint(i, j);
                canvas.drawCircle(point.x, point.y, radius, dotPaint);
            }
        }

        if (isDebugMode) {
            canvas.drawCircle(touchX, touchY, 10, touchPaint);
            debugNaming(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();

        detectConnectingLine();
        invalidate();
        isSide1 = !isSide1;
        return super.onTouchEvent(event);
    }

    public void detectConnectingLine() {
        ArrayList<Diff> diffs = new ArrayList<>();

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                Position position = computePoint(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(i, j, diff));
            }
        }
        Collections.sort(diffs, new Comparator<Diff>() {
            @Override
            public int compare(Diff o1, Diff o2) {
                return o1.diff.compareTo(o2.diff);
            }
        });
        Diff min1 = diffs.get(0);
        Diff min2 = diffs.get(1);

        if (min1.diff > space / 2) {
            return;
        }

        lines.add(new Line(min1.i, min1.j, min2.i, min2.j, isSide1 ? 1 : 2));
        Log.i("LOG", "" + min1.i + ", " + min1.j);
        Log.i("LOG", "" + min2.i + ", " + min2.j);
    }

    private float computeDiff(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void connect(Canvas canvas, Line line) {
        Position p1 = computePoint(line.i1,line.j1);
        Position p2 = computePoint(line.i2, line.j2);
        if (line.playerIndex == 1) {
            linePaint.setColor(Color.parseColor("#4444ff"));
        } else {
            linePaint.setColor(Color.parseColor("#ff4444"));
        }
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
    }

    private void debugNaming(Canvas canvas) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                String name = "" + i + "," + j;
                Position point = computePoint(i, j);
                canvas.drawText(name, point.x, point.y + 50, textPaint);
            }
        }
    }

    private Position computePoint(int i, int j) {
        int x = offsetX + (i * space);
        int y = offsetY + ((rows - 1 - j) * space);
        return new Position(x, y);
    }
}
