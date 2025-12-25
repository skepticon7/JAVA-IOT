package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.util.ReadersGenerators;

@Entity
@DiscriminatorValue("TEMPERATURE")
public class TemperatureSensor extends Device {

    protected TemperatureSensor() {
        super();
    }

    public TemperatureSensor(String name, Status status) {
        super(name, DeviceType.TEMPERATURE, status);
    }




}
