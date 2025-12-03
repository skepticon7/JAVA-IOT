package org.example.model;

import org.example.util.ReadersGenerators;

public class HumiditySensor extends Device{

    public HumiditySensor(long id , String name , boolean isActive) {
        super(id, name, isActive);
    }

    public HumiditySensor(){
        super();
    }

    @Override
    public double generateValue() {
        while(this.isActive())
            return ReadersGenerators.humidityGenerator.generate();
        return 0;
    }

    @Override
    public void readValue() {
        System.out.println(ReadersGenerators.humidityReader(this).read());;
    }


}
