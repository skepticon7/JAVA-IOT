package org.example.runners;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.enums.Status;
import org.example.model.Device;

import java.time.Instant;
import java.time.LocalDateTime;

public abstract class DeviceRunner implements Runnable{

    protected final Device device;
    protected final MqttClient mqttClient;
    protected volatile boolean active = true;

    public Device getDevice() {
        return device;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public DeviceRunner(Device device, MqttClient mqttClient) {
        this.device = device;
        this.mqttClient = mqttClient;
    }


    @Override
    public void run() {
        if (device.getStatus() == Status.ACTIVE) {
            while (active && device.getStatus() == Status.ACTIVE) {
                double value = generateValue();
                publishTelemetry(value);
                sleep();
            }
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        this.active = false;
    }

    protected void publishTelemetry(double value) {
        try {
            if (mqttClient.isConnected()) {
                String topic = "iot/" + device.getType() + "/" + device.getId() + "/telemetry";
                String payload = """
                        {"deviceId": %d, "type": "%s", "value": %.2f, "timestamp": "%s"}
                        """.formatted(device.getId(), device.getType(), value, LocalDateTime.now());

                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                mqttClient.publish(topic, message);
            } else {
                System.out.println("MQTT client not connected, skipping device " + device.getId());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public abstract double generateValue();
    public abstract void readValue();
}
