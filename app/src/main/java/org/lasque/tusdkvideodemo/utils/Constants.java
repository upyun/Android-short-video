/**
 * TuSDKVideoDemo
 * Constants.java
 *
 * @author Bonan
 * @Date: 2017-5-8 上午10:42:48
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 */
package org.lasque.tusdkvideodemo.utils;

import org.lasque.tusdk.core.seles.tusdk.FilterGroup;
import org.lasque.tusdk.core.seles.tusdk.FilterLocalPackage;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Constants {
    /**
     * 最大录制时长 (单位：秒)
     */
    public static final int MAX_RECORDING_TIME = 15;

    /**
     * 最小录制时长 (单位：秒)
     */
    public static final int MIN_RECORDING_TIME = 3;

    /** 最大合成数 (单位：个) */
    public static final int MAX_EDITOR_SELECT_MUN = 9;


    public static int[] AUDIO_EFFECTS = new int[]{R.raw.lsq_audio_lively, R.raw.lsq_audio_oldmovie, R.raw.lsq_audio_relieve};

    /** 配音code列表 **/
    public static String[] AUDIO_EFFECTS_CODES = new String[]{"none","record","lively", "oldmovie", "relieve"};

    /**
     * @param hasCantoon
     * @return
     */
    public static List<FilterGroup> getCameraFilters(boolean hasCantoon){
        List<FilterGroup> filterGroup = FilterLocalPackage.shared().getGroups();
        List<FilterGroup> result = new ArrayList<>();

        for (FilterGroup group : filterGroup){
            if (group.groupFiltersType == 0){
//                Collections.sort(group.filters, new Comparator<FilterOption>() {
//                    @Override
//                    public int compare(FilterOption o1, FilterOption o2) {
//                        return Long.compare(o1.id, o2.id);
//                    }
//                });
                result.add(group);
            }
        }

        Collections.sort(result, new Comparator<FilterGroup>() {
            @Override
            public int compare(FilterGroup o1, FilterGroup o2) {
                return Long.compare(o1.groupId, o2.groupId);
            }
        });

        if (hasCantoon){
            FilterGroup cartoon = FilterLocalPackage.shared().getFilterGroup(252);
            result.add(cartoon);
        }
        return result;
    }

    /** -----------注意事项：视频录制使用人像美颜滤镜(带有磨皮、大眼、瘦脸)，编辑组件尽量不要使用人像美颜滤镜，会造成视频处理过度，效果更不好，建议使用纯色偏滤镜 ----------------*/
    /**
     * 编辑滤镜 filterCode 列表
     */
    public static String[] EDITORFILTERS = {"None", "Olympus_1", "Leica_1", "Gold_1", "Cheerful_1",
            "White_1", "s1950_1", "Blurred_1", "Newborn_1", "Fade_1", "NewYork_1"};


    /**
     * 场景特效code 列表
     */
    public static String[] SCENE_EFFECT_CODES = {"None", "LiveShake01", "LiveMegrim01", "EdgeMagic01", "LiveFancy01_1", "LiveSoulOut01",
            "LiveSignal01", "LiveLightning01", "LiveXRay01", "LiveHeartbeat01", "LiveMirrorImage01", "LiveSlosh01", "LiveOldTV01"};

    /**
     * 时间特效Code列表
     * @since V3.0.0
     */
    public static String[] TIME_EFFECT_CODES = {"node", "repeated", "slowmotion", "reverse"};

    /**
     * 魔法 Code 列表
     */
    public static String[] PARTICLE_CODES = {"None", "snow01", "Music", "Star", "Love", "Bubbles", "Surprise", "Fireball", "Flower",
            "Magic", "Money", "Burning"};
}