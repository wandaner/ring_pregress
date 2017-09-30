package com.wxh.rrmainview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TestView testView;
    private Button button_1;
    private Button button_2;
    private Button button_3;
    private Button button_4;
    private Button button_5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testView = (TestView) findViewById(R.id.rr_view);


        button_1 = (Button) findViewById(R.id.bt_1);
        button_2 = (Button) findViewById(R.id.bt_2);
        button_3 = (Button) findViewById(R.id.bt_3);
        button_4 = (Button) findViewById(R.id.bt_4);
        button_5 = (Button) findViewById(R.id.bt_5);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_1:
                testView.show();
                break;
            case R.id.bt_2:
                testView.miss();
                break;
            case R.id.bt_3:
                testView.run();
                break;
            case R.id.bt_4:
                testView.stopRRView();
                break;
            case R.id.bt_5:
                testView.stopRRCenterView();
                break;
        }
    }
}
