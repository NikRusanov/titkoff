package com.rusanov.titkoff.configuration;


public interface ConfigurationControllerMBean {
    int getTimeClearCandlesInterval();
    int getCandlesCount();
    double getSensibility();

    void setTimeClearCandlesInterval(int minutes);
    void setSensibility(double percents);
    void setCandlesCount(int count);

}
