package com.upyun.upplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Monitor implements Serializable {

    @SerializedName("player_version")
    @Expose
    private String playerVersion;
    @SerializedName("first_package_time")
    @Expose
    private Long firstPackageTime;
    @SerializedName("server_name")
    @Expose
    private String serverName;
    @SerializedName("server_pid")
    @Expose
    private String serverPid;
    @SerializedName("server_cid")
    @Expose
    private String serverCid;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("node_type")
    @Expose
    private String nodeType;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("network_type")
    @Expose
    private String networkType;
    @SerializedName("ua")
    @Expose
    private String ua;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("download_size")
    @Expose
    private Long downloadSize;
    @SerializedName("play_duration")
    @Expose
    private Long playDuration;
    @SerializedName("buffer_length")
    @Expose
    private Integer bufferLength;
    @SerializedName("error_data")
    @Expose
    private List<ErrorData> errorData = new ArrayList<ErrorData>();
    @SerializedName("first_buffering_time")
    @Expose
    private Long firstBufferingTime;
    @SerializedName("protocol")
    @Expose
    private String protocol;
    @SerializedName("download_time")
    @Expose
    private Long downloadTime;
    @SerializedName("server_ip")
    @Expose
    private String serverIp;
    @SerializedName("@timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("connect_time")
    @Expose
    private Long connectTime;
    @SerializedName("reconnect_time")
    @Expose
    private List<ReconnectTime> reconnectTime = new ArrayList<ReconnectTime>();
    @SerializedName("isp")
    @Expose
    private String isp;
    @SerializedName("video_size")
    @Expose
    private VideoSize videoSize;
    @SerializedName("first_play_state")
    @Expose
    private Integer firstPlayState;
    @SerializedName("os_version")
    @Expose
    private String osVersion;
    @SerializedName("client_ip")
    @Expose
    private String clientIp;
    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("carrier_name")
    @Expose
    private String carrierName;
    @SerializedName("failure_rate")
    @Expose
    private Double failureRate;

    /**
     * @return The playerVersion
     */
    public String getPlayerVersion() {
        return playerVersion;
    }

    /**
     * @param playerVersion The player_version
     */
    public void setPlayerVersion(String playerVersion) {
        this.playerVersion = playerVersion;
    }

    /**
     * @return The firstPackageTime
     */
    public Long getFirstPackageTime() {
        return firstPackageTime;
    }

    /**
     * @param firstPackageTime The first_package_time
     */
    public void setFirstPackageTime(Long firstPackageTime) {
        this.firstPackageTime = firstPackageTime;
    }

    /**
     * @return The serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName The server_name
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return The serverPid
     */
    public String getServerPid() {
        return serverPid;
    }

    /**
     * @param serverPid The server_pid
     */
    public void setServerPid(String serverPid) {
        this.serverPid = serverPid;
    }

    /**
     * @return The serverCid
     */
    public String getServerCid() {
        return serverCid;
    }

    /**
     * @param serverCid The server_cid
     */
    public void setServerCid(String serverCid) {
        this.serverCid = serverCid;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The nodeType
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType The node_type
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province The province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return The networkType
     */
    public String getNetworkType() {
        return networkType;
    }

    /**
     * @param networkType The network_type
     */
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    /**
     * @return The ua
     */
    public String getUa() {
        return ua;
    }

    /**
     * @param ua The ua
     */
    public void setUa(String ua) {
        this.ua = ua;
    }

    /**
     * @return The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The downloadSize
     */
    public Long getDownloadSize() {
        return downloadSize;
    }

    /**
     * @param downloadSize The download_size
     */
    public void setDownloadSize(Long downloadSize) {
        this.downloadSize = downloadSize;
    }

    /**
     * @return The playDuration
     */
    public Long getPlayDuration() {
        return playDuration;
    }

    /**
     * @param playDuration The play_duration
     */
    public void setPlayDuration(Long playDuration) {
        this.playDuration = playDuration;
    }

    /**
     * @return The bufferLength
     */
    public Integer getBufferLength() {
        return bufferLength;
    }

    /**
     * @param bufferLength The buffer_length
     */
    public void setBufferLength(Integer bufferLength) {
        this.bufferLength = bufferLength;
    }

    /**
     * @return The errorData
     */
    public List<ErrorData> getErrorData() {
        return errorData;
    }

    /**
     * @param errorData The error_data
     */
    public void setErrorData(List<ErrorData> errorData) {
        this.errorData = errorData;
    }

    /**
     * @return The firstBufferingTime
     */
    public Long getFirstBufferingTime() {
        return firstBufferingTime;
    }

    /**
     * @param firstBufferingTime The first_buffering_time
     */
    public void setFirstBufferingTime(Long firstBufferingTime) {
        this.firstBufferingTime = firstBufferingTime;
    }

    /**
     * @return The protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol The protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return The downloadTime
     */
    public Long getDownloadTime() {
        return downloadTime;
    }

    /**
     * @param downloadTime The download_time
     */
    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }

    /**
     * @return The serverIp
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * @param serverIp The server_ip
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * @return The timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp The @timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return The connectTime
     */
    public Long getConnectTime() {
        return connectTime;
    }

    /**
     * @param connectTime The connect_time
     */
    public void setConnectTime(Long connectTime) {
        this.connectTime = connectTime;
    }

    /**
     * @return The reconnectTime
     */
    public List<ReconnectTime> getReconnectTime() {
        return reconnectTime;
    }

    /**
     * @param reconnectTime The reconnect_time
     */
    public void setReconnectTime(List<ReconnectTime> reconnectTime) {
        this.reconnectTime = reconnectTime;
    }

    /**
     * @return The isp
     */
    public String getIsp() {
        return isp;
    }

    /**
     * @param isp The isp
     */
    public void setIsp(String isp) {
        this.isp = isp;
    }

    /**
     * @return The videoSize
     */
    public VideoSize getVideoSize() {
        return videoSize;
    }

    /**
     * @param videoSize The video_size
     */
    public void setVideoSize(VideoSize videoSize) {
        this.videoSize = videoSize;
    }

    /**
     * @return The firstPlayState
     */
    public Integer getFirstPlayState() {
        return firstPlayState;
    }

    /**
     * @param firstPlayState The first_play_state
     */
    public void setFirstPlayState(Integer firstPlayState) {
        this.firstPlayState = firstPlayState;
    }

    /**
     * @return The osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion The os_version
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return The clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp The client_ip
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * @return The device
     */
    public String getDevice() {
        return device;
    }

    /**
     * @param device The device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * @return The carrierName
     */
    public String getCarrierName() {
        return carrierName;
    }

    /**
     * @param carrierName The carrier_name
     */
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }


    /**
     * @return The failureRate
     */
    public Double getFailureRate() {
        return failureRate;
    }

    /**
     * @param failureRate The failure_rate
     */
    public void setFailureRate(Double failureRate) {
        this.failureRate = failureRate;
    }

    public static class VideoSize implements Serializable {

        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("heigth")
        @Expose
        private Integer heigth;

        /**
         * @return The width
         */
        public Integer getWidth() {
            return width;
        }

        /**
         * @param width The width
         */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /**
         * @return The heigth
         */
        public Integer getHeigth() {
            return heigth;
        }

        /**
         * @param heigth The heigth
         */
        public void setHeigth(Integer heigth) {
            this.heigth = heigth;
        }
    }

    public static class ReconnectTime implements Serializable {

        @SerializedName("reconnect_begin")
        @Expose
        private Long reconnectBegin;
        @SerializedName("reconnect_waiting")
        @Expose
        private Integer reconnectWaiting;

        /**
         * @return The reconnectBegin
         */
        public Long getReconnectBegin() {
            return reconnectBegin;
        }

        /**
         * @param reconnectBegin The reconnect_begin
         */
        public void setReconnectBegin(Long reconnectBegin) {
            this.reconnectBegin = reconnectBegin;
        }

        /**
         * @return The reconnectWaiting
         */
        public Integer getReconnectWaiting() {
            return reconnectWaiting;
        }

        /**
         * @param reconnectWaiting The reconnect_waiting
         */
        public void setReconnectWaiting(Integer reconnectWaiting) {
            this.reconnectWaiting = reconnectWaiting;
        }

    }

    public static class ErrorData implements Serializable {

        @SerializedName("error_time")
        @Expose
        private Long errorTime;
        @SerializedName("error_des")
        @Expose
        private String errorDes;

        /**
         * @return The errorTime
         */
        public Long getErrorTime() {
            return errorTime;
        }

        /**
         * @param errorTime The error_time
         */
        public void setErrorTime(Long errorTime) {
            this.errorTime = errorTime;
        }

        /**
         * @return The errorDes
         */
        public String getErrorDes() {
            return errorDes;
        }

        /**
         * @param errorDes The error_des
         */
        public void setErrorDes(String errorDes) {
            this.errorDes = errorDes;
        }

    }
}
