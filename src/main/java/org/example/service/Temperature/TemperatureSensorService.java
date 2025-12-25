//package org.example.service.Temperature;
//
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.example.dao.repository.ITemperatureSensorDAO;
//import org.example.enums.DeviceType;
//import org.example.enums.Status;
//import org.example.model.Device;
//import org.example.model.TemperatureSensor;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class TemperatureSensorService {
//
//    private final ITemperatureSensorDAO temperatureSensorDAO;
//
//    public TemperatureSensorService(ITemperatureSensorDAO temperatureSensorDAO) {
//        this.temperatureSensorDAO = temperatureSensorDAO;
//    }
//
//    public TemperatureSensor addTemperatureSensor(String name , DeviceType type ,  Status status , MqttClient mqttClient) throws SQLException {
//        TemperatureSensor temperatureSensor = new TemperatureSensor(name , type , status , mqttClient);
//        return temperatureSensorDAO.addTemperatureSensor(temperatureSensor);
//    }
//
//    public List<TemperatureSensor> getTemperatureSensors() throws SQLException {
//        return temperatureSensorDAO.findAllTemperatureSensors();
//    }
//
//
//}
