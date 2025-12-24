package org.example.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttClientProvider {

    private static MqttClient mqttClient;

    public static synchronized MqttClient getMqttClient() {
        if(mqttClient == null) {
            try{
                mqttClient = new MqttClient("tcp://localhost:1883" , MqttClient.generateClientId());
                mqttClient.connect();
            }catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        return mqttClient;
    }

}
