package org.example.coordinators;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.example.manager.AirQualityManager;
import org.example.manager.HumidityManager;
import org.example.manager.TemperatureManager;
import org.example.model.AirQualitySensor;
import org.example.model.HumiditySensor;
import org.example.model.TemperatureSensor;
import org.example.runners.TemperatureSensorRunner;
import org.example.service.AirQuality.AirQualityReadingService;
import org.example.service.DeviceService;
import org.example.service.Humidity.HumidityReadingService;
import org.example.service.ReadingService;
import org.example.service.Temperature.TemperatureReadingService;

import java.sql.SQLException;
import java.util.List;

public class DeviceSensorCoordinator {
    private final DeviceService deviceService;
    private final ReadingService readingService;
    private final TemperatureManager temperatureManager;
    private final HumidityManager humidityManager;
    private final AirQualityManager airQualityManager;
    private final TemperatureReadingService temperatureReadingService;
    private final HumidityReadingService humidityReadingService;
    private final AirQualityReadingService airQualityReadingService;

    public ReadingService getReadingService() {
        return readingService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public TemperatureManager getTemperatureManager() {
        return temperatureManager;
    }

    public HumidityManager getHumidityManager() {
        return humidityManager;
    }

    public AirQualityManager getAirQualityManager() {
        return airQualityManager;
    }

    public TemperatureReadingService getTemperatureReadingService() {
        return temperatureReadingService;
    }

    public HumidityReadingService getHumidityReadingService() {
        return humidityReadingService;
    }

    public AirQualityReadingService getAirQualityReadingService() {
        return airQualityReadingService;
    }

    public DeviceSensorCoordinator(DeviceService deviceService, ReadingService readingService,
                                   TemperatureManager temperatureManager, HumidityManager humidityManager,
                                   AirQualityManager airQualityManager,
                                   TemperatureReadingService temperatureReadingService,
                                   HumidityReadingService humidityReadingService,
                                   AirQualityReadingService airQualityReadingService) {
        this.deviceService = deviceService;
        this.readingService = readingService;
        this.temperatureManager = temperatureManager;
        this.humidityManager = humidityManager;
        this.airQualityManager = airQualityManager;
        this.temperatureReadingService = temperatureReadingService;
        this.humidityReadingService = humidityReadingService;
        this.airQualityReadingService = airQualityReadingService;
    }

    public void start(List<TemperatureSensor> temperatureSensors,
                     List<HumiditySensor> humiditySensors,
                     List<AirQualitySensor> airQualitySensors) throws SQLException, MqttException {
        temperatureReadingService.start();
        humidityReadingService.start();
        airQualityReadingService.start();

        temperatureSensors.forEach(temperatureManager::addSensor);
        humiditySensors.forEach(humidityManager::addSensor);
        airQualitySensors.forEach(airQualityManager::addSensor);
    }

}
