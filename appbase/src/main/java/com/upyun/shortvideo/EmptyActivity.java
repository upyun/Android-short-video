package com.upyun.shortvideo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.seles.tusdk.FilterManager;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        TuProgressHub.setStatus(this, "正在初始化");
        TuSdk.checkFilterManager(mFilterManagerDelegate);
    }

    /**
     * 滤镜管理器委托
     */
    private FilterManager.FilterManagerDelegate mFilterManagerDelegate = new FilterManager.FilterManagerDelegate() {
        @Override
        public void onFilterManagerInited(FilterManager manager) {
            Toast.makeText(EmptyActivity.this, "初始化完成", Toast.LENGTH_LONG).show();
        }
    };
}
