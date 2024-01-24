package com.example.readery.repository;

import com.example.readery.ReadingStatus;
import com.example.readery.ReadingStatusKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReadingStatusRepository extends CrudRepository<ReadingStatus, ReadingStatusKey> {
    @Query(value = "SELECT * FROM reading_status r WHERE r.user_id = :userId", nativeQuery = true)
    public Set<ReadingStatus> findAllById(@Param("userId") int userId);
}
