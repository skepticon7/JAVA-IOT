package org.example.service.Temperature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.example.dao.repository.IReadingDAO;
import org.example.model.Reading;
import org.example.service.ReadingService;
import org.example.util.SensorTelemetry;
import org.example.util.TemperatureListener;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;

public class TemperatureReadingService implements MqttCallback {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final ReadingService readingService;
    private final List<TemperatureListener> listeners = new ArrayList<>();

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public TemperatureReadingService(MqttClient mqttClient, ReadingService readingService) {
        this.mqttClient = mqttClient;
        this.readingService = readingService;
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
            readingService.createReading(sensorTelemetry.getDeviceId() , sensorTelemetry.getValue() , sensorTelemetry.getTimestamp());

        } catch (Exception e) {
            System.err.println("Failed to parse telemetry: " + payload);
            e.printStackTrace();
        }
    }



    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
}