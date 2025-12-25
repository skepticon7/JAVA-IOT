package org.example.dao.implementation;

import org.example.configuration.HibernateConfig;
import org.example.dao.repository.IReadingDAO;
import org.example.model.Reading;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ReadingDAO implements IReadingDAO {

    @Override
    public void save(Reading reading) {
        executeInTransaction(session -> session.save(reading));
    }

    @Override
    public List<Reading> findAllReadings() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            return session.createQuery("FROM Reading " , Reading.class).getResultList();
        }
    }

    @Override
    public List<Reading> findDeviceAllReadings(Long id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            return session.createQuery("FROM Reading r WHERE r.device.id = :deviceId" , Reading.class)
                    .setParameter("deviceId" , id)
                    .getResultList();
        }
    }

    private void executeInTransaction(ReadingDAO.HibernateOperation operation){
        Transaction tx = null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            tx = session.beginTransaction();
            operation.accept(session);
            tx.commit();
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            throw e;
        }
    }


    @FunctionalInterface
    private interface HibernateOperation {
        void accept(Session session);
    }
}
