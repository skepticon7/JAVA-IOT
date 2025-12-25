package org.example.coordinators;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.manager.TemperatureManager;
import org.example.model.TemperatureSensor;
import org.example.runners.TemperatureSensorRunner;
import org.example.service.DeviceService;
import org.example.service.ReadingService;
import org.example.service.Temperature.TemperatureReadingService;

import java.sql.SQLException;
import java.util.List;

public class DeviceSensorCoordinator {
    private final DeviceService deviceService;
    private final ReadingService readingService;
    private final TemperatureManager temperatureManager;
    private final TemperatureReadingService temperatureReadingService;

    public ReadingService getReadingService() {
        return readingService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public TemperatureManager getTemperatureManager() {
        return temperatureManager;
    }

    public TemperatureReadingService getTemperatureReadingService() {
        return temperatureReadingService;
    }

    public DeviceSensorCoordinator(DeviceService deviceService, ReadingService readingService, TemperatureManager temperatureManager, TemperatureReadingService temperatureReadingService) {
        this.deviceService = deviceService;
        this.readingService = readingService;
        this.temperatureManager = temperatureManager;
        this.temperatureReadingService = temperatureReadingService;
    }

    public void start(List<TemperatureSensor> sensors) throws SQLException, MqttException {
        temperatureReadingService.start();
        sensors.forEach(tmp -> {
            temperatureManager.addSensor(new TemperatureSensorRunner(tmp , temperatureReadingService.getMqttClient()));
        });
    }

}
