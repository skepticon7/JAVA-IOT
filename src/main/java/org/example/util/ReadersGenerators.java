package org.example.util;

import org.example.model.Device;

public class ReadersGenerators {
    public static final ISensorGenerator temperatureGenerator = () -> 18 + Math.random() * 14;
    public static final ISensorGenerator humidityGenerator = () -> 24 + Math.random() * 100;
    public static final ISensorGenerator airQualitySensor = () -> 350 + Math.random() * 200;
    public static ISensorReader temperatureReader(Device device) {
        return  () -> "Temperature sensor id :" + device.getId() + " name : " + device.getName() + " , data : %s" + device.generateValue();
    };
    public static ISensorReader humidityReader(Device device) {
        return  () -> "Humididty sensor id :" + device.getId() + " name : " + device.getName() + " , data : %s" + device.generateValue();
    }
    public static ISensorReader airQualityReader(Device device) {
        return  () -> "Air quality sensor id :" + device.getId() + " name : " + device.getName() + " , data : %s" + device.generateValue();
    }

}
