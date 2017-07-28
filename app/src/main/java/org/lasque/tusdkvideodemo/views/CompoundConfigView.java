/**
 * TuSDKVideoDemo
 * CompoundConfigView.java
 *
 * @author  LiuHang
 * @Date  Jul 4, 2017 10:43:14 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.views;

import java.util.ArrayList;

import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdkvideodemo.views.ConfigViewParams.ConfigViewArg;
import org.lasque.tusdkvideodemo.views.ConfigViewSeekBar.ConfigSeekbarDelegate;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 可以生成多个调节栏
 * 
 * @author LiuHang
 *
 */
public class CompoundConfigView extends TuSdkRelativeLayout
{
	/** 调节栏的高度 */
	private int mSeekBarHeigth;
	
	/** 配置包装 */
	private LinearLayout mConfigWrap;
	
    /**
     * 拖动栏列表
     */
    private ArrayList<ConfigViewSeekBar> mSeekbars;
    
    /** 调节栏委托事件 */
    private ConfigSeekbarDelegate mDelegate;
    
    /** SeekBar标题框的宽度 */
    private int mSeekBarTitleWidth;
    
	public CompoundConfigView(Context context) 
	{
		super(context);
	}
	
    public CompoundConfigView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompoundConfigView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
	
    public void setDelegate(ConfigSeekbarDelegate delegate)
    {
		this.mDelegate = delegate;
	}
    
    /**
     * 设置调节栏参数
     *
     * @param params
     */
    public void setCompoundConfigView(ConfigViewParams params)
    {
        if (params == null)
        {
            return;
        }

        this.showViewIn(true);
               
        this.resetConfigView(this.getConfigWrap(),params);
    }

	/**
	 * 配置包装
	 * 
	 * @return
	 */
	public LinearLayout getConfigWrap()
	{
		if (mConfigWrap == null)
		{
			mConfigWrap = this.getViewById("lsq_configWrap");
		}
		return mConfigWrap;
	}
	
    public void setSeekBarHeight(int seekBarHeight)
    {
    	this.mSeekBarHeigth = seekBarHeight;
    }
    
	 /**
     * 配置视图
     *
     * @param configWrap
     * @param params
     */
    private void resetConfigView(LinearLayout configWrap, ConfigViewParams params)
    {
        if (configWrap == null || params == null || params.size() == 0) return;

        // 删除所有视图
        configWrap.removeAllViews();

        mSeekbars = new ArrayList<ConfigViewSeekBar>(params.size());

        for (ConfigViewArg arg : params.getArgs())
        {
        	ConfigViewSeekBar seekbar = this.buildAppendSeekbar(configWrap, this.mSeekBarHeigth);
        	
        	if (seekbar != null)
            {
                // 设置调节栏配置参数
        		updateSeeBarTitleWidth(seekbar);
                seekbar.setConfigViewArg(arg);
                seekbar.setDelegate(mDelegate);
                mSeekbars.add(seekbar);
            }
        }
    }

    public void  setSeekBarTitleWidh(int width) 
    {
		this.mSeekBarTitleWidth = width;
	}
    
    private void updateSeeBarTitleWidth(ConfigViewSeekBar seekBar)
    {
    	if (this.mSeekBarTitleWidth == 0) return;
    	seekBar.getTitleView().getLayoutParams().width = mSeekBarTitleWidth;
    }
    
    public ArrayList<ConfigViewSeekBar> getSeekBarList()
    {
    	return mSeekbars;
    }
    
	/**
	 * 创建并添加拖动栏
	 * 
	 * @param parent
	 *            父视图
	 * @return
	 */
	public ConfigViewSeekBar buildAppendSeekbar(LinearLayout parent, int height)
	{
		if (parent == null) return null;

		ConfigViewSeekBar seekbar = TuSdkViewHelper.buildView(this.getContext(), ConfigViewSeekBar.getLayoutId(), parent);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
		parent.addView(seekbar, params);
		return seekbar;
	}
}
