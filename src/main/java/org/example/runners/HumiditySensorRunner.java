package org.example.runners;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.example.enums.Status;
import org.example.model.HumiditySensor;
import org.example.util.ReadersGenerators;

public class HumiditySensorRunner extends DeviceRunner {

    private final HumiditySensor humiditySensor;

    public HumiditySensorRunner(HumiditySensor humiditySensor, MqttClient mqttClient) {
        super(humiditySensor, mqttClient);
        this.humiditySensor = humiditySensor;
    }

    public HumiditySensor getHumiditySensor() {
        return humiditySensor;
    }

    @Override
    public double generateValue() {
        while(this.humiditySensor.getStatus().equals(Status.ACTIVE))
            return ReadersGenerators.humidityGenerator.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.humidityReader(this).read());
    }
}
