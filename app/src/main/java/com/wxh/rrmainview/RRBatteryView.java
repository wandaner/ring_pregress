package com.wxh.rrmainview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xukai on 2017/10/26.
 */

public class RRBatteryView extends View {
        private static final float maxCount=100f;
        private static final float limitCount = 20f;
        private static final float padRadio=0.4f;
        private static final float spotRingRadio = 0.8f;
        private static final int spotN = 24;
        private int stepAlpha = 22;
        private int step = 1;
        private int currentSpot = 0;
        private int lightSpot = 0;
        private int stop = 0;

        private int currentStep = 1;
        private static final float startDegree = 300f;
        private static final float fullDegree = 360f;
        private static final float p1 = 1f;
        private static final float p2 = 2f;
        private static final float gap = 1.5f;
        private float currentCount=12;
        private Bitmap dot;
        private Bitmap spot;
        private Paint lightPaint;
        private Paint dotPaint;
        private Paint spotLightPaint;
        private Paint spotGrayPaint;
        private Paint spotShinnerPaint;
        private Paint grayPaint;
        private Paint mTextPaint;
        private int mWidth, mHeight;
        private Context context;
        //圆心坐标
        private float cenX;
        private float cenY;
        //进度环半径
        private float radius;
        //进度光点环半径
        private float radius_spot;
        //进度光点半径
        private float spotRadius;
        //大光点图片半径
        private int dotRadius;
        //环绘制区域
        private RectF rectBlackBg;

        private Timer timer;
        private TimerTask timerTask;

        public RRBatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init(context);
        }
        public RRBatteryView(Context context, AttributeSet attrs) {
                this(context, attrs, 0);
        }
        public RRBatteryView(Context context) {
                this(context, null);
        }
        private void init(Context context) {
                this.context = context;
                lightPaint = new Paint();
                grayPaint = new Paint();
                mTextPaint = new Paint();
                dotPaint = new Paint();
                spotLightPaint = new Paint();
                spotGrayPaint = new Paint();
                spotShinnerPaint = new Paint();
                dot = BitmapFactory.decodeResource(context.getResources(),R.drawable.battery_light_dot);
                spot = BitmapFactory.decodeResource(context.getResources(),R.drawable.battery_light_spot);
                dotRadius = dpToPx(10);
                spotRadius = dpToPx(4);
                onResume();
        }
        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                initPaint();

                float section = currentCount / maxCount;
                float degree = -1*(startDegree-section*fullDegree);
                float dotX = (float) (cenX+Math.cos(Math.toRadians(degree))*radius);
                float dotY = (float) (cenY-Math.sin(Math.toRadians(degree))*radius);

