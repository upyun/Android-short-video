package com.upyun.shortvideo.api;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.upyun.shortvideo.views.CompoundConfigView;
import com.upyun.shortvideo.views.ConfigViewParams;
import com.upyun.shortvideo.views.ConfigViewSeekBar;

import org.lasque.tusdk.api.audio.player.TuSDKMutiAudioPlayer;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioEntry;
import org.lasque.tusdk.core.TuSdkContext;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 多音轨播放器
 *
 * 支持多个音频播放
 *
 * @author zuojindong
 */
public class MutiAudioPlayerActivity extends Activity {

    private final static int[] AUIDOENTRY_RESIDARRAY = new int[]{R.raw.lsq_audio_oldmovie, R.raw.lsq_audio_relieve, R.raw.lsq_audio_lively};
    /** 多音轨播放器 */
    private TuSDKMutiAudioPlayer mMutiAudioPlayer;
    /** 音频数据 */
    private List<TuSDKAudioEntry> mAudioEntryList = new ArrayList<TuSDKAudioEntry>();

    /** 返回按钮 */
    private TextView mBackBtn;

    /** 音频音量调节栏 */
    private CompoundConfigView mVoiceConfigView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muti_audio_player);

        initView();
        initVoiceConfigView();
        prepareAudio();
    }

    /**
     * 初始化视图
     */
    private void initView()
    {
        mBackBtn = (TextView) findViewById(R.id.lsq_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        TextView titleView = (TextView) findViewById(R.id.lsq_title);
        titleView.setText(TuSdkContext.getString("lsq_muti_audio_player"));
        TextView nextBtn = (TextView) findViewById(R.id.lsq_next);
        nextBtn.setVisibility(View.GONE);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.lsq_back:
                    finish();
                    break;
            }
        }
    };


    /**
     * 初始化音频音量调节栏
     */
    private void initVoiceConfigView()
    {
        if(getVoiceConfigView() != null)
        {
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(TuSdkContext.getString("dubbingone"), 1.0f);
            params.appendFloatArg(TuSdkContext.getString("dubbingtwo"), 1.0f);
            params.appendFloatArg(TuSdkContext.getString("dubbingthree"), 1.0f);
            getVoiceConfigView().setSeekBarHeight(TuSdkContext.dip2px(50));
            getVoiceConfigView().setSeekBarTitleWidh(TuSdkContext.dip2px(50));
            getVoiceConfigView().setDelegate(mVoiceConfigSeekbarDelegate);
            getVoiceConfigView().setCompoundConfigView(params);

            for(int i = 0; i < getVoiceConfigView().getSeekBarList().size(); i++ )
            {
                // 初始化音量调节SeeKBar
                this.setSeekBarProgress(i, 0.5f);
            }
        }
    }

    /**
     * 音频调节栏委托事件
     */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mVoiceConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate()
    {

        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg)
        {
            if (arg.getKey().equals("dubbingone"))
                setSeekBarProgress(0,arg.getPercentValue());
            else if (arg.getKey().equals("dubbingtwo"))
                setSeekBarProgress(1,arg.getPercentValue());
            else if (arg.getKey().equals("dubbingthree"))
                setSeekBarProgress(2,arg.getPercentValue());
        }
    };

    private void setSeekBarProgress(int index, float progress)
    {
        getVoiceConfigView().getSeekBarList().get(index).setProgress(progress);
        // 设置音频音量
        getAudioEntryList().get(index).setVolume(progress);
    }

    private CompoundConfigView getVoiceConfigView()
    {
        if (mVoiceConfigView == null)
        {
            mVoiceConfigView = (CompoundConfigView) findViewById(R.id.lsq_voice_volume_config_view);
        }

        return mVoiceConfigView;
    }

    /**
     * 准备音频播放器
     */
    private void prepareAudio()
    {
        if (mMutiAudioPlayer == null) initMutiAudioPlayer();
        mMutiAudioPlayer.setLooping(true);
        mMutiAudioPlayer.asyncPrepare(getAudioEntryList());
    }

    /**
     * 初始化多音轨播放器
     */
    private void initMutiAudioPlayer()
    {
        mMutiAudioPlayer = new TuSDKMutiAudioPlayer();
        mMutiAudioPlayer.setDelegate(mMutiAudioPlayerDelegate);
    }

    /**
     * 多音轨混合播放器Delegate
     */
    private TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate mMutiAudioPlayerDelegate = new TuSDKMutiAudioPlayer.TuSDKMutiAudioPlayerDelegate()
    {
        /**
         * 播放器状态改变事件
         */
        @Override
        public void onStateChanged(TuSDKMutiAudioPlayer.State state)
        {
            if (state == TuSDKMutiAudioPlayer.State.PrePared)
            {
                startMutiAudioPlayer();
            }

        }
    };

    /**
     * 开始播放音频数据
     */
    private void startMutiAudioPlayer()
    {
        if (mMutiAudioPlayer == null) return;
        mMutiAudioPlayer.start();
    }

    /**
     * 停止音频播放器
     */
    private void stopMutiAudioPlayer()
    {
        if (mMutiAudioPlayer == null) return;
        mMutiAudioPlayer.stop();
    }

    /**
     * 准备音频数据
     */
    private List<TuSDKAudioEntry> getAudioEntryList()
    {
        if (mAudioEntryList != null && mAudioEntryList.size() > 0) return mAudioEntryList;

        mAudioEntryList = new ArrayList<TuSDKAudioEntry>();

        for (int i = 0; i < AUIDOENTRY_RESIDARRAY.length; i++)
        {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + AUIDOENTRY_RESIDARRAY[i]);
            TuSDKAudioEntry audio = new TuSDKAudioEntry(uri);
            audio.setTrunk(false);
            mAudioEntryList.add(audio);
        }

        return mAudioEntryList;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startMutiAudioPlayer();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopMutiAudioPlayer();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopMutiAudioPlayer();
        mMutiAudioPlayer = null;
    }
}
