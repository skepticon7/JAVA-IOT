package org.example.runners;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.Status;
import org.example.model.AirQualitySensor;
import org.example.util.ReadersGenerators;

public class AirQualitySensorRunner extends DeviceRunner {

    private final AirQualitySensor airQualitySensor;

    public AirQualitySensorRunner(AirQualitySensor airQualitySensor, MqttClient mqttClient) {
        super(airQualitySensor, mqttClient);
        this.airQualitySensor = airQualitySensor;
    }

    public AirQualitySensor getAirQualitySensor() {
        return airQualitySensor;
    }

    @Override
    public double generateValue() {
        while(this.airQualitySensor.getStatus().equals(Status.ACTIVE))
            return ReadersGenerators.airQualitySensor.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.airQualityReader(this).read());
    }
}
