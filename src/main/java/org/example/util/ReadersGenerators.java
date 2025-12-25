package org.example.util;

import org.example.model.Device;
import org.example.runners.DeviceRunner;

public class ReadersGenerators {
    public static final ISensorGenerator temperatureGenerator = () -> 18 + Math.random() * 14;
    public static final ISensorGenerator humidityGenerator = () -> 24 + Math.random() * 100;
    public static final ISensorGenerator airQualitySensor = () -> 350 + Math.random() * 200;
    public static ISensorReader temperatureReader(DeviceRunner deviceRunner) {
        return  () -> "Temperature sensor id :" + deviceRunner.getDevice().getId() + " name : " + deviceRunner.getDevice().getName() + " , data : %s" + deviceRunner.generateValue();
    };
    public static ISensorReader humidityReader(DeviceRunner deviceRunner) {
        return  () -> "Humidity sensor id :" + deviceRunner.getDevice().getId() + " name : " + deviceRunner.getDevice().getName() + " , data : %s" + deviceRunner.generateValue();
    }
    public static ISensorReader airQualityReader(DeviceRunner deviceRunner) {
        return  () -> "Air Quality sensor id :" + deviceRunner.getDevice().getId() + " name : " + deviceRunner.getDevice().getName() + " , data : %s" + deviceRunner.generateValue();
    }

}
