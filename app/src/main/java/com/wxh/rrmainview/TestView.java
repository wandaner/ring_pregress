package com.wxh.rrmainview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * Created by xukai on 2017/09/29.
 */

public class TestView extends FrameLayout {
    private RRView rrView;
    private RRCenterView rrCenterView;
    private boolean callStopRRView = false;
    private boolean callStopRRCenterView = false;
    private ObjectAnimator rrViewAnimator;
    private ObjectAnimator rrCenterViewAnimator;
    public TestView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private void init(Context context){
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        rrView = new RRView(context);
        rrCenterView = new RRCenterView(context);
        rrView.setCenterView(rrCenterView);

        rrViewAnimator = getAnimator(rrView);
        rrCenterViewAnimator = getAnimator(rrCenterView);

        rrViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(animation.getAnimatedFraction()>0.98){
                    if(callStopRRView){
                        callStopRRView = false;
                        rrViewAnimator.cancel();
                    }
                }
            }
        });

        rrCenterViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(animation.getAnimatedFraction()>0.98){
                    if(callStopRRCenterView){
                        callStopRRCenterView = false;
                        rrCenterViewAnimator.cancel();
                    }
                }
            }
        });
        addView(rrView,layoutParams);
        addView(rrCenterView,layoutParams);
    }
    private ObjectAnimator getAnimator(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"rotation",0f,359f).setDuration(2000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(-1);
        LinearInterpolator lir = new LinearInterpolator();
        animator.setInterpolator(lir);
        return animator;
    }
    public void show(){
        rrView.show();
    }

    public void miss(){
        rrView.miss();
    }
    public void run(){
        if(!rrViewAnimator.isStarted()) {
            rrViewAnimator.start();
            rrCenterViewAnimator.start();
        }
    }
    public void stopRRView(){
        if(!callStopRRView){
            callStopRRView = true;
        }
    }
    public void stopRRCenterView(){
        if(!callStopRRCenterView){
            callStopRRCenterView = true;
        }
    }
}
