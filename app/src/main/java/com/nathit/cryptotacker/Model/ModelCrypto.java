package com.nathit.cryptotacker.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelCrypto {


    @Exclude private String name;
    @Exclude private String symbol;
    @Exclude private double price;


    @Keep
    public ModelCrypto(String name, String symbol, double price) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
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
    public String getSymbol() {
        return symbol;
    }

    @Keep
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Keep
    public double getPrice() {
        return price;
    }

    @Keep
    public void setPrice(double price) {
        this.price = price;
    }
}
