package org.example.model;

import jdk.jshell.StatementSnippet;
import org.example.enums.Status;

import java.time.LocalDateTime;

public abstract class Device {
    private long id;
    private String name;
    private Status status;
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

    public Device(long id , String name , Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public Device() {
        this.name = "N/A";
        this.status = Status.INACTIVE;
    }

    @Override
    public String toString() {
        return "Device [id=" + id + ", name=" + name + ", status=" + status + "]";
    }

    public abstract double generateValue();

    public abstract void readValue();

}
