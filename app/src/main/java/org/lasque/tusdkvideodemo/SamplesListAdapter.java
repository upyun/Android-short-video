/**
 * TuSDKVideoDemo
 * ExpandableSamplesListAdapter.java
 *
 * @author Bonan
 * @Date 9:22:31 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 */
package org.lasque.tusdkvideodemo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.component.AudioMixedActivity;
import org.lasque.tusdkvideodemo.component.AudioPitchEngineActivity;
import org.lasque.tusdkvideodemo.component.AudioRecordActivity;
import org.lasque.tusdkvideodemo.component.MovieCutActivity;
import org.lasque.tusdkvideodemo.component.MovieMixerActivity;
import org.lasque.tusdkvideodemo.component.MovieThumbActivity;
import org.lasque.tusdkvideodemo.editor.MovieEditorCutActivity;

/**
 * 功能列表界面 Adapter
 *
 * @author Bonan
 */
public class SamplesListAdapter extends ArrayAdapter {
    private Context mContext;

    private SampleItem[] sampleItems = {
            SampleItem.AUDIO_MIXED,
            SampleItem.VIDEO_BGM,
            SampleItem.GAIN_THUMBNAIL,
            SampleItem.VIDEO_MIXED,
            SampleItem.ALBUM_VIDEO_TIMECUT_SAVE,
            SampleItem.AUDIO_FILE_RECORDER,
            SampleItem.AUDIO_ENGINE_PITCH,
    };

    public SamplesListAdapter(@NonNull Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return sampleItems.length;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return sampleItems[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View viewHolder;
        if (convertView != null) {
            viewHolder = convertView;
        } else {
            viewHolder = LayoutInflater.from(mContext).inflate(R.layout.sample_list_item, null);
        }

        ((TextView) viewHolder.findViewById(R.id.itemTitle))
                .setText(mContext.getResources().getString(((SampleItem) this.getItem(position)).titleId));

        return viewHolder;
    }

    public enum SampleItem {

        // 功能 API 展示
        AUDIO_MIXED(R.string.lsq_audio_mixed, AudioMixedActivity.class.getName(), 0, false),
        VIDEO_BGM(R.string.lsq_video_bgm, MovieMixerActivity.class.getName(), 0, false),
        GAIN_THUMBNAIL(R.string.lsq_gain_thumbnail, MovieThumbActivity.class.getName(), 0, false),
        VIDEO_MIXED(R.string.lsq_video_mixed, MovieEditorCutActivity.class.getName(), 9, false),
        ALBUM_VIDEO_TIMECUT_SAVE(R.string.lsq_album_video_timecut_save, MovieCutActivity.class.getName(), 1, false),
        AUDIO_FILE_RECORDER(R.string.lsq_audio_file_recorder, AudioRecordActivity.class.getName(), 0, false),
        AUDIO_ENGINE_PITCH(R.string.lsq_audio_engine_pitch, AudioPitchEngineActivity .class.getName(), 0, false);

        public String className;
        public int titleId;
        public int OpenAlbumForPicNum;
        public boolean needOpenCamera;

        private SampleItem(int titleId, String className, int needPicNum, boolean needOpenCamera) {
            this.className = className;
            this.titleId = titleId;
            this.OpenAlbumForPicNum = needPicNum;
            this.needOpenCamera = needOpenCamera;
        }
    }
}
