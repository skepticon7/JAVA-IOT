package org.example.configuration;

import org.example.model.*;

import java.util.Set;

public class EntityRegistry {
    public static final Set<Class<?>> ENTITIES = Set.of(
            Device.class,
            TemperatureSensor.class,
            HumiditySensor.class,
            AirQualitySensor.class,
            Reading.class
    );

    private EntityRegistry() {}
}
