/**
 * TuSDKVideoDemo 
 * MovieRecordView.java
 * 
 * @author Bonan
 * @Date: 2017-5-8 上午10:42:48
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package com.upyun.shortvideo.views.record;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;

/**
 * 断点续拍全屏录制视图
 */
public class MovieRecordFullScreenView extends MovieRecordView
{
	/** 不透明度为1 */
	private final int MAX_ALPHA = 255;

	/** 透明度比例 */
	private final float ALPHA_RATIO = 0.4f;

	/** 视频速度模式视图 */
	private ViewGroup mSpeedModeBar;

	public MovieRecordFullScreenView(Context context)
	{
		super(context);
	}

	public MovieRecordFullScreenView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected int getLayoutId()
	{
		return com.upyun.shortvideo.R.layout.movie_record_full_screen_view;
	}

	@Override
	protected void init(Context context)
	{
		super.init(context);

		updateButtonStatus(mRollBackButton, false);
		updateButtonStatus(mConfirmButton, false);

		// 速度控制条
		mSpeedModeBar = findViewById(com.upyun.shortvideo.R.id.lsq_movie_speed_bar);

		int childCount = mSpeedModeBar.getChildCount();

		for (int i = 0;i<childCount;i++)
		{
			mSpeedModeBar.getChildAt(i).setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					selectSpeedMode(Integer.parseInt((String)view.getTag()));
				}
			});
		}
	}

	/**
	 * 切换速率
	 *
	 * @param selectedSpeedMode
	 */
	private void selectSpeedMode(int selectedSpeedMode)
	{
		int childCount = mSpeedModeBar.getChildCount();

		for (int i = 0;i < childCount;i++)
		{
			Button btn = (Button) mSpeedModeBar.getChildAt(i);
			int speedMode = Integer.parseInt((String)btn.getTag());

			if (selectedSpeedMode == speedMode)
			{
				btn.setBackgroundColor(Color.parseColor("#b3f4a11a"));
				btn.setTextColor(Color.WHITE);

			}else
			{
				btn.setBackgroundColor(Color.TRANSPARENT);
				btn.setTextColor(Color.parseColor("#b3f4a11a"));
			}
		}

		// 切换相机速率
		TuSDKRecordVideoCamera.SpeedMode speedMode = TuSDKRecordVideoCamera.SpeedMode.values()[selectedSpeedMode];
		mCamera.setSpeedMode(speedMode);
	}

	/**
	 * 按下录制按钮
	 */
	@Override
	protected void onPressRecordButton()
	{
		super.onPressRecordButton();

		if (mSpeedModeBar != null)
			mSpeedModeBar.setVisibility(GONE);

	}

	/**
	 * 释放录制按钮
	 */
	@Override
	protected void onReleaseRecordButton()
	{
		super.onReleaseRecordButton();
		if (mSpeedModeBar != null)
			mSpeedModeBar.setVisibility(VISIBLE);
	}

	/** ----------------------- 修改按钮的透明度 ------------------------------------------------**/

	@Override
	protected void updateButtonStyle(TuSdkTextButton button, int imgId, int colorId, boolean clickable)
	{
		Drawable drawable = TuSdkContext.getDrawable(imgId);
		switch (button.getId())
		{
			case com.upyun.shortvideo.R.id.lsq_confirmWrap:
			case com.upyun.shortvideo.R.id.lsq_backWrap:
				if (!clickable)
					drawable.setAlpha((int) (MAX_ALPHA * ALPHA_RATIO));
				else
					drawable.setAlpha(MAX_ALPHA);
				break;
			default:
				break;
		}

		button.setCompoundDrawables(null, drawable, null, null);
		button.setTextColor(TuSdkContext.getColor(colorId));
	}

	/** ----------------------- 替换资源文件 ------------------------------------------------**/

	@Override
	protected int getFilterSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_filter_full;
	}

	@Override
	protected int getFilterUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_filter_full;
	}

	@Override
	protected int getStickerSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_sticker_full;
	}

	@Override
	protected int getStickerUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_sticker_full;
	}

	@Override
	protected int getConfirmSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_finish_full;
	}

	@Override
	protected int getConfirmUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_finish_full;
	}

	@Override
	protected int getCancelSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_cancel_full;
	}

	@Override
	protected int getCancelUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_cancel_full;
	}

	@Override
	protected int getRecordSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_recording_full;
	}

	@Override
	protected int getRecordUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_record_pause_full;
	}

	@Override
	protected int getFlashSelectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_lamp_on_full;
	}

	@Override
	protected int getFlashUnselectedDrawable()
	{
		return com.upyun.shortvideo.R.drawable.lsq_lamp_off_full;
	}
}