package com.scinc.sczbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Path;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue"})
public class ScanView extends View {

    private Paint mAnglePaint;
    private Paint mScanLinePaint;
    private Paint mBgPaint;

    private Region mBgRegion;

    private Path mScanLinePath;
    private Path mAnglePath;

    private int mScanLineVertical;  //扫描线的竖直位置

    private int mScanLineHeight = 5;  //扫描线的粗细
    private int mScanLineLength = 200;  //扫描线长度

    private int mAngleHalfWidth = 10;
    private int mAngleLength = 100;

    private int mAngleColor = Color.parseColor("#000000");
    private int mScanColor = Color.parseColor("#000000");
    private int mBgColor = Color.parseColor("#40000000");

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private int centerX;

    public ScanView(Context context) {
        super(context);
        init();
    }

    public ScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        getConfigFromAttr(context, attrs);
    }

    public ScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getConfigFromAttr(context, attrs);
    }

    private void getConfigFromAttr(Context context, AttributeSet attrs) {
        int angleLength = 0;
        int angleHalf = 0;

        int scanColor = 0;
        int angleColor = 0;
        int bgColor = 0;

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScanView);
        angleLength = (int) ta.getDimension(R.styleable.ScanView_angle_length, 0);
        angleHalf = (int) ta.getDimension(R.styleable.ScanView_angle_half_width, 0);
        scanColor = ta.getColor(R.styleable.ScanView_scan_line_color, 0);
        angleColor = ta.getColor(R.styleable.ScanView_angle_line_color, 0);
        bgColor = ta.getColor(R.styleable.ScanView_shadow_color, 0);
        left = (int) ta.getDimension(R.styleable.ScanView_scan_left, 0);
        top = (int) ta.getDimension(R.styleable.ScanView_scan_top, 0);
        right = (int) ta.getDimension(R.styleable.ScanView_scan_right, 0);
        bottom = (int) ta.getDimension(R.styleable.ScanView_scan_bottom, 0);
        ta.recycle();


    }

    private void init() {
        mAnglePaint = new Paint();
        mScanLinePaint = new Paint();
        mBgPaint = new Paint();

        mAnglePaint.setStyle(Paint.Style.STROKE);
        mAnglePaint.setStrokeWidth(2 * mAngleHalfWidth);
        mAnglePaint.setColor(mAngleColor);

        mScanLinePaint.setStyle(Paint.Style.STROKE);
        //noinspection SuspiciousNameCombination
        mScanLinePaint.setStrokeWidth(mScanLineHeight);
        mScanLinePaint.setColor(mScanColor);

        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBgPaint.setColor(mBgColor);

        mScanLinePath = new Path();
        mAnglePath = new Path();

        mBgRegion = new Region();
    }

    private void setAngle() {
        mAnglePath.moveTo(mLeft + mAngleHalfWidth, mTop + mAngleLength - mAngleHalfWidth);
        mAnglePath.lineTo(mLeft + mAngleHalfWidth, mTop + mAngleHalfWidth);
        mAnglePath.lineTo(mLeft + mAngleLength - mAngleHalfWidth, mTop + mAngleHalfWidth);

        mAnglePath.moveTo(mRight - mAngleLength + mAngleHalfWidth, mTop + mAngleHalfWidth);
        mAnglePath.lineTo(mRight - mAngleHalfWidth, mTop + mAngleHalfWidth);
        mAnglePath.lineTo(mRight - mAngleHalfWidth, mTop + mAngleLength - mAngleHalfWidth);

        mAnglePath.moveTo(mRight - mAngleLength + mAngleHalfWidth, mBottom - mAngleHalfWidth);
        mAnglePath.lineTo(mRight - mAngleHalfWidth, mBottom - mAngleHalfWidth);
        mAnglePath.lineTo(mRight - mAngleHalfWidth, mBottom - mAngleLength + mAngleHalfWidth);

        mAnglePath.moveTo(mLeft + mAngleHalfWidth, mBottom - mAngleLength + mAngleHalfWidth);
        mAnglePath.lineTo(mLeft + mAngleHalfWidth, mBottom - mAngleHalfWidth);
        mAnglePath.lineTo(mLeft + mAngleLength - mAngleHalfWidth, mBottom - mAngleHalfWidth);
    }

    public void setScanRegion(int left, int top, int right, int bottom) {
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
        centerX = (mRight - mLeft) / 2 + mLeft;
        setAngle();
    }

    private void updateScanLineVertical() {
        mScanLineVertical %= (mBottom - mTop - 4 * mAngleHalfWidth);
        mScanLineVertical += 5;

        mScanLineLength = mRight - mLeft - 20 * mAngleHalfWidth;

        mScanLinePath.reset();
        mScanLinePath.moveTo(centerX - mScanLineLength / 2, mScanLineVertical + mTop + 2 * mAngleHalfWidth);
        mScanLinePath.lineTo(centerX + mScanLineLength / 2, mScanLineVertical + mTop + 2 * mAngleHalfWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mAnglePath, mAnglePaint);
        canvas.drawPath(mScanLinePath, mScanLinePaint);
        drawRegion(canvas, mBgRegion, mBgPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBgRegion.set(0, 0, getWidth(), getHeight());
        mBgRegion.op(mLeft, mTop, mRight, mBottom, Region.Op.DIFFERENCE);
    }

    private void drawRegion(Canvas canvas, Region rgn, Paint paint) {
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        while (iter.next(r)) {
            canvas.drawRect(r, paint);
        }
    }

    public void startScan() {
        if (alive) {
            return;
        }
        alive = true;
        Thread thread = new Thread(new Signal());
        thread.start();
    }

    public void stopScan() {
        alive = false;
    }

    private volatile boolean alive = false;

    private class Signal implements Runnable {

        @Override
        public void run() {
            while (alive) {
                try {
                    Thread.sleep(32);
                    ScanView.this.updateScanLineVertical();
                    ScanView.this.postInvalidate();
                    Log.d("ScanView ", "run----");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("ScanView ", "finish");
        }
    }
}
