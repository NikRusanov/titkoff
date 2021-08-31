package com.rusanov.titkoff.configuration;


public interface ConfigurationControllerMBean {

    int getTimeClearCandlesInterval();

    int getCandlesCount();

    double getSensibility();

    double getStockMinimumPrice();

    void setTimeClearCandlesInterval(int minutes);

    void setSensibility(double percents);

    void setCandlesCount(int count);

    void setStockMinimumPrice(double minimumPrice);

    double getStockMaximumPrice();

    void setStockMaximumPrice(double stockMaximumPrice);
}
