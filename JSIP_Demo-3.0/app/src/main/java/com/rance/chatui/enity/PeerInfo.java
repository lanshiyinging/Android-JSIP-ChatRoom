package com.rance.chatui.enity;

import com.rance.chatui.util.Constants;

public class PeerInfo {

    private String username;
    private String ip;
    private String port = "5080";
    private String sipAddress;
    private int status = Constants.ON_LINE;

    public PeerInfo(String sipAddress, int status){
        this.sipAddress = sipAddress;
        this.status = status;
        if(sipAddress.split(":").length > 2) {
            port = sipAddress.split(":")[2];
            ip = sipAddress.split(":")[1];
            ip = ip.substring(ip.indexOf("@") + 1);
        }else{
            port = "5060";
            ip	= sipAddress.substring(sipAddress.indexOf("@") + 1);
        }
        username = sipAddress.substring(sipAddress.indexOf(":") + 1, sipAddress.indexOf("@"));
    }

    public PeerInfo(String ip, String port, String username, int status){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getSipAddress() {
        return sipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSipAddress(String sipAddress) {
        this.sipAddress = sipAddress;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
