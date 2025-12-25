package org.example.runners;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.Status;
import org.example.model.TemperatureSensor;
import org.example.util.ReadersGenerators;

public class TemperatureSensorRunner extends DeviceRunner{

    private final TemperatureSensor temperatureSensor;

    public TemperatureSensorRunner(TemperatureSensor temperatureSensor , MqttClient mqttClient) {
        super(temperatureSensor , mqttClient);
        this.temperatureSensor = temperatureSensor;
    }

    public TemperatureSensor getTemperatureSensor() {
        return temperatureSensor;
    }

    @Override
    public double generateValue() {
        while(this.temperatureSensor.getStatus().equals(Status.ACTIVE))
            return ReadersGenerators.temperatureGenerator.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.temperatureReader(this).read());
    }
}
