package com.upyun.upplayer.utils;


import com.upyun.upplayer.widget.UpVideoView;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class TrackUtil {
    public static void lowerVideoTrack(UpVideoView view) {
        ITrackInfo[] infos = view.getTrackInfo();
        int selectedVideo = view.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
        for (int i = selectedVideo; i >= 0; i--) {
            if (infos[i].getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO && i < selectedVideo) {
                view.deselectTrack(selectedVideo);
                view.selectTrack(i);
            }
        }
    }

    public static void lowerAudioTrack(UpVideoView view) {
        ITrackInfo[] infos = view.getTrackInfo();
        int selectedAudio = view.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        for (int i = selectedAudio; i >= 0; i--) {
            if (infos[i].getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO && i < selectedAudio) {
                view.deselectTrack(selectedAudio);
                view.selectTrack(i);
            }
        }
    }

}
