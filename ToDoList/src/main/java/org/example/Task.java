package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "creation_time")
    private LocalDateTime creationTime;
    @Column(name = "isDone")
    private boolean isDone;
    @Column(name = "title",unique = true)
    private String title;
    @Column(name = "description")
    private String description;
    public Task() {
    }

    public Task(String title, String description) {
        this.creationTime = LocalDateTime.now();
        this.isDone = false;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof Task))
            return false;
        Task task = (Task) obj;
        if(this.title == null){
            return task.title == null;
        }
        return this.title.equals(task.title);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Task:\s" +
                "id = " + id + "\n" +
                "creationTime = " + creationTime + "\n" +
                "isDone=" + isDone + "\n" +
                "title =\s" + title + "\n" +
                "description = " + description;
    }
}
