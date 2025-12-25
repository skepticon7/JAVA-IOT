package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.configuration.HibernateConfig;
import org.example.coordinators.DeviceSensorCoordinator;
import org.example.dao.implementation.DeviceDAO;
import org.example.dao.implementation.ReadingDAO;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.manager.TemperatureManager;
import org.example.model.Device;
import org.example.model.TemperatureSensor;
import org.example.mqtt.MqttClientProvider;
import org.example.service.DeviceService;
import org.example.service.ReadingService;
import org.example.service.Temperature.TemperatureReadingService;
import org.example.ui.SensorHubApp;

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

        HibernateConfig.getSessionFactory().openSession().clear();

        MqttClient mqttClient = MqttClientProvider.getMqttClient();

        DeviceDAO deviceDAO = new DeviceDAO();
        DeviceService deviceService = new DeviceService(deviceDAO);

        ReadingDAO readingDAO = new ReadingDAO();
        ReadingService readingService = new ReadingService(readingDAO , deviceDAO);

        TemperatureManager temperatureManager = new TemperatureManager();

        TemperatureReadingService temperatureReadingService = new TemperatureReadingService(mqttClient , readingService);



        DeviceSensorCoordinator deviceSensorCoordinator = new DeviceSensorCoordinator(
                deviceService,
                readingService,
                temperatureManager,
                temperatureReadingService
        );

        SensorHubApp.launchApp(deviceSensorCoordinator , args);

    }
}