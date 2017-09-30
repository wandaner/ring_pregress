package com.wxh.rrmainview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by xukai on 2017/09/30.
 */

public class RRBatteryView extends View {
    public RRBatteryView(Context context) {
        super(context);
    }

    public RRBatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RRBatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintPercentBack(canvas);
    }

    /**画两条线的底色*/
    private void paintPercentBack(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.blank));
        paint.setStrokeWidth(20);//outerArcWidth
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角
        paint.setAntiAlias(true);
        //绘制最外层圆条底色
        int cen_x = getWidth()/2;
        int cen_y = getHeight()/2;
        int outerArcRadius=380;
        int insideArcRadius = 360;
        float floatAngel = 90.0f;
        RectF outerArea= new RectF(cen_x - outerArcRadius, cen_y - outerArcRadius, cen_x  + outerArcRadius, cen_y + outerArcRadius);
        canvas.drawArc(outerArea,0,360, false, paint);
//        绘制里层大宽度弧形
        paint.setColor(getResources().getColor(R.color.blank));
        paint.setStrokeWidth(21);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(cen_x - insideArcRadius, cen_y - insideArcRadius, cen_x + insideArcRadius, cen_y + insideArcRadius),
                (float) (0),
                (float) (360), false, paint);

    }
}
