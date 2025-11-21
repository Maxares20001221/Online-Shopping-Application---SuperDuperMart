package com.example.superdupermart.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class AbstractHibernateDao<T> {

    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> clazz;

    protected void setClazz(Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T findById(Long id) {
        return getCurrentSession().get(clazz, id);
    }

    public List<T> findAll() {
        return getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    public void save(T entity) {
        getCurrentSession().save(entity);
    }

    public void update(T entity) {
        getCurrentSession().merge(entity);
    }

    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    public void deleteById(Long id) {
        T entity = findById(id);
        if (entity != null) delete(entity);
    }
}