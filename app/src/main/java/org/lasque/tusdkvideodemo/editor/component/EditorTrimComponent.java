package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.TrimRecyclerAdapter;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/4 11:10
 * @Copright (c) 2019 tusdk.com. All rights reserved.
 * <p>
 * 裁剪组件
 */
public class EditorTrimComponent extends EditorComponent {
    /** 底部视图 **/
    private View mBottomView;
    /** 返回按钮 **/
    private ImageButton mBackBtn;
    /** 下一步 **/
    private ImageButton mNextBtn;
    /** 裁剪尺寸列表 **/
    private RecyclerView mTrimRecycle;
    /** 适配器 **/
    private TrimRecyclerAdapter mTrimAdapter;
    /** 备份数据 **/
    private int mBackupIndex = 0;
    /** 当前下标 **/
    private int mCurrentIndex = 0;

    private TuSdkEditorPlayer.TuSdkProgressListener mTrimProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (playbackTimeUs == totalTimeUs){
                getEditorPlayer().pausePreview();
                getEditorPlayer().seekTimeUs(0);
                getEditorPlayer().startPreview();
            }
        }
    };



    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorTrimComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Trim;
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setClickable(true);
        getEditorController().getPlayBtn().setOnClickListener(mOnClickListener);
        getEditorPlayer().addProgressListener(mTrimProgressListener);

        if(mTrimAdapter != null)mTrimAdapter.setSelectItem(mBackupIndex);
    }

    @Override
    public void detach() {
        getEditorPlayer().removeProgressListener(mTrimProgressListener);
        getEditorController().getVideoContentView().setClickable(true);
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

    private synchronized View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_trim_bottom, null);
            mBackBtn = bottomView.findViewById(R.id.lsq_trim_close);
            mBackBtn.setOnClickListener(mOnClickListener);
            mNextBtn = bottomView.findViewById(R.id.lsq_trim_sure);
            mNextBtn.setOnClickListener(mOnClickListener);
            mBottomView = bottomView;

            mTrimRecycle = bottomView.findViewById(R.id.lsq_trim_list_view);
            mTrimRecycle.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mTrimAdapter = new TrimRecyclerAdapter();
            mTrimAdapter.setItemCilckListener(new TrimRecyclerAdapter.ItemClickListener() {
                @Override
                public void onItemClick(float ratio, int position) {
                    mCurrentIndex = position;
                    getEditorPlayer().pausePreview();
                    getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
                    getEditorPlayer().seekOutputTimeUs(getEditorPlayer().getCurrentOutputTimeUs());
                    getEditorPlayer().setOutputRatio(ratio,false);

                }
            });
            mTrimRecycle.setAdapter(mTrimAdapter);
        }
        return mBottomView;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lsq_trim_close:
                    if(mBackupIndex != mCurrentIndex){
                        mTrimAdapter.setSelectItem(mBackupIndex);
                    }
                    mCurrentIndex = mBackupIndex;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_trim_sure:
                    mBackupIndex = mCurrentIndex;
                    getEditorController().onBackEvent();
                    break;
                case R.id.lsq_play_btn:
                    if (getEditorPlayer().isPause()) {
                        startPreview();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开始预览
     */
    private void startPreview() {
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {

    }
}
