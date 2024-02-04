package com.example.readery.repository;

import com.example.readery.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book,Integer> {
    @Query(value = "SELECT * FROM books b WHERE to_tsvector(b.book_title) @@ to_tsquery(:query)", nativeQuery = true)
    List<Book> searchBooksByTitle(@Param("query") String query);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO book_author(fk_book, fk_author) " +
            "SELECT l.book_id, a.id " +
            "FROM authors a, book_author_library_ids l " +
            "WHERE a.library_id LIKE l.author_library_ids ",
            nativeQuery = true)
    void updateAuthors();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO book_authors_names(book_id, authors_names) " +
            "SELECT ba.fk_book, a.name FROM book_author ba, authors a " +
            "WHERE ba.fk_author = a.id",
            nativeQuery = true)
    void updateAuthorNames();



}
