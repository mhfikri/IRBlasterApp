package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IRBlaster extends AbstractIRBlaster {
    private String mId;
    private String mName;
    private String mTemperature;
    private String mHumidity;

    public IRBlaster() {

    }

    public IRBlaster(@NonNull String id, @Nullable String name, @Nullable String temperature, @Nullable String humidity) {
        mId = id;
        mName = name;
        mTemperature = temperature;
        mHumidity = humidity;
    }

    @NonNull
    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(@NonNull String id) {
        mId = id;
    }

    @Nullable
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(@Nullable String name) {
        mName = name;
    }

    @Nullable
    @Override
    public String getTemperature() {
        return mTemperature;
    }

    @Override
    public void setTemperature(@Nullable String temperature) {
        mTemperature = temperature;
    }

    @Nullable
    @Override
    public String getHumidity() {
        return mHumidity;
    }

    @Override
    public void setHumidity(@Nullable String humidity) {
        mHumidity = humidity;
    }

    @NonNull
    public String toString() {
        return "Device{" +
                "mId='" + mId + '\'' +
                ",mName='" + mName + '\'' +
                ",mTemperature=" + mTemperature +
                ",mHumidity=" + mHumidity +
                '}';
    }
}
