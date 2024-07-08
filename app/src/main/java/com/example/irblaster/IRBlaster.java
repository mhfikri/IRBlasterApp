package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IRBlaster extends AbstractIRBlaster {
    private String deviceId;
    private String uuid;
    private String deviceName;
    private String temperature;
    private String humidity;
    private String firmwareVersion;

    public IRBlaster() {

    }

    public IRBlaster(@NonNull String deviceId,
                     @Nullable String deviceName,
                     @Nullable String temperature,
                     @Nullable String humidity,
                     @Nullable String firmwareVersion) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.firmwareVersion = firmwareVersion;
    }

    @NonNull
    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(@NonNull String deviceId) {
        this.deviceId = deviceId;
    }

    @Nullable
    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public void setDeviceName(@Nullable String deviceName) {
        this.deviceName = deviceName;
    }

    @Nullable
    @Override
    public String getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(@Nullable String temperature) {
        this.temperature = temperature;
    }

    @Nullable
    @Override
    public String getHumidity() {
        return humidity;
    }

    @Override
    public void setHumidity(@Nullable String humidity) {
        this.humidity = humidity;
    }

    @Nullable
    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public void setFirmwareVersion(@Nullable String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @NonNull
    public String toString() {
        return "Device{" +
                "id='" + deviceId + '\'' +
                ",name='" + deviceName + '\'' +
                ",appVersion=" + firmwareVersion +
                ",temperature=" + temperature +
                ",humidity=" + humidity +
                '}';
    }
}
