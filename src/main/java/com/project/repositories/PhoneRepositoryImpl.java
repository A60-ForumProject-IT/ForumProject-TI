package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.PhoneNumber;
import com.project.models.User;
import com.project.repositories.contracts.PhoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhoneRepositoryImpl implements PhoneRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public PhoneRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addPhoneToAdmin(PhoneNumber phoneNumber) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(phoneNumber);
            session.getTransaction().commit();
        }
    }

    @Override
    public PhoneNumber getPhoneNumber(String phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<PhoneNumber> query = session.createQuery("from PhoneNumber where phoneNumber = :phoneNumber", PhoneNumber.class);
            query.setParameter("phoneNumber", phoneNumber);
            List<PhoneNumber> phoneNumbers = query.list();
            if (phoneNumbers.isEmpty()) {
                throw new EntityNotFoundException("Phone", "number", phoneNumber);
            }
            return phoneNumbers.get(0);
        }
    }
}
