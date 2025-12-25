package org.example.util;

import org.example.enums.DeviceType;

import java.time.Instant;
import java.time.LocalDateTime;

public class SensorTelemetry {
    private long deviceId;
    private String type;
    private double value;
    private LocalDateTime timestamp;

    public SensorTelemetry() {} // needed for Jackson

    public SensorTelemetry(long deviceId, String type , double value, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
