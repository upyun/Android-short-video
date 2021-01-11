package org.lasque.tusdkvideodemo.editor.component;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.MovieEditorTabBar;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 14:14
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 编辑 主页组件
 */
public class EditorHomeComponent extends EditorComponent {
    private static final String TAG = "HomeComponent";
    /** 头部视图 **/
    private View mHeaderView;
    /** 底部视图 **/
    private View mBottomView;
    /** 底部编辑组件选择控件 **/
    private MovieEditorTabBar mEditorTabBar;
    /** 是否启用控件 **/
    private boolean isEnable = true;

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorHomeComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Home;
    }

    @Override
    public void attach() {
        getEditorController().getHeaderView().addView(getHeaderView());
        getEditorController().getBottomView().addView(getBottomView());
    }

    @Override
    public void detach() {

    }

    /**
     * 设置是否启用控件
     *
     * @param isEnable
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
        if (mEditorTabBar == null) return;
        mEditorTabBar.setEnable(isEnable);
    }

    @Override
    public View getHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = initHeadView();
        }
        return mHeaderView;
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
     * 初始化headView
     *
     * @return View
     */
    private View initHeadView() {
        if (mHeaderView == null) {
            View headView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_navigation, null);
            ImageView ivBack = headView.findViewById(R.id.lsq_back);
            TextView tvNext = headView.findViewById(R.id.lsq_next);
            ivBack.setOnClickListener(mOnClickListener);
            tvNext.setOnClickListener(mOnClickListener);
            mHeaderView = headView;
        }
        return mHeaderView;
    }

    /**
     * 初始化BottomView
     *
     * @return View
     */
    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_home_bottom, null);
            mEditorTabBar = bottomView.findViewById(R.id.lsq_bottom_navigator);
            mEditorTabBar.setDelegate(getEditorController().getMovieEditorTabChangeListener());
            mEditorTabBar.loadView();
            mBottomView = bottomView;
        }
        return mBottomView;
    }


    /** 点击事件回调 **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isEnable) return;
            switch (v.getId()) {
                case R.id.lsq_back:
                    getEditorController().getActivity().finish();
                    break;
                case R.id.lsq_next:
                    if(getEditorController().isSaving())return;
                    getEditorController().saveVideo();
                    break;
            }
        }
    };
}
