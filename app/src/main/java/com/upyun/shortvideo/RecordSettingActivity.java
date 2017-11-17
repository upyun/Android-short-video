package com.upyun.shortvideo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.MyRadioView;
import com.upyun.shortvideo.views.SettingEditView;

public class RecordSettingActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "RecordSettingActivity";
    private MyRadioView myRadioView;
    private RadioGroup qxRadio;
    private SettingEditView sevMinSec;
    private SettingEditView sevMaxSec;
    private SettingEditView sevWidth;
    private SettingEditView sevHeight;
    private SettingEditView sevBit;
    private SettingEditView sevFps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_setting_layout);
        initView();
    }

    private void initView() {
        myRadioView = (MyRadioView) findViewById(R.id.mrv);
        qxRadio = (RadioGroup) findViewById(R.id.qx_rg);
        sevMinSec = (SettingEditView) findViewById(R.id.sev_minsec);
        sevMaxSec = (SettingEditView) findViewById(R.id.sev_maxsec);
        sevWidth = (SettingEditView) findViewById(R.id.sev_width);
        sevHeight = (SettingEditView) findViewById(R.id.sev_height);
        sevBit = (SettingEditView) findViewById(R.id.sev_bit);
        sevFps = (SettingEditView) findViewById(R.id.sev_fps);

        findViewById(R.id.lsq_back).setOnClickListener(this);
        findViewById(R.id.lsq_next).setOnClickListener(this);

        qxRadio.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.rb2)).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lsq_next:

                Constants.MAX_RECORDING_TIME = Integer.parseInt(sevMaxSec.getValue());
                Constants.MIN_RECORDING_TIME = Integer.parseInt(sevMinSec.getValue());

                Config.RECORDWIDTH = Integer.parseInt(sevWidth.getValue());
                Config.RECORHEIGHT = Integer.parseInt(sevHeight.getValue());
                Config.RECORBITRATE = Integer.parseInt(sevBit.getValue());
                Config.RECORDFPS = Integer.parseInt(sevFps.getValue());

                Config.POSITION = myRadioView.getPosition();
                Intent intent = new Intent(this, MovieRecordAndImportEditorActivity.class);
                this.startActivity(intent);
                break;

            case R.id.lsq_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb1:
                sevWidth.setValue(640);
                sevHeight.setValue(360);
                sevBit.setValue(384);
                sevFps.setValue(15);
                break;
            case R.id.rb2:
                sevWidth.setValue(854);
                sevHeight.setValue(480);
                sevBit.setValue(512);
                sevFps.setValue(20);
                break;
            case R.id.rb3:
                sevWidth.setValue(1280);
                sevHeight.setValue(720);
                sevBit.setValue(1152);
                sevFps.setValue(25);
                break;
            case R.id.rb4:
                sevWidth.setValue(1920);
                sevHeight.setValue(1080);
                sevBit.setValue(2560);
                sevFps.setValue(30);
                break;
        }
    }
}
