/**
 * TuSDKLiveDemo
 * FilterConfigView.java
 *
 * @author 		Yanlin
 * @Date 		2016-4-15 上午10:36:28
 * @Copyright 	(c) 2016 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.views;

import java.util.ArrayList;

import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.SelesParameters.FilterParameterInterface;
import org.lasque.tusdk.core.seles.sources.SelesOutInput;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper.OnSafeClickListener;
import com.upyun.shortvideo.views.FilterConfigSeekbar.FilterConfigSeekbarDelegate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 滤镜配置视图
 *
 * @author Clear
 */
public class FilterConfigView extends TuSdkRelativeLayout
{

    /**
     * 滤镜配置视图委托
     *
     * @author Clear
     */
    public interface FilterConfigViewDelegate
    {
        /**
         * 通知重新绘制
         *
         * @param configView
         */
        void onFilterConfigRequestRender(FilterConfigView configView);
    }

    /**
     * 滤镜配置视图委托
     */
    private FilterConfigViewDelegate mDelegate;

    /**
     * 滤镜配置视图委托
     *
     * @return the mDelegate
     */
    public FilterConfigViewDelegate getDelegate()
    {
        return mDelegate;
    }

    /**
     * 滤镜配置视图委托
     *
     * @param mDelegate
     *            the mDelegate to set
     */
    public void setDelegate(FilterConfigViewDelegate mDelegate)
    {
        this.mDelegate = mDelegate;
    }

    public FilterConfigView(Context context)
    {
        super(context);
    }

    public FilterConfigView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FilterConfigView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    // 配置包装
    private LinearLayout mConfigWrap;

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

    @Override
    public void loadView()
    {
        super.loadView();
        // 拖动条高度
        this.mSeekHeigth = getMeasuredHeight();
    }

    /**
     * 设置滤镜
     *
     * @param filter
     */
    public void setSelesFilter(SelesOutInput filter)
    {
        if (filter == null || !(filter instanceof FilterParameterInterface))
        {
            return;
        }

        this.showViewIn(true);
        loadView();
        this.resetConfigView(this.getConfigWrap(), (FilterParameterInterface) filter);
    }

    /**
     * 滤镜对象
     */
    private FilterParameterInterface mFilter;

    /**
     * 滤镜配置拖动栏列表
     */
    private ArrayList<FilterConfigSeekbar> mSeekbars;

    /**
     * 拖动条高度
     */
    private int mSeekHeigth;

    /**
     * 配置视图
     *
     * @param configWrap
     * @param filter
     */
    private void resetConfigView(LinearLayout configWrap, FilterParameterInterface filter)
    {
        mFilter = filter;
        if (configWrap == null || mFilter == null) return;

        // 删除所有视图
        configWrap.removeAllViews();
        SelesParameters params = mFilter.getParameter();

        if (params == null || params.size() == 0)
        {
            return;
        }

        mSeekbars = new ArrayList<FilterConfigSeekbar>(params.size());

        for (FilterArg arg : params.getArgs())
        {
        	if (arg.equalsKey("smoothing")) continue;
        	
            FilterConfigSeekbar seekbar = this.buildAppendSeekbar(configWrap, this.mSeekHeigth);
            if (seekbar != null)
            {
                // 设置滤镜配置参数
                seekbar.setFilterArg(arg);
                seekbar.setDelegate(mFilterConfigSeekbarDelegate);
                mSeekbars.add(seekbar);
            }
        }
    }

    /**
     * 创建并添加滤镜配置拖动栏
     *
     * @param parent
     *            父视图
     * @return
     */
    public FilterConfigSeekbar buildAppendSeekbar(LinearLayout parent, int height)
    {
        if (parent == null) return null;

        FilterConfigSeekbar seekbar = TuSdkViewHelper.buildView(this.getContext(), FilterConfigSeekbar.getLayoutId(), parent);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
        parent.addView(seekbar, params);
        return seekbar;
    }

    /**
     * 滤镜配置拖动栏委托
     */
    protected FilterConfigSeekbarDelegate mFilterConfigSeekbarDelegate = new FilterConfigSeekbarDelegate()
    {
        /**
         * 配置数据改变
         *
         * @param seekbar
         *            滤镜配置拖动栏
         * @param arg
         *            滤镜参数
         */
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg)
        {
            requestRender();
            
            if (getSeekBarDelegate() != null)
            	getSeekBarDelegate().onSeekbarDataChanged(seekbar, arg);
        }
    };

    /**
     * 滤镜配置拖动栏状态委托
     *
     * @author Clear
     */
    public interface FilterConfigViewSeekBarDelegate
    {
        /**
         * 配置数据改变
         *
         * @param seekbar
         *            滤镜配置拖动栏
         * @param arg
         *            滤镜参数
         */
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg);

    }
    
    private FilterConfigViewSeekBarDelegate mSeekBarDelegate;
    
    public void setSeekBarDelegate(FilterConfigViewSeekBarDelegate seekBarDelegate)
    {
    	this.mSeekBarDelegate = seekBarDelegate;
    }
    
    public FilterConfigViewSeekBarDelegate getSeekBarDelegate()
    {
    	return this.mSeekBarDelegate;
    }
    
    /**
     * 按钮点击事件
     */
    protected OnSafeClickListener mOnClickListener = new OnSafeClickListener()
    {
        @Override
        public void onSafeClick(View v)
        {
        }
    };
    
    /**
     * 设置滤镜配置选线显示状态
     */
    protected void handleShowStateAction()
    {
    }

    /**
     * 请求渲染
     */
    protected void requestRender()
    {
        if (mFilter != null)
        {
            mFilter.submitParameter();
        }

        if (mDelegate != null)
        {
            mDelegate.onFilterConfigRequestRender(this);
        }
    }
}
