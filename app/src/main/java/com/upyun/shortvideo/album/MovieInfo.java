/**
 * TuSDKVideoDemo
 * MovieInfo.java
 *
 * @author     loukang
 * @Date:      Oct 9, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.album;

/**
 * 视频参数信息
 *
 */

public class MovieInfo
{
    private String mPath;
    private int mDuration;

    public MovieInfo(String path, int duration )
    {
        this.mPath = path;
        this.mDuration = duration;
    }

    public String getPath()
    {
        return mPath;
    }

    public void setPath(String mPath)
    {
        this.mPath = mPath;
    }

    public int getDuration()
    {
        return mDuration;
    }

    public void setDuration(int mDuration)
    {
        this.mDuration = mDuration;
    }
}
