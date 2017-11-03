package com.wxh.rrmainview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xukai on 2017/11/01.
 */

public class WhorlView extends View {
    private static final float paddingRatio = 0.2f;
    private static final float spacingRatio = 0.1f;
    private float padding;
    private float spacing;
    private float cenX;
    private float cenY;
    private float radius;
    private float mCenX;
    private float mCenY;
    private int mWidth;
    private int mHeight;
    private int totalDegree = 750;
    private int eachDegree = 180;
    private int startDegree = 0;
    private static final float step = 2f;
    private int currentAlpha = 255;
    private int stepAlpha = 5;
    private int total_stay = 40;
    private int current_stay = 0;
    private Context context;
    private Paint mPaint;
    private TimerTask timerTask;
    private Timer timer;
    public WhorlView(Context context) {
        super(context);
        init(context);
    }

    public WhorlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WhorlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255,255,255,255));
        mPaint.setStrokeWidth(spToPx(4));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int index = totalDegree / eachDegree+1;
        int exDegree = totalDegree%eachDegree;
        for (int i=1;i<index;i++) {
            radius = spacing * i;
            cenX = mCenX + (i % 2) * spacing;
            cenY = mCenY;
            RectF rectF = new RectF(cenX - radius - dpToPx(4), cenY - radius - dpToPx(4), cenX + radius + dpToPx(4), cenY + radius + dpToPx(4));
            canvas.drawArc(rectF, startDegree + 180f * ((i + 1) % 2), eachDegree, false, mPaint);
        }
        radius = spacing * index;
        cenX = mCenX + (index % 2) * spacing;
        cenY = mCenY;
        RectF rectF = new RectF(cenX - radius - dpToPx(4), cenY - radius - dpToPx(4), cenX + radius + dpToPx(4), cenY + radius + dpToPx(4));
        canvas.drawArc(rectF, startDegree + 180f * (index % 2)-exDegree, exDegree, false, mPaint);

        if(totalDegree>=360*2.5f){
            if(current_stay<total_stay){
                current_stay++;
            }else {
                if (currentAlpha <= 0) {
                    currentAlpha = 255;
                    totalDegree = 0;
                    current_stay = 0;
                    mPaint.setColor(Color.argb(currentAlpha, 255, 255, 255));
                } else {
                    currentAlpha -= stepAlpha;
                    mPaint.setColor(Color.argb(currentAlpha, 255, 255, 255));
                }
            }
        }else {
            totalDegree+=step;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY
                || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dpToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        if(mWidth<mHeight){
            mHeight = mWidth;
        }else {
            mWidth = mHeight;
        }
        setMeasuredDimension(mWidth, mHeight);
        initSize();
    }
    private void initSize(){
        mCenX = mWidth/2f;
        mCenY = mHeight/2f;
        spacing = mCenX*spacingRatio;
        padding = mCenX*paddingRatio;
    }

    private int dpToPx(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
    private int spToPx(float sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }
}
