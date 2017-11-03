package com.wxh.rrmainview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private RRBatteryView proView;
    private SeekBar seekBar;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private Button button_4;
    private Button button_5;
    private Button button_6;
    private TextView tv_1;
    private TextView tv_2;
    private ImageView imageView;

    private RRRings RRRings;

    private HomeItemZoom h1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proView = (RRBatteryView) findViewById(R.id.proView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        proView.setCurrentCount(0);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                proView.setCurrentCount(progress/100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button_1 = (Button) findViewById(R.id.bt_1);
        button_2 = (Button) findViewById(R.id.bt_2);
        button_3 = (Button) findViewById(R.id.bt_3);
        button_4 = (Button) findViewById(R.id.bt_4);
        button_5 = (Button) findViewById(R.id.bt_5);
        button_6 = (Button) findViewById(R.id.bt_6);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        h1 = (HomeItemZoom) findViewById(R.id.h1);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        h1.setOnClickListener(this);
        h1.setMode(HomeItemZoom.Mode.middleBottom);
        RRRings = (RRRings) findViewById(R.id.testView);
        InputStream is = getResources().openRawResource(R.raw.battery);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 1;
        Bitmap btp = BitmapFactory.decodeStream(is, null, options);
        imageView = (ImageView) findViewById(R.id.battery_view);
        imageView.setImageBitmap(btp);
        LinearInterpolator lir = new LinearInterpolator();
        final ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,-359f).setDuration(4000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(-1);
        animator.setInterpolator(lir);
        animator.start();
//        Typeface typeface=Typeface.createFromAsset(getAssets(),
//                "fonts/iconfont.ttf");
//        tv_1.setTypeface(typeface);
//        tv_1.setText("\ue624");

    }
    private boolean a = true;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                RRRings.run();
                RRRings.show();
//                tv_1.setSelected(false);
                break;
            case R.id.bt_2:
                RRRings.miss();
//                tv_1.setSelected(true);
                break;
            case R.id.bt_3:
                RRRings.setMode(Particle.Mode.slient);
//                tv_2.setSelected(false);
                break;
            case R.id.bt_4:
                RRRings.setMode(Particle.Mode.standard);
//                tv_2.setSelected(true);
                break;
            case R.id.bt_5:
                RRRings.setMode(Particle.Mode.powerFull);
//                testView.stopRRCenterView();
                break;
            case R.id.bt_6:
                RRRings.setMode(Particle.Mode.fullSpeed);
//                testView.stopRRCenterView();
                break;
            case R.id.h1:
                if(a) {
                    h1.switchToNormal();
                }else {
                    h1.switchToClick();
                }
                a = !a;
                break;
        }
    }
}
