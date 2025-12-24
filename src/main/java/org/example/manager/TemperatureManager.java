package org.example.manager;

import org.example.model.Device;
import org.example.model.TemperatureSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemperatureManager {

    private final Map<Long , TemperatureSensor> sensors = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public synchronized void addSensor(TemperatureSensor sensor) {
        if(!sensors.containsKey(sensor.getId())) {
            sensors.put(sensor.getId(), sensor);
            executorService.submit(sensor);
        }
    }

    public synchronized void removeSensor(long id) {
        Device sensor = sensors.remove(id);
        if (sensor != null) {
            sensor.stop();
        }
    }

    public synchronized void stopAll() {
        sensors.values().forEach(Device::stop);
        executorService.shutdownNow();
    }

    public synchronized List<Long> getAllSensorIds() {
        return new ArrayList<>(sensors.keySet());
    }

}
