package com.wxh.rrmainview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * Created by xukai on 2017/11/02.
 */

public class HomeItemZoom extends RelativeLayout {
    public enum Mode{
        leftBottom,middleBottom,rightBottom
    }
    private static final float ScaleX = 1.1f;
    private static final float ScaleY = 1.1f;
    private static final float StartX = 1.0f;
    private static final float StartY = 1.0f;

    private Animation scaleSmallAnimation;
    private Animation scaleBigAnimation;

    private Mode mode = Mode.leftBottom;
    private boolean isClicked = false;
    public HomeItemZoom(Context context) {
        super(context);
        init();
    }

    public HomeItemZoom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HomeItemZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setWillNotDraw(false);
        setChildrenDrawingOrderEnabled(true);
    }
    public void setMode(Mode mode){
        this.mode = mode;
    }
    public void switchToClick(){
        if(!isClicked) {
            isClicked = true;
            zoomBig();
        }
    }
    public void switchToNormal(){
        if(isClicked) {
            isClicked = false;
            zoomSmall();
        }
    }

    private void zoomBig() {
        if(scaleBigAnimation == null) {
            //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
            switch (mode){
                case middleBottom:
                    scaleBigAnimation = new ScaleAnimation(StartX, ScaleX, StartY, ScaleY, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
                case rightBottom:
                    scaleBigAnimation = new ScaleAnimation(StartX, ScaleX, StartY, ScaleY, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
                case leftBottom:
                default:
                    scaleBigAnimation = new ScaleAnimation(StartX, ScaleX, StartY, ScaleY, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
            }
            //这个是设置动画时间的
            scaleBigAnimation.setDuration(100);
            //动画执行完毕后是否停在结束时的角度上
            scaleBigAnimation.setFillAfter(true);
        }
        //启动动画
        startAnimation(scaleBigAnimation);
        if(Build.VERSION.SDK_INT>=21) {
            setTranslationZ(20f);
            setElevation(20f);
        }
    }

    private void zoomSmall() {
        if (scaleSmallAnimation == null) {
            switch (mode){
                case middleBottom:
                    scaleSmallAnimation = new ScaleAnimation(ScaleX,StartX,ScaleY,StartY,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
                case rightBottom:
                    scaleSmallAnimation = new ScaleAnimation(ScaleX,StartX,ScaleY,StartY,Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
                case leftBottom:
                default:
                    scaleSmallAnimation = new ScaleAnimation(ScaleX,StartX,ScaleY,StartY,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF, 1.0f);
                    break;
            }

            //这个是设置动画时间的
            scaleSmallAnimation.setDuration(100);
            //动画执行完毕后是否停在结束时的角度上
            scaleSmallAnimation.setFillAfter(true);
        }
        startAnimation(scaleSmallAnimation);
        if(Build.VERSION.SDK_INT>=21) {
            setTranslationZ(0f);
            setElevation(0f);
        }
    }
}
