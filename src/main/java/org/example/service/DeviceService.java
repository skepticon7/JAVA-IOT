package org.example.service;

import org.example.dao.repository.IDeviceDAO;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.model.AirQualitySensor;
import org.example.model.Device;
import org.example.model.HumiditySensor;
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

    public TemperatureSensor getTemperatureSensorById(Long id) throws ClassNotFoundException {
        return deviceDAO.findTemperatureSensorById(id).orElseThrow(
                () -> new ClassNotFoundException("temperature sensor with id : " + id + " not found")
        );
    }

    public List<TemperatureSensor> getAllTemperatureSensors() {
        return deviceDAO.findAllTemperatureSensors();
    }

    // Humidity Sensor methods
    public HumiditySensor saveHumiditySensor(String name, String status) {
        try {
            HumiditySensor newHumiditySensor = new HumiditySensor(name, DeviceType.HUMIDITY, Status.valueOf(status));
            deviceDAO.save(newHumiditySensor);
            return newHumiditySensor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateHumiditySensor(Long id, String name, String status) throws ClassNotFoundException {
        Optional<HumiditySensor> humiditySensor = deviceDAO.findHumiditySensorById(id);
        if(humiditySensor.isEmpty())
            throw new ClassNotFoundException("Humidity sensor with id : " + id + " not found");
        HumiditySensor sensor = humiditySensor.get();
        if(name != null) sensor.setName(name);
        if(status != null) sensor.setStatus(Status.valueOf(status));
        deviceDAO.update(sensor);
    }

    public void deleteHumiditySensor(Long id) throws ClassNotFoundException {
        Optional<HumiditySensor> humiditySensor = deviceDAO.findHumiditySensorById(id);
        if(humiditySensor.isEmpty())
            throw new ClassNotFoundException("Humidity sensor with id : " + id + " not found");
        HumiditySensor sensor = humiditySensor.get();
        deviceDAO.delete(sensor);
    }

    public HumiditySensor getHumiditySensorById(Long id) throws ClassNotFoundException {
        return deviceDAO.findHumiditySensorById(id).orElseThrow(
                () -> new ClassNotFoundException("Humidity sensor with id : " + id + " not found")
        );
    }

    public List<HumiditySensor> getAllHumiditySensors() {
        return deviceDAO.findAllHumiditySensors();
    }

    // Air Quality Sensor methods
    public AirQualitySensor saveAirQualitySensor(String name, String status) {
        try {
            AirQualitySensor newAirQualitySensor = new AirQualitySensor(name, DeviceType.AIR_QUALITY, Status.valueOf(status));
            deviceDAO.save(newAirQualitySensor);
            return newAirQualitySensor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAirQualitySensor(Long id, String name, String status) throws ClassNotFoundException {
        Optional<AirQualitySensor> airQualitySensor = deviceDAO.findAirQualitySensorById(id);
        if(airQualitySensor.isEmpty())
            throw new ClassNotFoundException("Air Quality sensor with id : " + id + " not found");
        AirQualitySensor sensor = airQualitySensor.get();
        if(name != null) sensor.setName(name);
        if(status != null) sensor.setStatus(Status.valueOf(status));
        deviceDAO.update(sensor);
    }

    public void deleteAirQualitySensor(Long id) throws ClassNotFoundException {
        Optional<AirQualitySensor> airQualitySensor = deviceDAO.findAirQualitySensorById(id);
        if(airQualitySensor.isEmpty())
            throw new ClassNotFoundException("Air Quality sensor with id : " + id + " not found");
        AirQualitySensor sensor = airQualitySensor.get();
        deviceDAO.delete(sensor);
    }

    public AirQualitySensor getAirQualitySensorById(Long id) throws ClassNotFoundException {
        return deviceDAO.findAirQualitySensorById(id).orElseThrow(
                () -> new ClassNotFoundException("Air Quality sensor with id : " + id + " not found")
        );
    }

    public List<AirQualitySensor> getAllAirQualitySensors() {
        return deviceDAO.findAllAirQualitySensors();
    }

}
