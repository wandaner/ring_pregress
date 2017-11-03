package com.wxh.rrmainview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by rrdev on 2017/8/24.
 */

public class RRGarbageView extends View {
    private final int alphaMax = 180;
    private final int alphaMin = 0;
    private final int alphaStep = 9;
    private int alpha = 0;
    private boolean isShowing = false;
    private int MAX_NUM = 1*36;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private Matrix mMatrix;
    private Bitmap bitmap;
    private ArrayList<Particle> list = new ArrayList<Particle>();
    private ValueAnimator mParticleAnim;


    public RRGarbageView(Context context) {
        super(context);
        init(context);
    }

    public RRGarbageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public RRGarbageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void init(Context context){
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.garbage_dot);
        mMatrix = new Matrix();
        mParticleAnim = ValueAnimator.ofInt(0).setDuration(30);
        mParticleAnim.setRepeatCount(ValueAnimator.INFINITE);
        mParticleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                invalidate();
            }
        });
    }

    public void setMode(Particle.Mode mode){
        if(list!=null&&list.size()>=0){
            for(Particle particle : list){
                particle.setSpeedMode(mode);
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMeasuredWidth == 0) {
            mMeasuredWidth = getMeasuredWidth();
            mMeasuredHeight = getMeasuredHeight();
        }
        if (list.size() == 0) {
            for (int i = 0; i < MAX_NUM; i++) {
                Particle f = new Particle(bitmap,mMatrix,mMeasuredWidth, mMeasuredHeight,i);
                list.add(f);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int a = getCurAlpha();
        for (Particle item : list) {
            item.draw(canvas,a);
        }
        canvas.restore();
    }
    public int getCurAlpha(){
        if(isShowing){
            if(alpha<alphaMax){
                alpha+=alphaStep;
            }
        }else {
            if(alpha>alphaMin){
                alpha-=alphaStep;
            }
        }
        return alpha;
    }
    public void show(){
        isShowing = true;
    }
    public void dismiss(){
        isShowing = false;
    }
    public void stop(){
        if (mParticleAnim.isStarted()) {
            mParticleAnim.cancel();
        }
    }
    public void start(){
        if (!mParticleAnim.isRunning()) {
            mParticleAnim.start();
        }
    }
    private int dip2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }

    public void destroy(){
        if(bitmap!=null){
            bitmap.recycle();
        }
        for(Particle particle :list){
            if(particle!=null){
                particle.destroy();
            }
        }
    }

}
