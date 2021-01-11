package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.seles.tusdk.FilterGroup;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.FilterConfigSeekbar;
import org.lasque.tusdkvideodemo.views.FilterConfigView;
import org.lasque.tusdkvideodemo.views.FilterRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.TabPagerIndicator;
import org.lasque.tusdkvideodemo.views.newFilterUI.FilterFragment;
import org.lasque.tusdkvideodemo.views.newFilterUI.FilterViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeFilter;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:47
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 滤镜组件
 */
public class EditorFilterComponent extends EditorComponent {

    /** BottomView */
    private View mBottomView;
    /** 编辑器 */
    private TuSdkMovieEditor mMovieEditor;

    private ViewPager2 mFilterViewPager;
    private TabPagerIndicator mFilterTabIndicator;
    private FilterViewPagerAdapter mFilterViewPagerAdapter;
    private ImageView mFilterReset;

    private List<FilterFragment> mFilterFragments;

    private List<FilterGroup> mFilterGroups;

    private FilterConfigView mFilterConfigView;

    private String mCurrentFilterCode = "";

    private int mPreviewFragmentIndex = -1;




    /** 播放进度回调 **/
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (percentage >= 1) {
//                getEditorController().getPlayBtn().setVisibility(View.VISIBLE);

                startPreView();
            }
        }
    };


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorFilterComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Filter;
        mMovieEditor = editorController.getMovieEditor();
        getFilterConfigView().showView(false);
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayProgressListener);
        //设置滤镜设置的回调
        mMovieEditor.getEditorEffector().setFilterChangeListener(mFilterChangeListener);
        startPreView();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);
        getEditorController().getPlayBtn().setClickable(true);
        getEditorController().getPlayBtn().setOnClickListener(onClickListener);

        mSelectIndex = mMementoEffectIndex;
        mSelectEffectData = mMementoEffectData;
        if (mPreviewFragmentIndex != -1){
            mFilterViewPager.setCurrentItem(mPreviewFragmentIndex);
            mFilterFragments.get(mPreviewFragmentIndex).setCurrentPosition(mSelectIndex);
            mFilterConfigView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void detach() {
        mPreviewFragmentIndex = mFilterTabIndicator.getCurrentPosition();
        mMovieEditor.getEditorPlayer().removeProgressListener(mPlayProgressListener);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getPlayBtn().setClickable(false);
        getFilterConfigView().showView(false);
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {

    }

    /**
     * 初始化BottomView
     *
     * @return
     */
    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_filter_bottom, null);
            ImageButton ibFilterBack = bottomView.findViewById(R.id.lsq_filter_close);
            ImageButton ibFilterSure = bottomView.findViewById(R.id.lsq_filter_sure);
            ibFilterBack.setOnClickListener(onClickListener);
            ibFilterSure.setOnClickListener(onClickListener);
            mBottomView = bottomView;
            initFilterGroupsViews(bottomView,getEditorController().getActivity().getSupportFragmentManager(),getEditorController().getActivity().getLifecycle(), Constants.getCameraFilters(false));
        }
        return mBottomView;
    }

    public void initFilterGroupsViews(View view,FragmentManager fragmentManager, Lifecycle lifecycle, List<FilterGroup> filterGroups) {
        mFilterGroups = filterGroups;
        mFilterReset = view.findViewById(R.id.lsq_filter_reset);
        mFilterReset.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                mCurrentFilterCode = "";
                mFilterFragments.get(mFilterTabIndicator.getCurrentPosition()).removeFilter();
                mFilterConfigView.setVisibility(View.GONE);
                mMovieEditor.getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeFilter);
                mFilterViewPagerAdapter.notifyDataSetChanged();
            }
        });

        mFilterTabIndicator = view.findViewById(R.id.lsq_filter_tabIndicator);

        mFilterViewPager = view.findViewById(R.id.lsq_filter_view_pager);
        mFilterViewPager.requestDisallowInterceptTouchEvent(true);
        List<String> tabTitles = new ArrayList<>();
        List<FilterFragment> fragments = new ArrayList<>();
        for (FilterGroup group : mFilterGroups){
            FilterFragment fragment = FilterFragment.newInstance(group);
            fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                @Override
                public void onFilterItemClick(String code,int position) {
                    if (TextUtils.equals(mCurrentFilterCode,code)){
                        mFilterConfigView.setVisibility(mFilterConfigView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    } else {
                        mCurrentFilterCode = code;
                        mSelectIndex = position;
                        //设置滤镜
                        switchFilter(mCurrentFilterCode);
                    }
                }
            });

            fragments.add(fragment);
            tabTitles.add(group.getName());
        }
        mFilterFragments = fragments;
        mFilterViewPagerAdapter = new FilterViewPagerAdapter(fragmentManager,lifecycle,fragments);
        mFilterViewPager.setAdapter(mFilterViewPagerAdapter);
        mFilterTabIndicator.setViewPager(mFilterViewPager,0);
        mFilterTabIndicator.setDefaultVisibleCounts(tabTitles.size());
        mFilterTabIndicator.setTabItems(tabTitles);
    }


    /**
     * 滤镜调节栏
     *
     * @return
     */
    public FilterConfigView getFilterConfigView() {
        if (mFilterConfigView == null) {
            mFilterConfigView = (FilterConfigView) getEditorController().getActivity().findViewById(R.id.lsq_filter_config_view);
            mFilterConfigView.setSeekBarDelegate(mConfigSeekBarDelegate);
        }

        return mFilterConfigView;
    }

    /** 滤镜切换回调 */
    private TuSdkEditorEffector.TuSdkEffectorFilterChangeListener mFilterChangeListener = new TuSdkEditorEffector.TuSdkEffectorFilterChangeListener() {
        @Override
        public void onFilterChanged(FilterWrap filter) {
            if (filter == null) return;

            SelesParameters params = filter.getFilterParameter();
            filter.setFilterParameter(params);
            boolean isNeedShow = false;
            if (getFilterConfigView().getVisibility() == View.VISIBLE){
                isNeedShow = true;
            }
            getFilterConfigView().setSelesFilter(filter.getFilter());
            if (!isNeedShow){
                getFilterConfigView().setVisibility(View.GONE);
            }

        }
    };

    /** 滤镜拖动条监听事件 */
    private FilterConfigView.FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigView.FilterConfigViewSeekBarDelegate() {

        @Override
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, SelesParameters.FilterArg arg) {
            if (arg == null) return;
        }

    };

    /** 滤镜选择事件 */
    private FilterRecyclerAdapter.ItemClickListener itemClickListener = new FilterRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (TuSdkViewHelper.isFastDoubleClick()) return;

            getFilterConfigView().setVisibility((position == 0) ? View.GONE : (mSelectIndex == position ?
                    (getFilterConfigView().getVisibility() == View.GONE ? View.VISIBLE : View.GONE)
                    : View.GONE));
            if(mSelectIndex == position) return;
            mSelectIndex = position;
            switchFilter("");
        }
    };

    /** 点击事件 */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 关闭滤镜模块，隐藏调节栏
            getFilterConfigView().showView(false);
            switch (v.getId()) {
                case R.id.lsq_filter_close:
                    if (mSelectEffectData != null) {
                        getEditorController().getMovieEditor().getEditorEffector().removeMediaEffectData(mSelectEffectData);
                        if (mMementoEffectData != null) {
                            switchFilter((TuSdkMediaFilterEffectData) mMementoEffectData);
                        }else{
                            for (FilterFragment fragment : mFilterFragments){
                                fragment.removeFilter();
                            }
                        }
                    }

                    mSelectIndex = -1;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_filter_sure:
                    if (mSelectEffectData != null) {
                        mMementoEffectData = mSelectEffectData;
                    }else {
                        mMementoEffectData = null;
                    }

                    mMementoEffectIndex = mSelectIndex;
                    mSelectEffectData = null;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_play_btn:
                    if(mMovieEditor.getEditorPlayer().isPause()){
                        startPreView();
                        getEditorController().getPlayBtn().setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    /**
     * 切换滤镜
     *
     * @param code 滤镜code
     */
    protected void switchFilter(String code) {
        // 切换滤镜前必须打开视频预览, 滤镜切换依赖于视频的编解码
        // 如果视频暂停情况下切换滤镜会导致切换失败，onFilterChanged方法也不会回调
        startPreView();
        TuSdkMediaFilterEffectData mediaFilterEffectData = new TuSdkMediaFilterEffectData(code);
        switchFilter(mediaFilterEffectData);
    }

    /**
     * 根据当前数据切换滤镜
     * @param filterEffectData 特效数据
     */
    private void switchFilter(TuSdkMediaFilterEffectData filterEffectData) {
        mSelectEffectData = filterEffectData;
        getEditorController().getMovieEditor().getEditorEffector().addMediaEffectData(filterEffectData);
    }

    /**
     * 启动视频预览
     */
    protected void startPreView() {
        if (mMovieEditor == null) return;
        mMovieEditor.getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

}
