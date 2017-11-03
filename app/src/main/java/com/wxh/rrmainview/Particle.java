package com.wxh.rrmainview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rrdev on 2017/8/24.
 */

public class Particle {
    public enum Mode{
        slient,standard,powerFull,fullSpeed
    }
    private boolean type;                   //判断点所在位置，中点左边:true  右边:false
    private float speed = 4;
    private float origenSpeed = 4;
    private float upSpeedRatio = 1;
    private float speedX;
    private float speedY;

    private double position_ratio_x;
    private double position_ratio_y;

    private Random mRandom = new Random();
    private int canvas_width;
    private int canvas_height;
    private double diagonal;
    private int canvas_cenX;
    private int canvas_cenY;
    private Bitmap mBitmap;
//    private Bitmap tempBitmap;
    private float bitmap_scale;
    private Matrix bitmap_matrix;

    private Paint mPaint;
    private double distance;
    private float x;
    private float y;
    private double edge_distance;

    private int w;
    private int h;
    private int radius;
    private int normal_w;
    private int normal_h;

    private int cat_x;//消失的时候，图片裁剪参数



    private int id;

    private float degree;
    private ArrayList<Bitmap> bitList = null;


    public Particle(Bitmap bitmap, Matrix matrix, int width, int height, int id){
        mBitmap = bitmap;
        normal_w = bitmap.getWidth();
        normal_h = bitmap.getHeight();
        canvas_width = width;
        canvas_height = height;
        bitmap_matrix = matrix;
        diagonal = Math.sqrt(width*width+height*height)/2;
//        tempBitmap = null;
        this.id = id;
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(120);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        reset();
    }
    public void setSpeedMode(Mode mode){
        switch (mode){
            case slient:
                upSpeedRatio = 0.5f;
                break;
            case standard:
                upSpeedRatio = 1f;
                break;
            case powerFull:
                upSpeedRatio = 1.7f;
                break;
            case fullSpeed:
                upSpeedRatio = 2.5f;
                break;
        }
        speed = origenSpeed*upSpeedRatio;
    }
    public void draw(Canvas canvas, int alpha){
        double d_value = getDistanceDValue();
        mPaint.setAlpha(alpha);
        if(d_value<40){
            speed*=1.5;
        }
        if(d_value>0){
            bitmap_matrix.reset();
            bitmap_matrix.preTranslate(x+=speedX,y+=speedY);
            bitmap_matrix.preScale(bitmap_scale,bitmap_scale);
            canvas.drawBitmap(mBitmap,bitmap_matrix,mPaint);
            adjustDegreeAndSpeed();
        }else{
            cat_x+=d_value;
            if(cat_x>0){
                bitmap_matrix.reset();
                bitmap_matrix.preTranslate(x+=speedX,y+=speedY);
                bitmap_matrix.preScale(bitmap_scale,bitmap_scale);
                bitmap_matrix.preRotate(degree,normal_w/2,normal_w/2);
                Bitmap temp = Bitmap.createBitmap(mBitmap, 0, 0, cat_x, normal_h, null, false);
                canvas.drawBitmap(temp,bitmap_matrix,mPaint);
                bitList.add(temp);
                adjustDegreeAndSpeed();
            }else {
                reset();
            }
        }
    }
    private void reset(){
        if(bitList!=null) {
            for (Bitmap bitmap : bitList) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        origenSpeed = 3+1.5f*mRandom.nextFloat();
        speed = origenSpeed*upSpeedRatio;
        bitList = new ArrayList<Bitmap>();
        //随机缩放图片，0.3-0.5之间,其实圆形长宽都是直径，w和h相等的
        bitmap_scale = 0.8f+mRandom.nextFloat()*0.5f;
        w = (int) (mBitmap.getWidth()*bitmap_scale);
        h = (int) (mBitmap.getHeight()*bitmap_scale);

        radius = (int) (w/2.0);
        //屏幕中点
        canvas_cenX = canvas_width/2-radius;
        canvas_cenY = canvas_height/2-radius;

//        //图片在屏幕的位置方案1
//        setRandomXYRatio();
//        x = (float) (position_ratio_x*canvas_width-radius);
//        y = (float) (position_ratio_y*canvas_height-radius);

        //图片在屏幕的位置方案2
        setXY();

        type = x<canvas_cenX;
        cat_x = normal_w;
        //速度分解:横向和竖向的速度

        edge_distance = (getRDistanceRatioOfWidth()*canvas_width);
        //根据正切值计算需要旋转的角度
        adjustDegreeAndSpeed();

    }

    private void setXY(){
        int a = mRandom.nextInt(36);
        int b = id*10;
        distance = (mRandom.nextDouble()*0.5+0.5)*(diagonal+400);
        x = (float) (canvas_cenX+distance* Math.cos(b)-radius);
        y = (float) (canvas_cenY-distance* Math.sin(b)-radius);
    }
    //每次移动绘制完后再次计算x y速度以及距离中点
    private void adjustDegreeAndSpeed(){
        distance  = computeDistance();
        speedX = (float) (-speed*(x - canvas_cenX)/distance);
        speedY = (float) (-speed*(y - canvas_cenY)/distance);
        if(x-canvas_cenX==0){
            if(y>canvas_cenY){
                degree = 90f+270f;
            }else {
                degree = 90f;
            }
        }else {
            if (type) {
                degree = (float) Math.toDegrees(Math.atan((y - canvas_cenY) * 1.0 / (x - canvas_cenX)));
            } else {
                degree = 180f+(float) Math.toDegrees(Math.atan((y - canvas_cenY) * 1.0 / (x - canvas_cenX)));
            }
        }
    }
    //计算中心距离和阈值的差
    private double getDistanceDValue(){
        return distance-edge_distance;
    }
    //计算当前点到屏幕中心的距离
    private double computeDistance(){
        return Math.sqrt((x-canvas_cenX)*(x-canvas_cenX)+(y-canvas_cenY)*(y-canvas_cenY));
    }

    private double getLimitYRatio() {
        double v = mRandom.nextDouble();
        if (v>0.4f&&v<0.6) {
            v = getLimitYRatio();
        }
        return v;
    }
    //设置点在屏幕中的初始位置比例
    private void setRandomXYRatio(){
        position_ratio_x = mRandom.nextDouble();
        if(position_ratio_x<0.35||position_ratio_x>0.65){
            position_ratio_y = mRandom.nextDouble();
        }else {
            position_ratio_y = getLimitYRatio();
        }
    }
    private double getRDistanceRatioOfWidth(){
        return mRandom.nextFloat()*0.1+0.15;
    }
    public void destroy(){
        if(bitList!=null) {
            for (Bitmap bitmap : bitList) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

}
