package org.example.util;

import org.example.model.Reading;

public interface TemperatureListener {
     void onTemperatureReceived(Reading reading);
}
