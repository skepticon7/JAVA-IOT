package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.manager.TemperatureManager;
import org.example.model.Device;
import org.example.model.TemperatureSensor;
import org.example.mqtt.MqttClientProvider;
import org.example.service.Temperature.TemperatureReadingService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws MqttException {

        MqttClient mqttClient = MqttClientProvider.getMqttClient();
        if (!mqttClient.isConnected()) {
            mqttClient.connect();
        }

        TemperatureReadingService readingService = new TemperatureReadingService(mqttClient);

        readingService.start();

        TemperatureManager manager = new TemperatureManager();

        for (long i = 1 ; i <= 4; i++) {
            TemperatureSensor sensor = new TemperatureSensor(
                    i,
                    "TempSensor-"+i,
                    DeviceType.TEMPERATURE,
                    Status.FUNCTIONAL,
                    mqttClient
            );
            manager.addSensor(sensor);
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        manager.stopAll();


    }
}