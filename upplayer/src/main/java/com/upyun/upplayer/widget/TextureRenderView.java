package com.upyun.upplayer.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by 27536 on 2017/9/27.
 */

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureRenderView extends TextureView implements IRenderView {

    private MeasureHelper mMeasureHelper;
    private Listener mSurfaceTextureListener;


    public TextureRenderView(Context context) {
        super(context);
        initView(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mSurfaceTextureListener = new Listener(this);
        mMeasureHelper = new MeasureHelper(this);
        setSurfaceTextureListener(mSurfaceTextureListener);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return false;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        if (degree != getRotation()) {
            Log.i("setVideoRotation", "degree:" + degree);
            mMeasureHelper.setVideoRotation(degree);
            super.setRotation(degree);
            requestLayout();
        }
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    @Override
    public void addRenderCallback(@NonNull IRenderCallback callback) {
        mSurfaceTextureListener.addRenderCallback(callback);
    }

    @Override
    public void removeRenderCallback(@NonNull IRenderCallback callback) {
        mSurfaceTextureListener.removeRenderCallback(callback);
    }


    private static final class Listener implements SurfaceTextureListener {
        SurfaceTexture mSurfaceTexture;
        Surface mSurface;
        private int mWidth;
        private int mHeight;

        private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<>();
        private WeakReference<TextureRenderView> mWeakSurfaceView;

        public Listener(@NonNull TextureRenderView view) {
            mWeakSurfaceView = new WeakReference<>(view);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mWidth = 0;
            mHeight = 0;
            mSurfaceTexture = surface;
            mSurface = new Surface(surface);

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), mSurfaceTexture, mSurface);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mWidth = width;
            mHeight = height;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), mSurfaceTexture, mSurface);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceChanged(surfaceHolder, -1, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
            Surface s = mSurface;
            mSurface = null;
            s.release();

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), mSurfaceTexture, mSurface);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(surfaceHolder);
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

        public void addRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.put(callback, callback);

            ISurfaceHolder surfaceHolder = null;
            if (mSurfaceTexture != null) {
                surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), mSurfaceTexture, mSurface);
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight);
            }

        }

        public void removeRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.remove(callback);
        }
    }


    private static final class InternalSurfaceHolder implements ISurfaceHolder {
        private TextureRenderView mRenderView;
        private SurfaceTexture mSurfaceTexture;
        private Surface mSurface;

        public InternalSurfaceHolder(@NonNull TextureRenderView surfaceView,
                                     @Nullable SurfaceTexture surfaceHolder,
                                     @Nullable Surface surface) {
            mRenderView = surfaceView;
            mSurfaceTexture = surfaceHolder;
            mSurface = surface;
        }

        public void bindToMediaPlayer(IMediaPlayer mp) {
            if (mp != null) {
//                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) &&
//                        (mp instanceof ISurfaceTextureHolder)) {
//                    ISurfaceTextureHolder textureHolder = (ISurfaceTextureHolder) mp;
//                    textureHolder.setSurfaceTexture(null);
//                }
                Log.i("bindToMediaPlayer", "mSurface isValid" + mSurface.isValid());
                mp.setSurface(mSurface);
            }
        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return mRenderView;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            return null;
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return mSurfaceTexture;
        }

        @Nullable
        @Override
        public Surface openSurface() {
            return mSurface;
        }
    }


}
