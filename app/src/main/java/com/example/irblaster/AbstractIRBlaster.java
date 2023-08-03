package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class AbstractIRBlaster {
    @NonNull
    public abstract String getId();

    public abstract void setId(@NonNull String id);

    @Nullable
    public abstract String getName();

    public abstract void setName(@Nullable String name);

    @Nullable
    public abstract String getTemperature();

    public abstract void setTemperature(@Nullable String temperature);

    @Nullable
    public abstract String getHumidity();

    public abstract void setHumidity(@Nullable String humidity);

    @Nullable
    public abstract String getAppVersion();

    public abstract void setAppVersion(@Nullable String appVersion);
}
