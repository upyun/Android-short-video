/**
 * TuSDKVideoDemo
 * MovieInfo.java
 *
 * @author     loukang
 * @Date:      Oct 9, 2017 7:28:12 PM
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.album;

import java.io.Serializable;
import java.util.Objects;

/**
 * 视频参数信息
 *
 */

public class MovieInfo implements Serializable {
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

    @Override
    public String toString() {
        return "MovieInfo{" +
                "mPath='" + mPath + '\'' +
                ", mDuration=" + mDuration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieInfo)) return false;
        MovieInfo info = (MovieInfo) o;
        return mDuration == info.mDuration &&
                mPath.equals(info.mPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPath, mDuration);
    }
}
