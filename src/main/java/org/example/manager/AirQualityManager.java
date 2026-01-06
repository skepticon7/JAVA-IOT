package org.example.manager;

import org.example.model.AirQualitySensor;
import org.example.mqtt.MqttClientProvider;
import org.example.runners.AirQualitySensorRunner;
import org.example.runners.DeviceRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AirQualityManager {

    private final Map<Long, AirQualitySensorRunner> sensors = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public synchronized void addSensor(AirQualitySensor sensor) {
        AirQualitySensorRunner sensorRunner = new AirQualitySensorRunner(sensor, MqttClientProvider.getMqttClient());
        if(!sensors.containsKey(sensor.getId())) {
            sensors.put(sensor.getId(), sensorRunner);
            executorService.submit(sensorRunner);
        }
    }


    public synchronized void removeSensor(long id) {
        DeviceRunner sensor = sensors.remove(id);
        if (sensor != null) {
            sensor.stop();
        }
    }

    public synchronized void stopAll() {
        sensors.values().forEach(DeviceRunner::stop);
        executorService.shutdownNow();
    }

    public synchronized List<Long> getAllSensorIds() {
        return new ArrayList<>(sensors.keySet());
    }
}
