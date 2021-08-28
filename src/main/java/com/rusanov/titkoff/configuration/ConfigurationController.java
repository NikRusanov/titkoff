package com.rusanov.titkoff.configuration;

public class ConfigurationController implements ConfigurationControllerMBean {
    private int interval = 2;
    private double sensibility = 1;
    private int candlesCount  = 1;

    @Override
    public int getTimeClearCandlesInterval() {
        return interval;
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
        this.interval = minutes;

    }

    @Override
    public void setSensibility(double percents) {
        this.sensibility = percents;
    }

    @Override
    public void setCandlesCount(int count) {
        this.candlesCount =  count;
    }
}
