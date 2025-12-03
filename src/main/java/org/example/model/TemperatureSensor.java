package org.example.model;

import org.example.enums.Status;
import org.example.util.ReadersGenerators;

public class TemperatureSensor extends Device {

    public TemperatureSensor(long id , String name , Status status) {
        super(id, name, status);
    }

    public TemperatureSensor() {
        super();
    }

    @Override
    public double generateValue() {
        while(this.getStatus().equals(Status.ACTIVE))
            return ReadersGenerators.temperatureGenerator.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.temperatureReader(this).read());
    }

    public String display() {
        return "displaying temperature sensor";
    }

}
