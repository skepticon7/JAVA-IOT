package org.example.model;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.util.ReadersGenerators;

public class TemperatureSensor extends Device {

    public TemperatureSensor(long id , String name , DeviceType type , Status status , MqttClient mqttClient) {
        super(id, name, type , status , mqttClient);
    }

    public TemperatureSensor(MqttClient mqttClient , DeviceType type) {
        super(mqttClient , type);
    }

    @Override
    public double generateValue() {
        while(this.getStatus().equals(Status.FUNCTIONAL))
            return ReadersGenerators.temperatureGenerator.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.temperatureReader(this).read());
    }

    public String display() {
        return "displaying temperature sensor";
    }

}
