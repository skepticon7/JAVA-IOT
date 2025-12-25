package org.example.dao.repository;

import org.example.model.AirQualitySensor;
import org.example.model.Device;
import org.example.model.HumiditySensor;
import org.example.model.TemperatureSensor;

import java.util.List;
import java.util.Optional;

public interface IDeviceDAO {

    void save(Device device);

    void update(Device device);

    void delete(Device device);

    Optional<Device> findById(Long id);

    List<Device> findAll();

    List<TemperatureSensor> findAllTemperatureSensors();

    List<HumiditySensor> findAllHumiditySensors();

    List<AirQualitySensor> findAllAirQualitySensors();

    Optional<TemperatureSensor> findTemperatureSensorById(Long id);

    Optional<HumiditySensor> findHumiditySensorById(Long id);

    Optional<AirQualitySensor> findAirQualitySensorById(Long id);

}
