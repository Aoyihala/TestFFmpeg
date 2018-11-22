package com.grandbroad.testffmpeg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.grandbroad.testffmpeg.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SSettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_rtmp)
    EditText editRtmp;
    @BindView(R.id.btn_360p)
    RadioButton btn360p;
    @BindView(R.id.btn_480p)
    RadioButton btn480p;
    @BindView(R.id.btn_720p)
    RadioButton btn720p;
    @BindView(R.id.radio_screen)
    RadioGroup radioScreen;
    @BindView(R.id.btn_low)
    RadioButton btnLow;
    @BindView(R.id.btn_medial)
    RadioButton btnMedial;
    @BindView(R.id.btn_high)
    RadioButton btnHigh;
    @BindView(R.id.radio_video)
    RadioGroup radioVideo;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.edit_fps)
    EditText editFps;
    @BindView(R.id.edit_speed)
    EditText editSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("设置推流参数");
        initsession();
    }

    /**
     * 初始化事件
     */
    private void initsession() {
        /**
         * //大部分手机必有的分辨率
         * 1080p：1920*1080
         * 720p：1280*720
         * 480p：640*480
         * 360p: 480*360
         */
        radioScreen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.btn_360p:
                        SpUtils.saveScreenSize("640-360",getApplicationContext());
                        break;
                    case R.id.btn_480p:
                        SpUtils.saveScreenSize("640-480",getApplicationContext());
                        break;
                    case R.id.btn_720p:
                        SpUtils.saveScreenSize("1280-720",getApplicationContext());
                        break;
                }
            }
        });
        //保存
        radioVideo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.btn_low:
                        SpUtils.setQultey(0,getApplicationContext());
                        break;
                    case R.id.btn_medial:
                        SpUtils.setQultey(1,getApplicationContext());
                        break;
                    case R.id.btn_high:
                        SpUtils.setQultey(2,getApplicationContext());
                        break;

                }
            }
        });
        //完成
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存帧率
                String fps = editFps.getText().toString();
                if (TextUtils.isEmpty(fps))
                {
                    SpUtils.saveFps(30,getApplicationContext());
                }
                else
                {
                    SpUtils.saveFps(Integer.parseInt(fps),getApplicationContext());
                }
                String netspeed =editSpeed.getText().toString();
                if (TextUtils.isEmpty(netspeed))
                {
                    SpUtils.saveSpeed(800,getApplicationContext());
                }
                else
                {
                    SpUtils.saveSpeed(Integer.parseInt(netspeed),getApplicationContext());
                }
                String rtmp = editRtmp.getText().toString();
                if (TextUtils.isEmpty(rtmp))
                {
                    SpUtils.saveUrl(editRtmp.getHint().toString(),getApplicationContext());
                }
                else
                {
                    if (editRtmp.getHint().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"至少填入地址",Toast.LENGTH_LONG).show();
                        return;
                    }
                    SpUtils.saveUrl(rtmp,getApplicationContext());
                }
                startActivity(new Intent(SSettingActivity.this,MainActivity2.class));
                finish();
            }
        });

    }

}
