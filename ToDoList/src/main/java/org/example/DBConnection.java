package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private Session session;

    public DBConnection() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder()
                .build();
        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder()
                .build();
        this.session = sessionFactory.openSession();
    }
    public Task get(Integer id) {
        return session.get(Task.class, id);
    }
    public List<Task> getListTask(){
        int id = 1;
        List<Task> listTask = new ArrayList<>();
        while (get(id) != null) {
            listTask.add(get(id));
            id++;
        }
    return  listTask;
    }
    public void create(String title,String description){
        session.persist(new Task(title,description));
        session.beginTransaction().commit();
    }
    public void update(Integer id, String newTitle) {
        Task task = get(id);
        if(task != null) {
            task.setTitle(newTitle);
            session.persist(task);
            session.beginTransaction().commit();
        }
    }
    public void update(String newDescription, Integer id) {
        Task task = get(id);
        if(task != null) {
            task.setDescription(newDescription);
            session.persist(task);
            session.beginTransaction().commit();
        }
    }
    public void update(boolean newIsDone, Integer id) {
        Task task = get(id);
        if(task != null) {
            task.setDone(newIsDone);
            session.persist(task);
            session.beginTransaction().commit();
        }
    }
    public void update(Integer id, String newTitle,String newDescription,Boolean newIsDone) {
        Task task = get(id);
        if(task != null) {
            task.setTitle(newTitle);
            task.setDescription(newDescription);
            task.setDone(newIsDone);
            session.persist(task);
            session.beginTransaction().commit();
        }
    }
    public void delete(Integer id){
        Task task = get(id);
        if(task != null){
            session.remove(task);
            session.beginTransaction().commit();
        }
    }
}
