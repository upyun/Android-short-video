package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;

/**
 * 魔法预览界面
 */
public class MagicEditorLayout extends TuSdkRelativeLayout implements View.OnClickListener
{
    // 魔法预览界面
    private MagicEffectsTimelineView mTimeLineView;

    // 魔法预览界面返回按钮
    private ImageView mMagicPreviewBack;

    // 魔法预览界面调整效果大小按钮
    private ImageView mMagicSizeBtn;

    // 魔法预览界面播放按钮
    private ImageView mMagicPlayBtn;

    // 魔法预览界面调整效果颜色按钮
    private ImageView mMagicColorBtn;
    private MagicPreviewLayoutDelegate mDelegate;

    // 调节粒子大小拖动条
    private SizeSeekBar mSizSeekBar;

    // 调节粒子颜色拖动条
    private ColorSeekBar mColorSeekBar;

    /** 撤销按钮 */
    private TuSdkTextButton mUndoButton;

    public interface MagicPreviewLayoutDelegate
    {
        void onUndo();

        /** 返回事件 */
        void onBackAction();

        /** 大小变化 */
        void onSizeSeekBarProgressChanged(ConfigViewSeekBar seekbar);

        /** 颜色变化 0 ： 默认颜色*/
        void onColorSeekBarProgressChanged(int color);

        /**  魔法预览页面播放暂停事件 */
        void onMagicPreviewPlay();
    }

    public MagicEditorLayout(Context context)
    {
        super(context);
    }

    public MagicEditorLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MagicEditorLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void loadView()
    {
        super.loadView();

        // 动画时间轴视图
        mTimeLineView = getViewById(com.upyun.shortvideo.R.id.lsq_magic_preview_timelineView);
        mTimeLineView.setDelegate(mEffectsTimelineViewDelegate);
        mMagicPreviewBack = (ImageView) findViewById(com.upyun.shortvideo.R.id.lsq_magic_back);
        mMagicPreviewBack.setOnClickListener(this);
        mMagicSizeBtn = (ImageView) findViewById(com.upyun.shortvideo.R.id.lsq_magic_edit_size_btn);
        mMagicSizeBtn.setOnClickListener(this);
        mMagicColorBtn = findViewById(com.upyun.shortvideo.R.id.lsq_magic_edit_color_btn);
        mMagicColorBtn.setOnClickListener(this);
        mMagicPlayBtn = findViewById(com.upyun.shortvideo.R.id.lsq_magic_edit_play_btn);
        mMagicPlayBtn.setOnClickListener(this);
        mSizSeekBar = new SizeSeekBar();
        mColorSeekBar = new ColorSeekBar();

        mUndoButton = findViewById(com.upyun.shortvideo.R.id.lsq_magic_preview_cell_undo_btn);
        mUndoButton.setOnClickListener(this);
        updateUndoButton(false);
    }

    @Override
    public void viewDidLoad()
    {
        super.viewDidLoad();
    }

    public ImageView getMagicPlayBtn()
    {
        return mMagicPlayBtn;
    }

    public void setDelegate(MagicPreviewLayoutDelegate delegate)
    {
        this.mDelegate = delegate;
    }

    private EffectsTimelineView.EffectsTimelineViewDelegate mEffectsTimelineViewDelegate = new EffectsTimelineView.EffectsTimelineViewDelegate()
    {
        @Override
        public void onProgressCursorWillChaned()
        {
        }

        @Override
        public void onProgressChaned(float progress)
        {
        }

        @Override
        public void onEffectNumChanged(int effectNum)
        {
            updateUndoButton(effectNum == 0 ? false :true);
        }
    };

    /**
     * 更新撤销按钮的状态
     *
     * @param isEnableClicked
     */
    public void updateUndoButton(boolean isEnableClicked)
    {
        Drawable cancelUnClickedDrawable = getResources().getDrawable(com.upyun.shortvideo.R.drawable.edit_ic_back);
        cancelUnClickedDrawable.setAlpha(isEnableClicked ? 255 : 66);
        // 这一步必须要做,否则不会显示
        cancelUnClickedDrawable.setBounds(0, 0, cancelUnClickedDrawable.getMinimumWidth(), cancelUnClickedDrawable.getMinimumHeight());
        mUndoButton.setCompoundDrawables(null, cancelUnClickedDrawable, null, null);

        mUndoButton.setEnabled(isEnableClicked);
        mUndoButton.setTextColor(getResColor(isEnableClicked ? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_color_alpha_20));
    }

    /**
     * 魔法效果预览界面时间轴视图
     *
     * @return
     */
    public MagicEffectsTimelineView getTimelineView()
    {
        return mTimeLineView;
    }

