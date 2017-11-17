package com.upyun.shortvideo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upyun.shortvideo.R;


public class SettingEditView extends LinearLayout {

    private TextView key;

    public String getValue() {
        return value.getText().toString();
    }

    private EditText value;

    private TextView unit;

    public SettingEditView(Context context) {
        super(context);
        initView(context, null);
    }

    public SettingEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SettingEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingEditView);
        String keyText = typedArray.getString(R.styleable.SettingEditView_keyText);
        String valueText = typedArray.getString(R.styleable.SettingEditView_valueText);
        String unitText = typedArray.getString(R.styleable.SettingEditView_unitText);

        LayoutInflater.from(context).inflate(R.layout.setting_edit_view, this);
        key = (TextView) findViewById(R.id.key_text);
        value = (EditText) findViewById(R.id.value_text);
        unit = (TextView) findViewById(R.id.unit_text);
        key.setText(keyText);
        value.setText(valueText);
        unit.setText(unitText);
    }

    public void setValue(int value) {
        this.value.setText(value + "");
    }
}
