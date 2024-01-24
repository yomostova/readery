package com.example.readery;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")

public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

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
