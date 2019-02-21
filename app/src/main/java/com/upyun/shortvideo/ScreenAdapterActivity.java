package com.upyun.shortvideo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import org.lasque.tusdk.impl.TuSpecialScreenHelper;
import com.upyun.shortvideo.utils.AppManager;

/**
 * @author xujie
 * @Date 2018/10/29
 */

public class ScreenAdapterActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppManager.getInstance().addActivity(this); //添加到栈中
        if(TuSpecialScreenHelper.isNotchScreen())
        {
            setTheme(android.R.style.Theme_NoTitleBar);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this); //从栈中移除
    }
}
