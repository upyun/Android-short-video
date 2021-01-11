package org.lasque.tusdkvideodemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.upyun.shortvideo.R;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.video.TuSDKVideo;

/**
 * 启动页
 */
public class SplashActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView mVersion = findViewById(R.id.lsq_version);
        mVersion.setText(String.format("TuSDK Video %s", TuSDKVideo.VIDEO_VERSION));

        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {

//                String path = "/storage/emulated/0/sssssssssssss.mp4";
//                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sssssssssssss.mp4";
//                Intent intent = new Intent(SplashActivity.this, MovieEditorActivity.class);
//                intent.putExtra("isDirectEdit",false);
//                intent.putExtra("videoPath",path);

                startActivity(new Intent(SplashActivity.this,DemoEntryActivity.class));
//                startActivity(intent);
                overridePendingTransition(R.anim.lsq_fade_in,R.anim.lsq_fade_out);
                finish();
            }
        },2000);
    }

}
