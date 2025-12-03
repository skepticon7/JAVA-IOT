package org.example.dao.implementation;

import org.example.configuration.DatabaseConnection;
import org.example.dao.repository.ITemperatureSensorDAO;
import org.example.model.Device;
import org.example.model.TemperatureSensor;

import java.sql.*;
import java.util.List;

public class TemperatureSensorDAO implements ITemperatureSensorDAO {

    private Connection connection;

    public TemperatureSensorDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public TemperatureSensor addTemperatureSensor(TemperatureSensor temperatureSensor) throws SQLException {
        String sql = "INSERT INTO TEMPERATURE_SENSORS (NAME, STATUS) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, temperatureSensor.getName());
            stmt.setString(2, temperatureSensor.getStatus().toString());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting device failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    temperatureSensor.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Inserting device failed, no ID obtained.");
                }
            }
        }

        return temperatureSensor;
    }


    @Override
    public Device findTemperatureSensorById(long id) throws SQLException {
        return null;
    }

    @Override
    public List<TemperatureSensor> findAllTemperatureSensors() throws SQLException {
        return List.of();
    }

    @Override
    public TemperatureSensor updateTemperatureSensor(TemperatureSensor temperatureSensor) throws SQLException {
        return null;
    }

    @Override
    public TemperatureSensor deleteTemperatureSensor(long id) throws SQLException {
        return null;
    }
}
