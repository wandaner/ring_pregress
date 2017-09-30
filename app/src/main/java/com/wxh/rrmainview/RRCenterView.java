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

/**
 * Created by xukai on 2017/09/29.
 */

public class RRCenterView extends View {
    private final int Padding = 30;
    private Bitmap bitmap;
    private Paint paint;
    private Rect rect;
    private Resources resources;
    private static final int id = R.raw.rr_ring_2;
    public RRCenterView(Context context) {
        super(context);
        init(context);
    }

    public RRCenterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RRCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        resources = context.getResources();
        bitmap = getBitmapFromRaw(id);
        paint = getPaint();
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
        canvas.drawBitmap(bitmap,null,rect,paint);
    }
    public void draw(Paint p){
        paint = p;
        postInvalidate();
    }
    private Rect getRect(){
        int width = getWidth();
        int height = getHeight();
        int size = width<height?width:height;
        return new Rect(Padding,Padding,size-Padding,size-Padding);
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
        if(bitmap!=null){
            bitmap.recycle();
        }
    }

}