    public float getSize()
    {
        return mSizSeekBar.getValue();
    }

    public int getColor()
    {
        return mColorSeekBar.getValue();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case com.upyun.shortvideo.R.id.lsq_magic_back:
                if (mDelegate != null)
                    mDelegate.onBackAction();
                break;
            case com.upyun.shortvideo.R.id.lsq_magic_edit_size_btn:
                toggleMagicSizeMode(true);
                break;
            case com.upyun.shortvideo.R.id.lsq_magic_edit_color_btn:
                toggleMagicColorMode(true);
                break;
            case com.upyun.shortvideo.R.id.lsq_magic_edit_play_btn:
                if (mDelegate != null)
                    mDelegate.onMagicPreviewPlay();
                break;
            case  com.upyun.shortvideo.R.id.lsq_magic_preview_cell_undo_btn:
                if (mDelegate != null)
                    mDelegate.onUndo();
                break;
        }
    }

    /**
     * 切换粒子大小拖动条
     *
     * @param isShown
     */
    public void toggleMagicSizeMode(boolean isShown)
    {
        mMagicSizeBtn.setVisibility(isShown ? GONE : VISIBLE);
        mSizSeekBar.getMagicSizeSeekBarWrap().setVisibility(isShown ? VISIBLE : GONE);
    }

    /**
     * 切换粒子颜色拖动条
     *
     * @param isShown
     */
    public void toggleMagicColorMode(boolean isShown)
    {
        mMagicColorBtn.setVisibility(isShown ? GONE : VISIBLE);
        mColorSeekBar.getMagicColorSeekBarWrap().setVisibility(isShown ? VISIBLE : GONE);
    }

    /**
     * 设置魔法预览页时间轴动画委托
     *
     * @param delegate
     */
    public void setTimelineDelegate(SceneEffectsTimelineView.EffectsTimelineViewDelegate delegate)
    {
        mTimeLineView.setDelegate(delegate);
    }

    /**
     * 处理调节粒子大小拖动条逻辑
     */
    public class SizeSeekBar implements OnClickListener
    {
        private TextView mSeekBarTitle;

        /** 魔法效果大小调节栏 */
        private ConfigViewSeekBar mSizeSeekBar;

        // 粒子大小调节栏上层布局
        private RelativeLayout mSizeSeekBarWrap;

        public SizeSeekBar()
        {
            initView();
        }

        public void initView()
        {
            mSeekBarTitle = getMaigcSizeSeekBar().findViewById(com.upyun.shortvideo.R.id.lsq_titleView);
            mSeekBarTitle.setText(getResString(com.upyun.shortvideo.R.string.lsq_magic_size));
            mSeekBarTitle.setOnClickListener(this);
            TextView valueText = getMaigcSizeSeekBar().findViewById(com.upyun.shortvideo.R.id.lsq_configValueView);
            valueText.setVisibility(GONE);

            getMaigcSizeSeekBar().getSeekbar().getBottomView().setBackground(TuSdkContext.getDrawable(com.upyun.shortvideo.R.color.lsq_magic_size_baground));
        }

        @Override
        public void onClick(View v)
        {
            if (v == mSeekBarTitle)
            {
                toggleMagicSizeMode(false);
            }
        }

        /**
         * 粒子大小调节栏
         *
         * @return
         */
        public ConfigViewSeekBar getMaigcSizeSeekBar()
        {
            if (mSizeSeekBar == null)
            {
                ConfigViewSeekBar.setLayoutId("tusdk_config_seekbar_two");
                mSizeSeekBar = (ConfigViewSeekBar) findViewById(com.upyun.shortvideo.R.id.lsq_size_seek_bar);
                mSizeSeekBar.setBackgroundColor(getResColor(com.upyun.shortvideo.R.color.lsq_color_transparent));
                mSizeSeekBar.setHeight(60);
                mSizeSeekBar.setDelegate(mSizeSeekBarDelegate);
            }

            return mSizeSeekBar;
        }

        public float getValue()
        {
            return getMaigcSizeSeekBar().getSeekbar().getProgress();
        }

        /**
         * 粒子大小调节栏上层布局
         *
         * @return
         */
        public RelativeLayout getMagicSizeSeekBarWrap()
        {
            if (mSizeSeekBarWrap == null)
                mSizeSeekBarWrap = findViewById(com.upyun.shortvideo.R.id.lsq_size_seek_bar_wrap);

            return mSizeSeekBarWrap;
        }

        /** 粒子大小拖动条监听事件 */
        private ConfigViewSeekBar.ConfigSeekbarDelegate mSizeSeekBarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate()
        {
            @Override
            public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg)
            {
                if (mDelegate != null)
                    mDelegate.onSizeSeekBarProgressChanged(seekbar);
            }
        };
    }

    /**
     * 处理调节粒子颜色拖动条逻辑
     */
    public class ColorSeekBar implements OnClickListener
    {
        private TextView mSeekBarTitle;

        /** 魔法效果大小调节栏 */
        private ConfigViewSeekBar mSizeSeekBar;

        // 粒子大小调节栏上层布局
        private RelativeLayout mColorSeekBarWrap;

        // 颜色条视图
        private View mColorView;

        // 颜色条Bitmap
        public Bitmap mColorBarBitmap;

        public ColorSeekBar()
        {
            initView();
        }

        public void initView()
        {
            mSeekBarTitle = getMaigcColorSeekBar().findViewById(com.upyun.shortvideo.R.id.lsq_titleView);
            mSeekBarTitle.setText(getResString(com.upyun.shortvideo.R.string.lsq_magic_color));
            mSeekBarTitle.setOnClickListener(this);
            TextView valueText = getMaigcColorSeekBar().findViewById(com.upyun.shortvideo.R.id.lsq_configValueView);
            valueText.setVisibility(GONE);

            // 修改SeekBar背景为色度表
            mColorView = getMaigcColorSeekBar().getSeekbar().getBottomView();
            mColorView.setBackground(TuSdkContext.getDrawable(com.upyun.shortvideo.R.drawable.lsq_color_bar));
            getMaigcColorSeekBar().getSeekbar().getTopView().setVisibility(GONE);
        }

        @Override
        public void onClick(View v)
        {
            if (v == mSeekBarTitle)
            {
                toggleMagicColorMode(false);
            }
        }

        /**
         * 粒子大小调节栏
         *
         * @return
         */
        public ConfigViewSeekBar getMaigcColorSeekBar()
        {
            if (mSizeSeekBar == null)
            {
                ConfigViewSeekBar.setLayoutId("tusdk_config_seekbar_two");
                mSizeSeekBar = (ConfigViewSeekBar) findViewById(com.upyun.shortvideo.R.id.lsq_color_seek_bar);
                mSizeSeekBar.setBackgroundColor(getResColor(com.upyun.shortvideo.R.color.lsq_color_transparent));
                mSizeSeekBar.setHeight(60);
                mSizeSeekBar.setDelegate(mColorSeekBarDelegate);
            }

            return mSizeSeekBar;
        }

        /**
         * 粒子颜色调节栏上层布局
         *
         * @return
         */
        public RelativeLayout getMagicColorSeekBarWrap()
        {
            if (mColorSeekBarWrap == null)
                mColorSeekBarWrap = findViewById(com.upyun.shortvideo.R.id.lsq_color_seek_bar_wrap);

            return mColorSeekBarWrap;
        }

        /** 粒子颜色拖动条监听事件 */
        private ConfigViewSeekBar.ConfigSeekbarDelegate mColorSeekBarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate()
        {
            @Override
            public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg)
            {
                if (mDelegate != null)
                    mDelegate.onColorSeekBarProgressChanged(getValue());

            }
        };

        /**
         * 获取当前的颜色
         */
        public int getValue()
        {
            // 0 为默认颜色
            if (getMaigcColorSeekBar().getSeekbar().getProgress() == 0) return 0;

            int x = (int) (getMaigcColorSeekBar().getSeekbar().getProgress() * mColorView.getWidth());
            int y = mColorView.getHeight() / 2;

            if (mColorBarBitmap == null)
                mColorBarBitmap = getBitmapFromView(mColorView);

            if (x >= mColorBarBitmap.getWidth() || y >= mColorBarBitmap.getHeight() || x < 0 || y < 0) return 0;

            int pixel = mColorBarBitmap.getPixel(x,y);

            return pixel;
        }

        /**
         * 获取view的bitmap
         *
         * @param view
         * @return
         */
        public  Bitmap getBitmapFromView(View view)
        {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            // Draw background
            Drawable bgDrawable = view.getBackground();
            if (bgDrawable != null)
            {
                bgDrawable.draw(canvas);
            }
            else
            {
                canvas.drawColor(Color.WHITE);
            }
            // Draw view to canvas
            view.draw(canvas);
            return bitmap;
        }

        public Bitmap getColorBarBitmap()
        {
            return mColorBarBitmap;
        }
    }

    /**
     * 销毁资源
     */
    public void release()
    {
        if (mColorSeekBar != null)
        {
            if (mColorSeekBar.getColorBarBitmap() == null) return;

            mColorSeekBar.getColorBarBitmap().recycle();
        }
    }
}
