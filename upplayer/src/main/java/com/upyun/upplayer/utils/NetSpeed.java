package com.upyun.upplayer.utils;

import android.content.Context;
import android.net.TrafficStats;

public class NetSpeed {

    private long last_data;
    private long last_time;
    private int uid;

    public NetSpeed(Context context) {
        uid = context.getApplicationInfo().uid;
        reset();
    }

    public void reset() {
        last_data = TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? TrafficStats.getTotalRxBytes() : TrafficStats.getUidRxBytes(uid);
        last_time = System.currentTimeMillis();
    }

    public int getAvgSpeed() {

        long data = TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? TrafficStats.getTotalRxBytes() : TrafficStats.getUidRxBytes(uid);
        long traffic_data = data - last_data;
        long duration = System.currentTimeMillis() - last_time;
        return (int) (traffic_data * 1000 / (duration * 1024));
    }

    public long getDateSize() {
        long data = TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? TrafficStats.getTotalRxBytes() : TrafficStats.getUidRxBytes(uid);
        return data - last_data;
    }
}
