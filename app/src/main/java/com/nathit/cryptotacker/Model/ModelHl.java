package com.nathit.cryptotacker.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelHl {


    @Exclude private String cId;
    @Exclude private String ip;
    @Exclude private String time;

    @Keep
    public ModelHl() {
    }

    @Keep
    public ModelHl(String cId, String ip, String time) {
        this.cId = cId;
        this.ip = ip;
        this.time = time;
    }

    @Keep
    public String getcId() {
        return cId;
    }

    @Keep
    public void setcId(String cId) {
        this.cId = cId;
    }

    @Keep
    public String getIp() {
        return ip;
    }

    @Keep
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Keep
    public String getTime() {
        return time;
    }

    @Keep
    public void setTime(String time) {
        this.time = time;
    }
}
