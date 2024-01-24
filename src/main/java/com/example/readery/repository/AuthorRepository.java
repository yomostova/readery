package com.example.readery.repository;

import com.example.readery.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Integer> {
    List<Author> findByLibraryId(String libraryId);
    List<Author> findByNameAllIgnoreCase(String name);
}
