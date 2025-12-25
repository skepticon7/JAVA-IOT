package org.example.util;

public interface MqttConnectionListener {
    void onConnect();
    void onDisconnect(Throwable message);
}
