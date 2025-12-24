package org.example.model;

import jdk.jshell.StatementSnippet;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.enums.DeviceType;
import org.example.enums.Status;

import java.time.Instant;
import java.time.LocalDateTime;

public abstract class Device implements Runnable {
    private long id;
    private String name;
    private DeviceType type;
    private Status status;
    protected volatile boolean active = true;
    protected final MqttClient mqttClient;
    public LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Device(long id , String name , DeviceType type , Status status , MqttClient mqttClient) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.mqttClient = mqttClient;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Device(MqttClient mqttClient , DeviceType type) {
        this.mqttClient = mqttClient;
        this.name = "N/A";
        this.status = Status.FUNCTIONAL;
        this.type = type;
    }



    @Override
    public void run() {
        if(status.equals(Status.FUNCTIONAL)) {
            while(status.equals(Status.FUNCTIONAL)) {
                double value = generateValue();
                publishTelemetry(value);
                sleep();
            }
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(2000);
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
                String topic = "iot/"  + type + "/" + id + "/telemetry";
                String payload = """
                {"deviceId": %d, "type": "%s", "value": %.2f, "timestamp": "%s"}
                """.formatted(id, type, value, Instant.now());

                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                mqttClient.publish(topic, message);
            } else {
                System.out.println("MQTT client is not connected, skipping publish for device " + id);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Device [id=" + id + ", name=" + name + ", status=" + status + "]";
    }

    public abstract double generateValue();

    public abstract void readValue();

}
