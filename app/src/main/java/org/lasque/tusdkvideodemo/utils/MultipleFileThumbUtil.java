package org.lasque.tusdkvideodemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xujie
 * @Date 2018/12/11
 */

public class MultipleFileThumbUtil {

    private List<Long> mMediaTimeList = new ArrayList<>();
    private List<Integer> mThumbNumList = new ArrayList<>();
    private List<TuSdkTimeRange> mMediaTimeRangeList = new ArrayList<>();
    private long sum = 0;

    private ThumbListener listener;

    public interface ThumbListener{
        void onReturnThumb(Bitmap bitmap);
    }

    public void setThumbListener(ThumbListener listener){
        this.listener = listener;
    }

    public MultipleFileThumbUtil(final List<TuSdkMediaDataSource> mediaDataSources,final int thumbMaxNum){
        ThreadHelper.runThread(new Runnable() {
            @Override
            public void run() {
                for (TuSdkMediaDataSource source : mediaDataSources) {
                    String duration = source.getMediaMetadataRetriever().extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (!TextUtils.isEmpty(duration)) {
                        mMediaTimeList.add(Long.valueOf(duration));
                        sum += Long.valueOf(duration);
                    }
                }

                float frameInterval = (thumbMaxNum > 0) ? ((sum / 1000.f) / thumbMaxNum) : (sum / 1000.f);
                for (int i = 0; i < mMediaTimeList.size(); i++) {
                    MediaMetadataRetriever retriever = mediaDataSources.get(i).getMediaMetadataRetriever();
                    float mediaTime = mMediaTimeList.get(i);

                    float offset = 0;
                    if(i > 0){
                        offset = Math.abs((int) Math.ceil((mMediaTimeList.get(i - 1) / (float)sum) * thumbMaxNum) * frameInterval -
                                (mMediaTimeList.get(i - 1) / 1000.0f - offset));
                    }

                    for (float j = offset; j < mediaTime / 1000.0f; j += frameInterval) {
                        long frameTimeUs = (long) (i * 1000000);

                        Bitmap frameBitmap = retriever.getFrameAtTime(frameTimeUs);

                        if (frameBitmap == null) continue;

                        frameBitmap = compressImage(frameBitmap, 80);

                        if (frameBitmap.getWidth() == 80 && frameBitmap.getHeight() == 80) {
                            if (listener != null)
                                listener.onReturnThumb(frameBitmap);
                            return;
                        }

                        final Bitmap scaleBitmap = BitmapHelper.imageScale(frameBitmap, 80, 80);

                        if (listener != null)
                            listener.onReturnThumb(scaleBitmap);

                        // 回收 Bitmap
                        BitmapHelper.recycled(frameBitmap);

                    }
                    if (retriever != null)
                        retriever.release();
                }
            }
        });
    }

    /**
     * 质量压缩
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image,int quality)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    private void load(){
        TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
                TuSdkContext.dip2px(56));
        TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();

//        extractor.setOutputImageSize(tuSdkSize)
//                .setVideoDataSource(TuSDKMediaDataSource.create(mVideoPaths.get(0).getPath()))
//                .setExtractFrameCount(20);
    }
}
