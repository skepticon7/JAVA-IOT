package org.example.dao.repository;

import org.example.model.Device;
import org.example.model.TemperatureSensor;

import java.sql.SQLException;
import java.util.List;

public interface ITemperatureSensorDAO {
    TemperatureSensor addTemperatureSensor(TemperatureSensor temperatureSensor) throws SQLException;
    Device findTemperatureSensorById(long id) throws SQLException;
    List<TemperatureSensor> findAllTemperatureSensors() throws SQLException;
    TemperatureSensor updateTemperatureSensor(TemperatureSensor temperatureSensor) throws SQLException;
    TemperatureSensor deleteTemperatureSensor(long id) throws SQLException;
}
