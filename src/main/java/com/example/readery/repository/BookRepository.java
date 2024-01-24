package com.example.readery.repository;

import com.example.readery.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book,Integer> {
    @Query(value = "SELECT * FROM books b WHERE to_tsvector(b.book_title) @@ to_tsquery(:query)", nativeQuery = true)
    List<Book> searchBooksByTitle(@Param("query") String query);

    @Query(value = "CREATE INDEX bookTitle_index ON books USING GIN " +
            "(book_title)", nativeQuery = true)
    void createGinIndex();

}
