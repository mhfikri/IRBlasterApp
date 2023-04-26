package com.example.irblaster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class IRBlasterHolder extends RecyclerView.ViewHolder {
    private final TextView mDeviceName;
    private final TextView mTemperature;
    private final TextView mHumidity;

    public IRBlasterHolder(@NonNull View itemView) {
        super(itemView);
        mDeviceName = itemView.findViewById(R.id.text_device_name);
        mTemperature = itemView.findViewById(R.id.temperature_text);
        mHumidity = itemView.findViewById(R.id.humidity_text);
    }

    public void bind(@NonNull AbstractIRBlaster irBlaster) {
        setName(irBlaster.getName());
        setTemperature(String.format("%1$s°C", irBlaster.getTemperature()));
        setHumidity(String.format("%1$s%%", irBlaster.getHumidity()));
    }

    private void setName(@Nullable String name) {
        mDeviceName.setText(name);
    }

    private void setTemperature(@Nullable String temperature) {
        mTemperature.setText(temperature);
    }

    private void setHumidity(@Nullable String humidity) {
        mHumidity.setText(humidity);
    }
}
