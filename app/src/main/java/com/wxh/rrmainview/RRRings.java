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

public class RRRings extends FrameLayout {
    private RROtherRing rrOtherRing;
    private RRCenterRing rrCenterRing;
    private RRGarbageView rrGarbageView;
    private boolean callStopRROtherRing = false;
    private boolean callResumeRROtherRing = false;
    private boolean callStopRRCenterRing = false;
    private ObjectAnimator rrOtherRingAnimator;
    private ObjectAnimator rrCenterRingAnimator;
    public RRRings(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RRRings(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RRRings(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private void init(Context context){
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        rrOtherRing = new RROtherRing(context);
        rrCenterRing = new RRCenterRing(context);
        rrOtherRing.setCenterView(rrCenterRing);
        rrGarbageView = new RRGarbageView(context);
        addView(rrOtherRing,layoutParams);
        addView(rrCenterRing,layoutParams);
        addView(rrGarbageView,layoutParams);
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
        rrOtherRing.show();
    }
    public void setMode(Particle.Mode mode){
        rrGarbageView.setMode(mode);
    }
    public void miss(){
        rrOtherRing.miss();
    }
    public void run(){
        runRRCenterRing();
        runRROtherRing();
        rrGarbageView.start();
        rrGarbageView.show();
    }
    public void runRROtherRing(){
        if(rrOtherRingAnimator==null) {
            rrOtherRingAnimator = getAnimator(rrOtherRing);
            rrOtherRingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(animation.getAnimatedFraction()>0.98){
                        if(callStopRROtherRing){
                            callStopRROtherRing = false;
                            rrOtherRingAnimator.cancel();
                        }
                    }
                }
            });
        }
        if(!rrOtherRingAnimator.isStarted()){
            rrOtherRingAnimator.start();
        }
    }
    public void runRRCenterRing(){
        if(rrCenterRingAnimator==null) {
            rrCenterRingAnimator = getAnimator(rrCenterRing);
            rrCenterRingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(animation.getAnimatedFraction()>0.98){
                        if(callStopRRCenterRing){
                            callStopRRCenterRing = false;
                            rrCenterRingAnimator.cancel();
                        }else if(callResumeRROtherRing){
                            callResumeRROtherRing=false;
                            runRROtherRing();
                        }
                    }
                }
            });
        }
        if(!rrCenterRingAnimator.isStarted()){
            rrCenterRingAnimator.start();
        }
    }
    public void resumeRROtherRing(){
        if(!callResumeRROtherRing){
            callResumeRROtherRing = true;
        }
    }
    public void stopRROtherRing(){
        if(!callStopRROtherRing){
            callStopRROtherRing = true;
        }
    }
    public void stopRRCenterRing(){
        if(!callStopRRCenterRing){
            callStopRRCenterRing = true;
        }
    }
}
