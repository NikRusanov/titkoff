package com.rusanov.titkoff.configuration;

public class ConfigurationController implements ConfigurationControllerMBean {

    private int timeClearCandlesInterval = 1;

    private double sensibility = 2;

    private int candlesCount  = 5;

    private double stockMinimumPrice = 5.0;

    private double stockMaximumPrice = 125.0;


    @Override
    public int getTimeClearCandlesInterval() {
        return timeClearCandlesInterval;
    }

    @Override
    public int getCandlesCount() {
        return candlesCount;
    }

    @Override
    public double getSensibility() {
        return sensibility;
    }

    @Override
    public void setTimeClearCandlesInterval(int minutes) {
        this.timeClearCandlesInterval = minutes;

    }

    @Override
    public void setSensibility(double percents) {
        this.sensibility = percents;
    }

    @Override
    public void setCandlesCount(int count) {
        this.candlesCount =  count;
    }

    @Override
    public double getStockMinimumPrice() {
        return stockMinimumPrice;
    }

    @Override
    public void setStockMinimumPrice(double stockMinimumPrice) {
        this.stockMinimumPrice = stockMinimumPrice;
    }

    @Override
    public double getStockMaximumPrice() {
        return stockMaximumPrice;
    }

    @Override
    public void setStockMaximumPrice(double stockMaximumPrice) {
        this.stockMaximumPrice = stockMaximumPrice;
    }
}
