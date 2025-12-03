package org.example.service;

import org.example.dao.repository.ITemperatureSensorDAO;
import org.example.enums.Status;
import org.example.model.Device;
import org.example.model.TemperatureSensor;

import java.sql.SQLException;

public class TemperatureSensorService {
    private final ITemperatureSensorDAO temperatureSensorDAO;

    public TemperatureSensorService(ITemperatureSensorDAO temperatureSensorDAO) {
        this.temperatureSensorDAO = temperatureSensorDAO;
    }

    public Device addTemperatureSensor(String name , String status) throws SQLException {
        TemperatureSensor temperatureSensor = new TemperatureSensor();
        temperatureSensor.setName(name);
        temperatureSensor.setStatus(Status.valueOf(status));
        return temperatureSensorDAO.addTemperatureSensor(temperatureSensor);
    }

}
