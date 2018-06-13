package com.upyun.shortvideo.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.upyun.shortvideo.R;

import org.lasque.tusdk.core.utils.TuSdkWaterMarkOption;

import java.util.ArrayList;
import java.util.List;

public class MyRadioView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {

    TuSdkWaterMarkOption.WaterMarkPosition position;

    private List<RadioButton> list = new ArrayList<>();
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rb5;

    private CompoundButton buttonView;

    public MyRadioView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public MyRadioView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MyRadioView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.my_select_view, this);

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);
        rb5 = (RadioButton) findViewById(R.id.rb5);

        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb4.setOnCheckedChangeListener(this);
        rb5.setOnCheckedChangeListener(this);

        list.add(rb1);
        list.add(rb2);
        list.add(rb3);
        list.add(rb4);
        list.add(rb5);

        rb1.setChecked(true);
    }


    private void clearCheck(CompoundButton buttonView) {
        for (RadioButton radioButton : list) {
            if (!radioButton.equals(buttonView)) {
                radioButton.setChecked(false);
            }
        }
        this.buttonView = buttonView;
    }

    public TuSdkWaterMarkOption.WaterMarkPosition getPosition() {

        if (buttonView != null) {
            switch (buttonView.getId()) {
                case R.id.rb1:
                    return TuSdkWaterMarkOption.WaterMarkPosition.TopLeft;
                case R.id.rb2:
                    return TuSdkWaterMarkOption.WaterMarkPosition.BottomLeft;
                case R.id.rb3:
                    return TuSdkWaterMarkOption.WaterMarkPosition.Center;
                case R.id.rb4:
                    return TuSdkWaterMarkOption.WaterMarkPosition.TopRight;
                case R.id.rb5:
                    return TuSdkWaterMarkOption.WaterMarkPosition.BottomRight;

            }
        }
        return null;
    }

    public void setPosition(TuSdkWaterMarkOption.WaterMarkPosition position) {
        switch (position){
            case TopLeft:
                rb1.setChecked(true);
                break;
            case BottomLeft:
                rb2.setChecked(true);
                break;
            case Center:
                rb3.setChecked(true);
                break;
            case TopRight:
                rb4.setChecked(true);
                break;
            case BottomRight:
                rb5.setChecked(true);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            clearCheck(buttonView);
        }
    }
}
