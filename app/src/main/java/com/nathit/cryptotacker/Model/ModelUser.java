package com.nathit.cryptotacker.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelUser {

    @Exclude private  String name;
    @Exclude private  String email;

    @Keep
    public ModelUser() {
    }

    @Keep
    public ModelUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Keep
    public String getName() {
        return name;
    }

    @Keep
    public void setName(String name) {
        this.name = name;
    }

    @Keep
    public String getEmail() {
        return email;
    }

    @Keep
    public void setEmail(String email) {
        this.email = email;
    }
}
