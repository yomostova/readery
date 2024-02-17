package com.example.readery.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class ReadingStatus {
    @EmbeddedId
    ReadingStatusKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    Book book;

    public enum Status{
        WISHLIST,
        READING,
        FINISHED,
        UNFINISHED,
        UNKNOWN,
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate startedDate;
    private LocalDate finishedDate;
    private int rating;


    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public ReadingStatusKey getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public LocalDate getFinishedDate() {
        return finishedDate;
    }

    public int getRating() {
        return rating;
    }

    public void setId(ReadingStatusKey id) {
        this.id = id;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartedDate(LocalDate startedDate) {
        this.startedDate = startedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        this.finishedDate = finishedDate;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReadingStatus that)) return false;
        return rating == that.rating && Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(book, that.book) && status == that.status && Objects.equals(startedDate, that.startedDate) && Objects.equals(finishedDate, that.finishedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, book, status, startedDate, finishedDate, rating);
    }
}
