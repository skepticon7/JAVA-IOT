package org.example.dao.implementation;

import org.example.configuration.HibernateConfig;
import org.example.dao.repository.IDeviceDAO;
import org.example.model.AirQualitySensor;
import org.example.model.Device;
import org.example.model.HumiditySensor;
import org.example.model.TemperatureSensor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class DeviceDAO implements IDeviceDAO {
    @Override
    public void save(Device device) {
        executeInTransaction(session -> session.persist(device));
    }

    @Override
    public void update(Device device) {
        executeInTransaction(session -> session.merge(device));
    }

    @Override
    public void delete(Device device) {
        executeInTransaction(session -> session.remove(device));
    }

    @Override
    public Optional<Device> findById(Long id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Device.class , id));
        }
    }

    @Override
    public List<Device> findAll() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            return session.createQuery("FROM Device " , Device.class).getResultList();
        }
    }

    @Override
    public List<TemperatureSensor> findAllTemperatureSensors() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM TemperatureSensor " , TemperatureSensor.class).getResultList();
        }
    }

    @Override
    public List<HumiditySensor> findAllHumiditySensors() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM HumiditySensor " , HumiditySensor.class).getResultList();
        }
    }

    @Override
    public List<AirQualitySensor> findAllAirQualitySensors() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("FROM AirQualitySensor " , AirQualitySensor.class).getResultList();
        }
    }

    @Override
    public Optional<TemperatureSensor> findTemperatureSensorById(Long id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(TemperatureSensor.class , id));
        }
    }

    @Override
    public Optional<HumiditySensor> findHumiditySensorById(Long id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(HumiditySensor.class , id));
        }
    }

    @Override
    public Optional<AirQualitySensor> findAirQualitySensorById(Long id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(AirQualitySensor.class , id));
        }
    }

    private void executeInTransaction(HibernateOperation operation){
        Transaction tx = null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            tx = session.beginTransaction();
            operation.accept(session);
            tx.commit();
        } catch (Exception e) {
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw e;
        }
    }


    @FunctionalInterface
    private interface HibernateOperation {
        void accept(Session session);
    }

}
