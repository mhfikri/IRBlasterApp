package com.example.irblaster;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class IRBlasterHolder extends RecyclerView.ViewHolder {
    private final TextView mDeviceName;
    private final TextView mSensorTemperature;
    private final TextView mSensorHumidity;
    private final ImageView mTemperatureIcon;

    public IRBlasterHolder(@NonNull View itemView) {
        super(itemView);
        mDeviceName = itemView.findViewById(R.id.text_device_name);
        mSensorTemperature = itemView.findViewById(R.id.text_sensor_temperature);
        mSensorHumidity = itemView.findViewById(R.id.text_sensor_humidity);
        mTemperatureIcon = itemView.findViewById(R.id.temperature_icon);
    }

    public void bind(@NonNull AbstractIRBlaster irBlaster) {
        setName(irBlaster.getName());
        setTemperature(String.format("%1$s°C", irBlaster.getTemperature()));
        setTemperatureIcon(irBlaster.getTemperature());
        setHumidity(String.format("%1$s%%", irBlaster.getHumidity()));
    }

    private void setName(@Nullable String name) {
        mDeviceName.setText(name);
    }

    private void setTemperature(@Nullable String formattedTemperature) {
        mSensorTemperature.setText(formattedTemperature);
    }

    private void setTemperatureIcon(@Nullable String temperature) {
        if (temperature != null && Double.parseDouble(temperature) <= 26.0) {
            mTemperatureIcon.setImageResource(R.drawable.ic_temperature_cool_24dp);
        } else {
            mTemperatureIcon.setImageResource(R.drawable.ic_temperature_hot_24dp);
        }
    }

    private void setHumidity(@Nullable String humidity) {
        mSensorHumidity.setText(humidity);
    }
}
