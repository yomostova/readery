package com.example.readery.entity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors", indexes = @Index(columnList = "libraryId"))

public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,  generator =
            "AUTHOR_SEQ")
    @SequenceGenerator(name = "AUTHOR_SEQ", sequenceName = "AUTHOR_SEQ")
    private int id;

    @Column(name = "name", columnDefinition = "text")
    private String name;

    private String libraryId;

    public Author(){}

    @ManyToMany(mappedBy="authors")
    private Set<Book> books = new HashSet<>();

    @Override
    public String toString(){
        return name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLibraryId(String id) {
        this.libraryId = id;
    }

    public void setBooks(Set<Book> books) {
        this.books.addAll(books);
    }

}
