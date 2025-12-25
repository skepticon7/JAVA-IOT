package org.example.dao.repository;

import org.example.model.Reading;

import java.util.List;

public interface IReadingDAO {
    void save(Reading reading);
    List<Reading> findAllReadings();
    List<Reading> findDeviceAllReadings(Long id);
}
