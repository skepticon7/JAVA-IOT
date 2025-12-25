package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.util.ReadersGenerators;

@Entity
@DiscriminatorValue("AIR_QUALITY")
public class AirQualitySensor extends Device {
    protected AirQualitySensor() {
        super();
    }

    public AirQualitySensor(String name, DeviceType type, Status status) {
        super(name, DeviceType.AIR_QUALITY , status);
    }
}
