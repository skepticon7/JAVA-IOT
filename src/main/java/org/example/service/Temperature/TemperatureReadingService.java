package org.example.service.Temperature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.example.dao.repository.IReadingDAO;
import org.example.model.Reading;
import org.example.service.ReadingService;
import org.example.util.MqttConnectionListener;
import org.example.util.SensorTelemetry;
import org.example.util.TemperatureListener;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;

public class TemperatureReadingService implements MqttCallbackExtended {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final ReadingService readingService;
    private MqttConnectionListener connectionListener;
    

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setConnectionListener(MqttConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
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
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            mqttClient.connect(options);
        }
        System.out.println("connected");
        handleConnect();
        mqttClient.subscribe("iot/TEMPERATURE/+/telemetry");
    }

    private void handleConnect() {
        if (connectionListener != null) connectionListener.onConnect();
    }

    private void handleDisconnect(Throwable cause) {
        if (connectionListener != null) connectionListener.onDisconnect(cause);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("MQTT Connection Lost: " + throwable.getMessage());
        handleDisconnect(throwable);
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

    @Override
    public void connectComplete(boolean b, String s) {
        System.out.println("MQTT Connected to " + s);
    }
}