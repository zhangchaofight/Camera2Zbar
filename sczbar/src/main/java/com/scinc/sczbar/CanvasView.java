package com.scinc.sczbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

@SuppressWarnings("unused")
public class CanvasView extends View {

    private Paint mPaint;
    private Paint mPathPaint;

    private Path path;
    private Path horPath;

    private int lineWidth = 5;
    private int lineLength = 60;
    private int viewWidth = 300;
    private int viewHeight = 300;

    private volatile int lineVertical = 50;

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }


    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    private void init() {
        mPaint = new Paint();
        mPathPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.parseColor("#00ffff"));
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(lineWidth * 2);
        mPathPaint.setColor(Color.parseColor("#8e8e8e"));

        path = new Path();
        horPath = new Path();

        Thread thread = new Thread(new Signal());
        thread.start();
    }

    private void updateLineRound() {
        int x0 = lineWidth;
        int x1 = lineLength - lineWidth;
        int x2 = viewWidth - lineLength + lineWidth;
        int x3 = viewWidth - lineWidth;
        int y2 = viewHeight - lineLength + lineWidth;
        int y3 = viewHeight - lineWidth;
        //noinspection SuspiciousNameCombination
        path.moveTo(x0, x1);
        //noinspection SuspiciousNameCombination
        path.lineTo(x0, x0);
        //noinspection SuspiciousNameCombination
        path.lineTo(x1, x0);

        //noinspection SuspiciousNameCombination
        path.moveTo(x2, x0);
        //noinspection SuspiciousNameCombination
        path.lineTo(x3, x0);
        //noinspection SuspiciousNameCombination
        path.lineTo(x3, x1);

        path.moveTo(x3, y2);
        path.lineTo(x3, y3);
        path.lineTo(x2, y3);

        path.moveTo(x1, y3);
        path.lineTo(x0, y3);
        path.lineTo(x0, y2);
    }

    private void updateLineVertical() {
        lineVertical += 5;
        lineVertical %= (viewHeight - 4 * lineWidth);

        horPath.reset();
        horPath.moveTo(30, lineVertical + 2 * lineWidth);
        horPath.lineTo(viewWidth - 30, lineVertical + 2 * lineWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        setViewHeight(getHeight());
        setViewWidth(getWidth());
        updateLineRound();
        updateLineVertical();
    }

    public void start() {
        if (alive) {
            return;
        }alive = true;
        Thread thread = new Thread(new Signal());
        thread.start();
    }

    public boolean isAlive() {
        return alive;
    }

    public void stop() {
        alive = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, mPathPaint);
        canvas.drawPath(horPath, mPaint);
    }

    private volatile boolean alive = true;

    private class Signal implements Runnable {

        @Override
        public void run() {
            while (alive) {
                try {
                    Thread.sleep(32);
                    CanvasView.this.updateLineVertical();
                    CanvasView.this.postInvalidate();
                    Log.d("CanvasView ", "run: ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}