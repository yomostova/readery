package com.example.readery.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "books", indexes = @Index(columnList = "book_title"))
public class Book {
    @Id
    @PrimaryKeyJoinColumn(name = "book_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_SEQ")
    @SequenceGenerator(name = "BOOK_SEQ", sequenceName = "BOOK_SEQ")
    private int id;

    @Column(name = "book_title", columnDefinition = "text")
    private String title;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    private String coverId;
    private String coverUrl;

    @ManyToMany
    @JoinTable(name = "book_author",
            joinColumns = {@JoinColumn(name = "fk_book")},
            inverseJoinColumns = {@JoinColumn(name = "fk_author")})
    private Set<Author> authors = new HashSet<>();

    @ElementCollection
    @Column(length=1024)
    private List<String> authorsNames = new ArrayList<>();

    @ElementCollection
    private Set<String> authorLibraryIds = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private Map<Integer, ReadingStatus> readingStatuses = new HashMap<>();

    private void addAuthor(Author author) {
        this.authors.add(author);
        this.authorsNames.add(author.getName());
    }

    public void configureCover(String coverImageBase){
        if(coverId != null && !coverId.isEmpty()){
            String coverUrl = coverImageBase + coverId + "-M.jpg";
            if(this.coverUrl == null){
                this.coverUrl = coverUrl;
            }
        } else {
            this.coverUrl = "/images/no-image.png";
        }
    }

    @Override
    public String toString() {
        return "Book titled " + title + " by " + authors.toString();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public Map<Integer, ReadingStatus> getReadingStatuses() {
        return readingStatuses;
    }

    public String getCoverId() {
        return coverId;
    }

    public List<String> getAuthorsNames() {
        return authorsNames;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public Set<String> getAuthorLibraryIds() {
        return authorLibraryIds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthors(Set<Author> authors) {
        for (Author a : authors) {
            addAuthor(a);
        }
    }

    public void setReadingStatuses(Integer userId, ReadingStatus status) {
        readingStatuses.put(userId, status);
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    public void setAuthorsNames(List<String> authorsNames) {
        this.authorsNames = authorsNames;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public void setAuthorLibraryIds(Set<String> authorLibraryIds) {
        this.authorLibraryIds = authorLibraryIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return id == book.id && Objects.equals(title, book.title) && Objects.equals(publicationDate, book.publicationDate) && Objects.equals(description, book.description) && Objects.equals(coverId, book.coverId) && Objects.equals(coverUrl, book.coverUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, publicationDate, description, coverId, coverUrl);
    }
}
