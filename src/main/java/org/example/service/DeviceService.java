package org.example.service;

import org.example.dao.repository.IDeviceDAO;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.model.Device;
import org.example.model.TemperatureSensor;

import java.util.List;
import java.util.Optional;

public class DeviceService {

    private final IDeviceDAO deviceDAO;

    public DeviceService(IDeviceDAO deviceDAO) {
        this.deviceDAO = deviceDAO;
    }

    public TemperatureSensor saveTemperatureSensor(String name , String status) {
        try{
            TemperatureSensor newTempSensor = new TemperatureSensor(name , Status.valueOf(status));
            deviceDAO.save(newTempSensor);
            return newTempSensor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTemperatureSensor(Long id , String name , String status) throws ClassNotFoundException {
        Optional<TemperatureSensor> tmpSensor = deviceDAO.findTemperatureSensorById(id);
        if(tmpSensor.isEmpty())
            throw new ClassNotFoundException("Temperature sensor with id : " + id + "Not found");
        TemperatureSensor sensor = tmpSensor.get();
        if(name != null) sensor.setName(name);
        if(status != null) sensor.setStatus(Status.valueOf(status));
        deviceDAO.update(sensor);
    }

    public void deleteTemperatureSensor(Long id) throws ClassNotFoundException {
        Optional<TemperatureSensor> tmpSensor = deviceDAO.findTemperatureSensorById(id);
        if(tmpSensor.isEmpty())
            throw new ClassNotFoundException("Temperature sensor with id : " + id + "Not found");
        TemperatureSensor sensor = tmpSensor.get();
        deviceDAO.delete(sensor);
    }

    public Optional<TemperatureSensor> getTemperatureSensorById(Long id) {
        return deviceDAO.findTemperatureSensorById(id);
    }

    public List<TemperatureSensor> getAllTemperatureSensors() {
        return deviceDAO.findAllTemperatureSensors();
    }



}
