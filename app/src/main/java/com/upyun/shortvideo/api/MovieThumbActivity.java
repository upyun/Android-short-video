/**
 * TuSDKVideoDemo
 * MovieThumbActivity.java
 *
 * @author  LiuHang
 * @Date  Jul 12, 2017 14:52:11 PM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */

package com.upyun.shortvideo.api;

import java.util.List;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer.PlayerState;
import org.lasque.tusdk.api.movie.player.TuSDKMoviePlayer.TuSDKMoviePlayerDelegate;
import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 获取视频缩略图
 * 
 * @author LiuHang
 *
 */
public class MovieThumbActivity extends Activity
{
	/** 返回按钮 */
	private TextView mBackBtn;
	
	/** 视频播放器 */
	private TuSDKMoviePlayer mMoviePlayer;
	
	private GridView mThumbList;
	
	/** 加载缩略图按钮 */
	private Button mLoadThumbButton;
	
	/** 上一次播放的位置 */
	private int mLastPlayPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(com.upyun.shortvideo.R.layout.movie_thumb_activity);
		initView();
	}
	
	private void initView()
	{
		mBackBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_back);
		mBackBtn.setOnClickListener(mOnClickListener);
		TextView titleView = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_title);
		titleView.setText(TuSdkContext.getString("lsq_movie_thumb"));
		TextView nextBtn = (TextView) findViewById(com.upyun.shortvideo.R.id.lsq_next);
		nextBtn.setVisibility(View.GONE);
		SurfaceView preview = (SurfaceView) findViewById(com.upyun.shortvideo.R.id.lsq_preview);
		iniMoviePlayer(preview);
		mLoadThumbButton = (Button) findViewById(com.upyun.shortvideo.R.id.lsq_load_thumb_btn);
		mLoadThumbButton.setOnClickListener(mOnClickListener);
		mThumbList = (GridView) findViewById(com.upyun.shortvideo.R.id.lsq_movie_thumb_list);
		mThumbList.setNumColumns(3);
		mThumbList.setColumnWidth(TuSdkContext.dip2px(56));
		mThumbList.setHorizontalSpacing(TuSdkContext.dip2px(5));
		mThumbList.setVerticalSpacing(TuSdkContext.dip2px(20));
	}

	private TuSDKMoviePlayerDelegate mMoviePlayerDelegate = new TuSDKMoviePlayerDelegate()
	{
		@Override
		public void onStateChanged(PlayerState state) 
		{
			if (state == PlayerState.INITIALIZED)
			{
				mMoviePlayer.seekTo(mLastPlayPosition);
			}
		}

		@Override
		public void onVideSizeChanged(MediaPlayer mp,int width, int height)
		{
		}

		@Override
		public void onProgress(int progress)
		{
		}

		@Override
		public void onSeekComplete()
		{
		}

		@Override
		public void onCompletion() 
		{
		}
	};
	
	protected void iniMoviePlayer(SurfaceView surfaceView)
	{
		mMoviePlayer = TuSDKMoviePlayer.createMoviePlayer();
		mMoviePlayer.setLooping(true);
        
        mMoviePlayer.initVideoPlayer(this, getVideoPath(), surfaceView);
        mMoviePlayer.setDelegate(mMoviePlayerDelegate);
	}
	
	/** 加载视频缩略图 */
	public void loadVideoThumbList(String videoPath)
	{
		TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),TuSdkContext.dip2px(30));
		
		TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();
		
		extractor.setOutputImageSize(tuSdkSize)
				.setVideoDataSource(TuSDKMediaDataSource.create(getVideoPath()))
				.setExtractFrameCount(6);
		
		extractor.asyncExtractImageList(new TuSDKVideoImageExtractorDelegate() 
		{
			@Override   
			public void onVideoImageListDidLoaded(List<Bitmap> images) 
			{
				mThumbList.setAdapter(new MovieThumbAdapter(MovieThumbActivity.this, images));
				String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_refresh_list_view_state_hidden);
				TuSdk.messageHub().showToast(MovieThumbActivity.this, hintMsg);
			}
			
			@Override
			public void onVideoNewImageLoaded(Bitmap bitmap)
			{
			}
		});	
	}
	
	private Uri getVideoPath()
	{
		Uri videoPathUri = Uri.parse("android.resource://" + getPackageName() + "/" + com.upyun.shortvideo.R.raw.tusdk_sample_video);
		return videoPathUri;
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if (v == mBackBtn)
			{
				finish();
			}
			else if (v == mLoadThumbButton)
			{
				if (mThumbList.getAdapter()!= null) return;
				
				loadVideoThumbList(getVideoPath().toString());
				String hintMsg = getResources().getString(com.upyun.shortvideo.R.string.lsq_movie_thumb_loading);
				TuSdk.messageHub().setStatus(MovieThumbActivity.this, hintMsg);
			}
		}
	};
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if (mMoviePlayer != null)
			mMoviePlayer.start();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
		mMoviePlayer.pause();
		mLastPlayPosition = mMoviePlayer.getCurrentPosition();
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mMoviePlayer.destory();
		mMoviePlayer = null;
	}
	
	/** 自定义适配器 */
    public static class MovieThumbAdapter extends BaseAdapter
    {
        private  Context context;
        
        private List<Bitmap> mImageList;
        
        public MovieThumbAdapter(Context context,List<Bitmap> imageList)
        {
            this.context = context;
            this.mImageList = imageList;
        }
        
        @Override
        public int getCount()
        {
            return mImageList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mImageList.get(position);
        }

        @Override
        public long getItemId(int position) 
        {
            return position;
        }

        //给每一个item填充图片
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView = new ImageView(context);

            //每一张图片
            imageView.setImageBitmap(mImageList.get(position));

            return imageView;
        }
    }
}
