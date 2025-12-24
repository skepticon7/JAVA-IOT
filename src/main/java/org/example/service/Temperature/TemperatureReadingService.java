package org.example.service.Temperature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.example.util.SensorTelemetry;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

public class TemperatureReadingService implements MqttCallback {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    public TemperatureReadingService(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        this.objectMapper = mapper;
        mqttClient.setCallback(this);
    }

    public void start() throws MqttException {
        if (!mqttClient.isConnected()) {
            mqttClient.connect();
        }
        System.out.println("subscribing");
        mqttClient.subscribe("iot/TEMPERATURE/+/telemetry");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("MQTT Connection Lost: " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String payload = new String(mqttMessage.getPayload());
        try {
            SensorTelemetry sensorTelemetry = objectMapper.readValue(payload, SensorTelemetry.class);
            System.out.printf("reading from sensor %d: %.2f%n",
                    sensorTelemetry.getDeviceId(), sensorTelemetry.getValue());
        } catch (Exception e) {
            System.err.println("Failed to parse telemetry: " + payload);
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
}