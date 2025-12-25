package org.example.manager;

import org.example.model.Device;
import org.example.model.TemperatureSensor;
import org.example.mqtt.MqttClientProvider;
import org.example.runners.DeviceRunner;
import org.example.runners.TemperatureSensorRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemperatureManager {

    private final Map<Long , TemperatureSensorRunner> sensors = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public synchronized void addSensor(TemperatureSensor sensor) {
        TemperatureSensorRunner sensorRunner = new TemperatureSensorRunner(sensor , MqttClientProvider.getMqttClient());
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
