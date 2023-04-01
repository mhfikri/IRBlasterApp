package com.example.irblaster;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IRBlasterHolder extends RecyclerView.ViewHolder {
    private final TextView mDeviceName;
    private final TextView mSensorTemp;
    private final TextView mSensorHumidity;

    public IRBlasterHolder(@NonNull View itemView) {
        super(itemView);
        mDeviceName = itemView.findViewById(R.id.text_device_name);
        mSensorTemp = itemView.findViewById(R.id.text_sensor_temp);
        mSensorHumidity = itemView.findViewById(R.id.text_sensor_humidity);
    }

    public void bind(@NonNull AbstractIRBlaster irBlaster) {
        mDeviceName.setText(irBlaster.getName());
        mSensorTemp.setText(irBlaster.getTemperature());
        mSensorHumidity.setText(irBlaster.getHumidity());
    }
}
