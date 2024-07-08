package com.example.irblaster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class IRBlasterHolder extends RecyclerView.ViewHolder {
    private final TextView deviceName;
    private final TextView temperature;
    private final TextView humidity;

    public IRBlasterHolder(@NonNull View itemView) {
        super(itemView);
        deviceName = itemView.findViewById(R.id.text_device_name);
        temperature = itemView.findViewById(R.id.temperature_text);
        humidity = itemView.findViewById(R.id.humidity_text);
    }

    public void bind(@NonNull AbstractIRBlaster irBlaster) {
        setName(irBlaster.getDeviceName());
        setTemperature(String.format("%1$s°C", irBlaster.getTemperature()));
        setHumidity(String.format("%1$s%%", irBlaster.getHumidity()));
    }

    private void setName(@Nullable String name) {
        deviceName.setText(name);
    }

    private void setTemperature(@Nullable String temperature) {
        this.temperature.setText(temperature);
    }

    private void setHumidity(@Nullable String humidity) {
        this.humidity.setText(humidity);
    }
}
