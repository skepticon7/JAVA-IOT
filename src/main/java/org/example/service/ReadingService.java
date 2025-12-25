package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dao.repository.IDeviceDAO;
import org.example.dao.repository.IReadingDAO;
import org.example.model.Device;
import org.example.model.Reading;
import org.example.util.TemperatureListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ReadingService {

    private final IReadingDAO readingDAO;
    private final IDeviceDAO deviceDAO;
    private final List<TemperatureListener> listeners = new ArrayList<>();

    public ReadingService(IReadingDAO readingDAO, IDeviceDAO deviceDAO) {
        this.readingDAO = readingDAO;
        this.deviceDAO = deviceDAO;
    }

    @Transactional
    public void createReading(Long deviceId , double value , LocalDateTime timestamp) throws ClassNotFoundException {
        Device device = deviceDAO.findById(deviceId).orElseThrow(
                () -> new ClassNotFoundException("device with id : " + deviceId + " not found")
        );
        Reading newReading = new Reading(device , value , timestamp);
        readingDAO.save(newReading);

        listeners.forEach(listener -> listener.onTemperatureReceived(newReading));

    }

    public void addListener(TemperatureListener listener) {
        listeners.add(listener);
    }

    public List<Reading> getAllReadings() {
        return readingDAO.findAllReadings();
    }

    public List<Reading> getAllDeviceReadings(Long id) throws ClassNotFoundException {
        deviceDAO.findById(id).orElseThrow(
                () -> new ClassNotFoundException("device with id : " + id + " not found")
                );
        return readingDAO.findDeviceAllReadings(id);
    }

}
