package com.example.readery.entity;
import com.example.readery.entity.ReadingStatus;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "user")
    Set<ReadingStatus> readingStatuses = new HashSet<>();

    public int getId() {
        return id;
    }

    public Set<ReadingStatus> getReadingStatuses() {
        return readingStatuses;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReadingStatuses(Set<ReadingStatus> readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
