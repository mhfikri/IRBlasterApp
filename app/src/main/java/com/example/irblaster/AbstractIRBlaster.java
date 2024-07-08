package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AbstractIRBlaster {
    @NonNull
    public abstract String getDeviceId();

    public abstract void setDeviceId(@NonNull String deviceId);

    @Nullable
    public abstract String getDeviceName();

    public abstract void setDeviceName(@Nullable String deviceName);

    @Nullable
    public abstract String getTemperature();

    public abstract void setTemperature(@Nullable String temperature);

    @Nullable
    public abstract String getHumidity();

    public abstract void setHumidity(@Nullable String humidity);

    @Nullable
    public abstract String getFirmwareVersion();

    public abstract void setFirmwareVersion(@Nullable String firmwareVersion);
}
