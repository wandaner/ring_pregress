package com.wxh.rrmainview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xukai on 2017/09/29.
 */

public class RRView extends View {

    private static final int N = 12;
    private final int Padding = 30;
    private final int NullAlpha = 0;
    private final int FullAlpha = 255;
    private final int MaxStage = 90;
    private final int MinStage = 0;
    private final int AlphaStep = 20;   //透明度间隔
    private final long TimeStep = 30;   //刷新时间间隔
    private final int DelayPerEdge = 7; //相邻的两个圆之间的显示间隔stage
    private int stage = 0;//主控参数
    private Timer timer;
    private TimerTask taskOfShow;
    private TimerTask taskOfMiss;

    private Bitmap[] bitmaps = new Bitmap[N];
    private Paint[] paints = new Paint[N];
    private Rect rect;
    private Resources resources;

    private boolean isRun = true;
    private boolean isRunCenter = true;
    public void setCenterView(RRCenterView centerView) {
        this.centerView = centerView;
    }

    private RRCenterView centerView;
    private static final int[] idList = {
            R.raw.rr_ring_2,R.raw.rr_ring_1,R.raw.rr_ring_3,
            R.raw.rr_ring_4,R.raw.rr_ring_5,R.raw.rr_ring_6,
            R.raw.rr_ring_7,R.raw.rr_ring_8,R.raw.rr_ring_9,
            R.raw.rr_ring_10,R.raw.rr_ring_11,R.raw.rr_ring_12
    };
    public RRView(Context context) {
        super(context);
        init(context);
    }

    public RRView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RRView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        resources = context.getResources();
        timer = new Timer();
        for(int i=0;i<N;i++){
            bitmaps[i] = getBitmapFromRaw(idList[i]);
            paints[i] = getPaint();
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(rect==null){
            rect = getRect();
        }
        for(int i=1;i<N;i++){
            canvas.drawBitmap(bitmaps[i],null,rect,paints[i]);
        }
    }
    private Rect getRect(){
        int width = getWidth();
        int height = getHeight();
        int size = width<height?width:height;
        return new Rect(Padding,Padding,size-Padding,size-Padding);
    }
    public void show(){
        if(taskOfMiss!=null) {
            taskOfMiss.cancel();
        }
        taskOfShow = new TimerTask() {
            @Override
            public void run() {
                if(stage++>MaxStage){
                    taskOfShow.cancel();
                    return;
                }
                for(int i=0;i<N;i++){
                    int alpha = AlphaStep*(stage-DelayPerEdge*i);
                    alpha = alpha<FullAlpha?alpha:FullAlpha;
                    alpha = alpha>NullAlpha?alpha:NullAlpha;
                    paints[i].setAlpha(alpha);
                }
                postInvalidateCenterView();
                postInvalidateRRView();
            }
        };
        timer.schedule(taskOfShow,0,TimeStep);
    }
    public void miss(){
        if(taskOfShow!=null) {
            taskOfShow.cancel();
        }
        taskOfMiss = new TimerTask() {
            @Override
            public void run() {
                if(stage--<MinStage){
                    taskOfMiss.cancel();
                    return;
                }
                for(int i=0;i<N;i++){
                    int alpha = AlphaStep*(stage-DelayPerEdge*i);
                    alpha = alpha<FullAlpha?alpha:FullAlpha;
                    alpha = alpha>NullAlpha?alpha:NullAlpha;
                    paints[i].setAlpha(alpha);
                }
                postInvalidateCenterView();
                postInvalidateRRView();
            }
        };
        timer.schedule(taskOfMiss,0,TimeStep);
    }

    public void postInvalidateRRView(){
        if(isRun){
            postInvalidate();
        }
    }
    public void postInvalidateCenterView() {
        if(centerView!=null&&isRunCenter){
            centerView.draw(paints[0]);
        }
    }
    private Paint getPaint(){
        Paint paint = new Paint();
        paint.setAlpha(0);
        return paint;
    }
    private Bitmap getBitmapFromRaw(int id){
        InputStream in =  resources.openRawResource(id);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        return bitmap;
    }

    /**
     * 一般销毁的时候会调用一次
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(taskOfMiss!=null){
            taskOfMiss.cancel();
        }
        if(taskOfShow!=null){
            taskOfShow.cancel();
        }
        if(timer!=null){
            timer.cancel();
        }
        for(Bitmap bitmap:bitmaps){
            if(bitmap!=null){
                bitmap.recycle();
            }
        }
    }
}
