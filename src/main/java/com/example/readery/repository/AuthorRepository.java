package com.example.readery.repository;

import com.example.readery.entity.Author;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Integer> {
    List<Author> findByLibraryId(String libraryId);
    List<Author> findByNameAllIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM authors x USING authors y " +
            "WHERE x.id < y.id AND x.library_id = y.library_id",
            nativeQuery = true)
    void deleteDuplicates();
}
