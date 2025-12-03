package org.example.model;

import org.example.util.ReadersGenerators;

public class AirQualitySensor extends Device{


    public AirQualitySensor(long id , String name , boolean isActive) {
        super(id , name , isActive);
    }

    @Override
    public double generateValue() {
        while(this.isActive())
            return ReadersGenerators.airQualitySensor.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.airQualityReader(this).read());;
    }
}
