package com.upyun.shortvideo.utils;

import android.media.MediaMetadataRetriever;

import org.lasque.tusdk.core.struct.TuSdkSize;

/**
 * 视频信息提取工具类
 */


public class VideoInfoUtils
{
    /**
     * 根据视频路径获取视频时长
     *
     * @param videoPath
     * @return
     */
    public static float getVideoDuration(String videoPath)
    {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try
        {
            mediaMetadataRetriever.setDataSource(videoPath);
        }
        catch (IllegalArgumentException e)
        {
            return 0.0f;
        }

        // 播放时长单位为毫秒
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        mediaMetadataRetriever.release();

        // 注意：视频时长需要转成float类型，转成int类型时间会少掉
        return duration == null ? 0 : Float.parseFloat(duration) / 1000;
    }

    /**
     * 获取视频宽高
     *
     * @param videoPath
     * @return
     */
    public static TuSdkSize getVideoSize(String videoPath)
    {
        TuSdkSize videoSize = new TuSdkSize(0, 0);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try
        {
            mediaMetadataRetriever.setDataSource(videoPath);
        }
        catch (IllegalArgumentException e)
        {
            return videoSize;
        }

        // 获取视频宽高
        String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        videoSize = new TuSdkSize(Integer.parseInt(width), Integer.parseInt(height));
        mediaMetadataRetriever.release();

        return videoSize;
    }
}
