package com.upyun.shortvideo.editor.component;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;

import com.upyun.shortvideo.editor.MovieEditorController;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.FilterConfigSeekbar;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterRecyclerAdapter;

import java.util.Arrays;

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
    /** 滤镜列表 */
    private RecyclerView mFilterRecyclerView;
    /** 滤镜适配器 */
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    /** 滤镜调节 */
    private FilterConfigView mConfigView;
    /** 编辑器 */
    private TuSdkMovieEditor mMovieEditor;

    /** 播放进度回调 **/
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (percentage >= 1) {
                getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
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
    }

    @Override
    public void detach() {
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
            initFilterLayout();
        }
        return mBottomView;
    }

    private void initFilterLayout() {
        getFilterListView();

        mFilterRecyclerAdapter.setFilterList(Arrays.asList(Constants.EDITORFILTERS));
    }

    /**
     * 滤镜栏视图
     *
     * @return
     */
    public RecyclerView getFilterListView() {
        if (mFilterRecyclerView == null) {
            mFilterRecyclerView = mBottomView.findViewById(R.id.lsq_filter_list_view);
            mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mFilterRecyclerAdapter = new FilterRecyclerAdapter();
            mFilterRecyclerAdapter.setItemCilckListener(itemClickListener);
            mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter);
        }
        return mFilterRecyclerView;
    }

    /**
     * 滤镜调节栏
     *
     * @return
     */
    public FilterConfigView getFilterConfigView() {
        if (mConfigView == null) {
            mConfigView = (FilterConfigView) getEditorController().getActivity().findViewById(R.id.lsq_filter_config_view);
            mConfigView.setSeekBarDelegate(mConfigSeekBarDelegate);
        }

        return mConfigView;
    }

    /** 滤镜切换回调 */
    private TuSdkEditorEffector.TuSdkEffectorFilterChangeListener mFilterChangeListener = new TuSdkEditorEffector.TuSdkEffectorFilterChangeListener() {
        @Override
        public void onFilterChanged(FilterWrap filter) {
            if (filter == null) return;

            SelesParameters params = filter.getFilterParameter();
            filter.setFilterParameter(params);
            getFilterConfigView().setSelesFilter(filter.getFilter());
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
            switchFilter(mFilterRecyclerAdapter.getFilterList().get(position));
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
                            mFilterRecyclerAdapter.setCurrentPosition(mMementoEffectIndex);
                        }else
                            mFilterRecyclerAdapter.setCurrentPosition(0);
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
