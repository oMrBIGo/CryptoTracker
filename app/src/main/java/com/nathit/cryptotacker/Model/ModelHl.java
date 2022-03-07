package com.nathit.cryptotacker.Model;

public class ModelHl {

    String cId;
    String ip;
    String time;

    public ModelHl() {
    }

    public ModelHl(String cId, String ip, String time) {
        this.cId = cId;
        this.ip = ip;
        this.time = time;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
