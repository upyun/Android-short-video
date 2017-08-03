package com.upyun.upplayer.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.upyun.upplayer.model.Monitor;
import com.upyun.upplayer.utils.DeviceIdUtil;
import com.upyun.upplayer.utils.NetSpeed;
import com.upyun.upplayer.utils.NetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MonitorRecorder {

    public List<Monitor> monitorList;
    public NetSpeed netSpeed;
    public long startTime;
    public long firstPacketTime;
    public long firstPlayTime;
    private Monitor monitor;
    private Context mCotext;

    private long bufferStartTime;
    private long bufferEndTime;
    private File tempFile;
    private int bufferLength;
    public static int POSTCOUNT = 0;

    public String mServerPid;
    public String mServerCid;
    public String mServerIP;

    public MonitorRecorder(Context context) {
        this.mCotext = context;
        this.monitor = new Monitor();
        this.tempFile = new File(context.getCacheDir(), "monitor");
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(tempFile));
            monitorList = (List<Monitor>) os.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (monitorList == null) {
            monitorList = new ArrayList<>();
        }


    }

    // 获取pid, cid等
    public void getMetaData(Bundle bundle) {
        if (bundle != null) {
            mServerPid = bundle.getString("srs_pid");
            mServerCid = bundle.getString("srs_id");
            mServerIP = bundle.getString("srs_server_ip");
            Log.d("MonitorRecorder", "srs_pid: " + mServerPid);
            Log.d("MonitorRecorder", "srs_id: " + mServerCid);
            Log.d("MonitorRecorder", "srs_server_ip: " + mServerIP);
            if (mServerPid != null) this.monitor.setServerPid(mServerPid);
            if (mServerCid != null) this.monitor.setServerCid(mServerCid);
            if (mServerIP != null) this.monitor.setServerIp(mServerIP);
        }
    }

    public void start() {
        this.startTime = System.currentTimeMillis();

        this.bufferLength = 0;
        this.monitor = new Monitor();
        this.monitorList.add(this.monitor);
        this.monitor.setNetworkType(NetUtil.isConnected(mCotext).toString());
        this.monitor.setPlayerVersion(Config.VERSION);
        this.monitor.setOsVersion(Build.VERSION.RELEASE);
        this.monitor.setTimestamp(System.currentTimeMillis());
        this.monitor.setUuid(DeviceIdUtil.getDeviceId(mCotext));
        this.monitor.setDevice(Build.MODEL);
        NetUtil.getClientIp(monitor);

        PackageManager packageManager = mCotext.getPackageManager();

        int version = -1;
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(mCotext.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this.monitor.setUa(mCotext.getPackageName() + ":" + version);
        this.monitor.setNodeType("streaming_media_android_player");
//        this.monitor.setNodeType("streaming_media_ios_player");
        this.netSpeed = new NetSpeed(mCotext);
    }

    public void firstPacket() {
        this.firstPacketTime = System.currentTimeMillis();
        this.monitor.setConnectTime(firstPacketTime - startTime);
        this.monitor.setFirstPackageTime(firstPacketTime - startTime);
    }

    public void setPlayUrl(String url) {

        this.monitor.setUrl(url);
    }

    public void setConnectTime() {
//        this.monitor.setConnectTime();
    }

    public void setFirstPlayState(int state) {
        this.monitor.setFirstPlayState(state);
    }

    public void BufferStart() {
        this.bufferStartTime = System.currentTimeMillis();
    }

    public void BufferEnd() {
        if (bufferStartTime != 0) {
            this.bufferEndTime = System.currentTimeMillis();
            int bufferTime = (int) (bufferEndTime - bufferStartTime);
            Monitor.ReconnectTime reconnectTime = new Monitor.ReconnectTime();
            reconnectTime.setReconnectBegin(bufferStartTime);
            reconnectTime.setReconnectWaiting(bufferTime);
            bufferLength += bufferTime;
            this.monitor.getReconnectTime().add(reconnectTime);
        }
    }

    public void errorDate(String des) {
        Monitor.ErrorData errorData = new Monitor.ErrorData();
        errorData.setErrorTime(System.currentTimeMillis());
        errorData.setErrorDes(des);
        this.monitor.getErrorData().add(errorData);

    }

    public void setVideoSize(int height, int width) {
        Monitor.VideoSize videoSize = new Monitor.VideoSize();
        videoSize.setHeigth(height);
        videoSize.setWidth(width);
        this.monitor.setVideoSize(videoSize);
    }

    public void endRecode() {
        this.monitor.setDownloadTime(System.currentTimeMillis() - startTime);
        this.monitor.setPlayDuration(System.currentTimeMillis() - startTime);
        this.monitor.setDownloadSize(netSpeed.getDateSize());
        this.monitor.setBufferLength(bufferLength);
        postRecord();
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(new FileOutputStream(tempFile));
            objectOutput.writeObject(monitorList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postRecord() {

        if (this.monitorList != null && monitorList.size() > POSTCOUNT) {
            NetUtil.postMonitor(monitorList);
            this.monitorList.clear();
        }
    }

}
