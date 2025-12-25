package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.example.enums.DeviceType;
import org.example.enums.Status;
import org.example.util.ReadersGenerators;

@Entity
@DiscriminatorValue("HUMIDITY")
public class HumiditySensor extends Device{
    public HumiditySensor(String name, DeviceType type, Status status) {
        super(name, DeviceType.HUMIDITY , status);
    }

    protected HumiditySensor() {
        super();
    }
}
