package com.example.readery.repository;

import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadingStatusRepository extends CrudRepository<ReadingStatus, ReadingStatusKey> {
    @Query(value = "SELECT * FROM reading_status r WHERE r.user_id = :userId", nativeQuery = true)
    Set<ReadingStatus> findAllById(@Param("userId") int userId);

    void deleteById(ReadingStatusKey id);
}
