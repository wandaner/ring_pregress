package com.wxh.rrmainview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by rrdev on 2017/8/22.
 */

public class RRRingsView extends FrameLayout {
    enum RingState{
        blank,cleaning,loadState,waiting,charging
    }
    private final int N = 12;
    private static final int[] ring_id = {
            R.raw.rr_ring_2,R.raw.rr_ring_1,R.raw.rr_ring_3,
            R.raw.rr_ring_4,R.raw.rr_ring_5,R.raw.rr_ring_6,
            R.raw.rr_ring_7,R.raw.rr_ring_8,R.raw.rr_ring_9,
            R.raw.rr_ring_10,R.raw.rr_ring_11,R.raw.rr_ring_12};

    private RingState state = RingState.blank;
    private boolean isCallStop[] = new boolean[N];
    private boolean isShow[] = new boolean[N];
    private boolean isCallClean = false;
    private boolean isCallCharge = false;
    private Context context;
    private ArrayList<ObjectAnimator> alphaList_show = new ArrayList<ObjectAnimator>() ;
    private ArrayList<ObjectAnimator> alphaList_miss = new ArrayList<ObjectAnimator>() ;
    private ArrayList<ObjectAnimator> alphaList_wave = new ArrayList<ObjectAnimator>() ;
    private ArrayList<ObjectAnimator> rotateList = new ArrayList<ObjectAnimator>();
    private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
    public RRRingsView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public RRRingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public RRRingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView(){
        LinearInterpolator lir = new LinearInterpolator();
        final Resources resources = context.getResources();
        for(int i=0;i<N;i++) {
            final int index = i;
            ImageView imageView = new ImageView(context);
            InputStream is = resources.openRawResource(ring_id[i]);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 1;
            Bitmap btp = BitmapFactory.decodeStream(is, null, options);
            imageView.setImageBitmap(btp);
            imageView.setAlpha(0.0f);
            bitmapList.add(btp);
            addView(imageView);

            final ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,359f).setDuration(2000);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(-1);
            animator.setInterpolator(lir);
            //第一个圈的旋转动画要适配多个状态监测器
            if(i==0){
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                    if(animation.getAnimatedFraction()>0.98){
                        //响应停止转动
                        if(isCallStop[index]) {
                            animator.cancel();
                            isCallStop[index] = false;
                        }
                        //响应开始清扫
                        if(isCallClean){
                            respondCleaning();
                            isCallClean = false;
                        }
                        if(isCallCharge){
                            respondCharging();
                            isCallCharge = false;
                        }
                    }
                    }
                });
            }else {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                    if (isCallStop[index] && animation.getAnimatedFraction() > 0.98) {
                        isCallStop[index] = false;
                        animator.cancel();
                    }
                    }
                });
            }
            rotateList.add(animator);

            //显示动画
            ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView,"alpha",0.0f,1-0.075f*i).setDuration(250);
            alpha.setStartDelay(150*i);
            alphaList_show.add(alpha);
            //消失动画
            //渐渐消失的动画,消失之后将旋转动画停止
            ObjectAnimator alpha_miss = ObjectAnimator.ofFloat(imageView,"alpha",1-0.055f*i,0.0f).setDuration(250);
            alpha_miss.setStartDelay(150*(9-i));
            alpha_miss.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animator.cancel();
                }
            });
            alphaList_miss.add(alpha_miss);
        }
    }

    /**
     * 状态加载时View的显示效果
     */
    public void loadingState() {
        //判断当前状态，如果处于当前状态就操作
        if(state== RingState.loadState){
            return;
        }
        state = RingState.loadState;
        //如果第一个圈未显示，则开始显示第一个圈
        if (!isShow[0]) {
            if (!alphaList_show.get(0).isRunning()) {
                alphaList_show.get(0).start();
                isShow[0] = true;
            }
        }
        //如第一个圈未开始旋转，则开始第一个圈的旋转
        rotateList.get(0).setDuration(2000);
        if (!rotateList.get(0).isStarted()) {
            rotateList.get(0).start();
            isCallStop[0] = false;
        }
        //其他圈显示与否不用管，只要暂停即可
        for (int i = 1; i < N; i++) {
            if(isShow[i]){
                isShow[i]=false;
                alphaList_miss.get(i).start();
            }
        }
    }

    /**
     * 充电中时View的显示效果
     */
    public void chargeState() {
        //判断当前状态，如果处于当前状态就操作
//        rotateList.get(0).setDuration(4000);
        if(state== RingState.charging){
            return;
        }
        state = RingState.charging;
        if(rotateList.get(0).isStarted()){
            isCallCharge = true;
        }else {
            respondCharging();
        }

    }
    private void respondCharging(){
        //如果第一个圈未显示，则开始显示第一个圈
        if (!isShow[0]) {
            if (!alphaList_wave.get(0).isRunning()) {
                alphaList_wave.get(0).start();
                isShow[0] = true;
            }
        }
        if(rotateList.get(0).isStarted()){
            isCallStop[0] = true;
        }
        //其他圈停止转动，消失，开始下一轮闪动
        for (int i = 1; i < N; i++) {
            getChildAt(i).setAlpha(0.0f);
            if(rotateList.get(i).isStarted()) {
                rotateList.get(i).cancel();
            }
            alphaList_show.get(i).start();
        }

    }
    /**
     * 开始清扫时View的显示效果
     */
    public void cleaningState(){
        //判断当前状态，如果处于当前状态就操作
        if(state== RingState.cleaning){
            return;
        }
        state = RingState.cleaning;
        for(int i=0;i<N;i++){
            isCallStop[i]=false;
        }
        if(rotateList.get(0).isStarted()){
            isCallClean = true;
        }else{
            respondCleaning();
        }
    }
    private void respondCleaning(){
        rotateList.get(0).setDuration(2000);
        for(int i=0;i<N;i++){
            if(!isShow[i]){
                isShow[i]=true;
                alphaList_show.get(i).start();
            }
            rotateList.get(i).start();
        }
    }
    public void speedUp(){
        rotateList.get(0).setDuration(500);
    }
    public void recover(){
        rotateList.get(0).setDuration(1300);
    }
    public void destroy(){
        for(Bitmap bitmap : bitmapList){
            if(bitmap!=null){
                bitmap.recycle();
            }
        }
    }


}