//                绘制电量数字
                canvas.drawText(currentCount+" ", mWidth / 2, mHeight / 2 + 50,mTextPaint);
                if(currentCount<0){
                        return;
                }else if(currentCount<=p1){
                        canvas.drawArc(rectBlackBg,startDegree-section*fullDegree,section*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-p1/maxCount*fullDegree,(p1/maxCount-section)*fullDegree,false,grayPaint);
                        canvas.drawArc(rectBlackBg,startDegree-(p1+gap+p2)/maxCount*fullDegree,p2/maxCount*fullDegree,false,grayPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-(p1+p2+gap*2)/maxCount)*fullDegree,false,grayPaint);
                        canvas.drawBitmap(dot,null,new RectF(dotX-dotRadius,dotY-dotRadius,dotX+dotRadius,dotY+dotRadius),dotPaint);
                }else if(currentCount<=(p1+gap)) {
                        canvas.drawArc(rectBlackBg,startDegree-(p1/maxCount)*fullDegree,(p1/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-(p1+gap+p2)/maxCount*fullDegree,p2/maxCount*fullDegree,false,grayPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-(p1+p2+gap*2)/maxCount)*fullDegree,false,grayPaint);
                }else if(currentCount<=(p1+gap+p2)){
                        canvas.drawArc(rectBlackBg,startDegree-(p1/maxCount)*fullDegree,(p1/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-section*fullDegree,(section-(p1+gap)/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-(p1+gap+p2)/maxCount*fullDegree,((p1+p2+gap)/maxCount-section)*fullDegree,false,grayPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-(p1+p2+gap*2)/maxCount)*fullDegree,false,grayPaint);
                        canvas.drawBitmap(dot,null,new RectF(dotX-dotRadius,dotY-dotRadius,dotX+dotRadius,dotY+dotRadius),dotPaint);
                }else if(currentCount<=(p1+p2+gap*2)){
                        canvas.drawArc(rectBlackBg,startDegree-(p1/maxCount)*fullDegree,(p1/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-(p1+gap+p2)/maxCount*fullDegree,p2/maxCount*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-(p1+p2+gap*2)/maxCount)*fullDegree,false,grayPaint);
                }else if(currentCount<=limitCount) {
                        canvas.drawArc(rectBlackBg,startDegree-(p1/maxCount)*fullDegree,(p1/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-(p1+gap+p2)/maxCount*fullDegree, p2/maxCount*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-section*fullDegree,(section-(p1+p2+gap*2)/maxCount)*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-section)*fullDegree,false,grayPaint);
                        canvas.drawBitmap(dot,null,new RectF(dotX-dotRadius,dotY-dotRadius,dotX+dotRadius,dotY+dotRadius),dotPaint);
                }else {
                        canvas.drawArc(rectBlackBg,startDegree-section*fullDegree,section*fullDegree,false,lightPaint);
                        canvas.drawArc(rectBlackBg,startDegree-fullDegree,(1-section)*fullDegree,false,grayPaint);
                        canvas.drawBitmap(dot,null,new RectF(dotX-dotRadius,dotY-dotRadius,dotX+dotRadius,dotY+dotRadius),dotPaint);
                }
                //绘制光点环
                if(currentCount<maxCount){
                        if(currentSpot == 0){
                                lightSpot = (int) (currentCount/maxCount*24);
                        }
                        for(int i=0;i<lightSpot;i++){
                                float spotDegree = -1*(startDegree-15*i-7.5f);
                                float spotX = (float) (cenX+Math.cos(Math.toRadians(spotDegree))*radius_spot);
                                float spotY = (float) (cenY-Math.sin(Math.toRadians(spotDegree))*radius_spot);
                                canvas.drawBitmap(spot, null, new RectF(spotX - spotRadius,
                                        spotY - spotRadius, spotX + spotRadius, spotY + spotRadius), spotLightPaint);
                        }
                        for(int i=0;i+lightSpot<spotN;i++){
                                float spotDegree = -1*(startDegree-15*(i+lightSpot)-7.5f);
                                float spotX = (float) (cenX+Math.cos(Math.toRadians(spotDegree))*radius_spot);
                                float spotY = (float) (cenY-Math.sin(Math.toRadians(spotDegree))*radius_spot);
                                if(i==currentSpot){
                                        canvas.drawBitmap(spot, null, new RectF(spotX - spotRadius,
                                                spotY - spotRadius, spotX + spotRadius, spotY + spotRadius), spotShinnerPaint);
                                }else {
                                        canvas.drawBitmap(spot, null, new RectF(spotX - spotRadius,
                                                spotY - spotRadius, spotX + spotRadius, spotY + spotRadius), spotGrayPaint);
                                }
                        }
                        if(currentStep>=10){
                                step = -1;
                        }
                        if(currentStep<=0){
                                if(stop>=15) {
                                        step = 1;
                                        if (currentSpot + lightSpot + 1 >= spotN) {
                                                currentSpot = 0;
                                        } else {
                                                currentSpot = currentSpot + 1;
                                        }
                                        stop = 0;
                                }else {
                                        step = 0;
                                        stop++;
                                }
                        }
                        spotShinnerPaint.setColor(Color.argb(35+currentStep*stepAlpha,255,255,255));
                        currentStep+=step;
                }else {
                        for (int i = 0; i < spotN; i++) {
                                float spotDegree = -1 * (startDegree - 15 * i-7.5f);
                                float spotX = (float) (cenX + Math.cos(Math.toRadians(spotDegree)) * radius_spot);
                                float spotY = (float) (cenY - Math.sin(Math.toRadians(spotDegree)) * radius_spot);
                                canvas.drawBitmap(spot, null, new RectF(spotX - spotRadius,
                                        spotY - spotRadius, spotX + spotRadius, spotY + spotRadius), spotLightPaint);
                        }
                }
        }
        private void initPaint() {
                lightPaint.setAntiAlias(true);
                lightPaint.setFilterBitmap(true);
                lightPaint.setStrokeWidth(dpToPx(4f));
                lightPaint.setStyle(Paint.Style.STROKE);
                lightPaint.setStrokeCap(Paint.Cap.ROUND);
                lightPaint.setColor(Color.TRANSPARENT);
                lightPaint.setColor(Color.argb(255,255,255,255));

                grayPaint.setAntiAlias(true);
                grayPaint.setFilterBitmap(true);
                grayPaint.setStrokeWidth(dpToPx(4f));
                grayPaint.setStyle(Paint.Style.STROKE);
                grayPaint.setStrokeCap(Paint.Cap.ROUND);
                grayPaint.setColor(Color.TRANSPARENT);
                grayPaint.setColor(Color.argb(35,255,255,255));

                dotPaint.setAntiAlias(true);
                dotPaint.setFilterBitmap(true);
                dotPaint.setColor(Color.argb(255,255,255,255));

                spotLightPaint.setAntiAlias(true);
                spotLightPaint.setFilterBitmap(true);
                spotLightPaint.setColor(Color.argb(255,255,255,255));

                spotGrayPaint.setAntiAlias(true);
                spotGrayPaint.setFilterBitmap(true);
                spotGrayPaint.setColor(Color.argb(35,255,255,255));

                spotShinnerPaint.setAntiAlias(true);
                spotShinnerPaint.setFilterBitmap(true);

                mTextPaint.setAntiAlias(true);
                mTextPaint.setStrokeWidth(dpToPx(3));
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                mTextPaint.setTextSize(50);
                mTextPaint.setColor(Color.WHITE);
                mTextPaint.setTextSize(spToPx(17));
        }
        private int dpToPx(float dp){
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        dp, context.getResources().getDisplayMetrics());
        }
        private int spToPx(float sp){
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        sp, context.getResources().getDisplayMetrics());
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
                cenX = mWidth/2f;
                cenY = mHeight/2f;
                float padding = mWidth/2f*padRadio;
                radius = mWidth/2f-padding;
                radius_spot = mWidth/2f*spotRingRadio;
                rectBlackBg = new RectF(padding, padding, mWidth - padding, mHeight - padding);
        }

        public void onPause(){
                if(timerTask!=null){
                        timerTask.cancel();
                }
                if(timer!=null){
                        timer.cancel();
                }
        }
        public void onResume(){
                timerTask = new TimerTask() {
                        @Override
                        public void run() {
                                postInvalidate();
                        }
                };
                timer = new Timer();
                timer.schedule(timerTask,0,30);
        }
        public void onDestroy(){
                if(timerTask!=null){
                        timerTask.cancel();
                }
                if(timer!=null){
                        timer.cancel();
                }
                if(spot!=null){
                        spot.recycle();
                }
                if(dot!=null){
                        dot.recycle();
                }
        }
        public void setCurrentCount(float currentCount) {
                this.currentCount = currentCount;
        }
}
