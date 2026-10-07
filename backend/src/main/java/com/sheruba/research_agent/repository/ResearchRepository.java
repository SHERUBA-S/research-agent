package com.sheruba.research_agent.repository;

import com.sheruba.research_agent.entity.Research;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearchRepository extends JpaRepository<Research, String> {

    List<Research> findAllByOrderByCreatedAtDesc();

    @Query("SELECT r FROM Research r LEFT JOIN FETCH r.sources WHERE r.id = :id")
    Optional<Research> findByIdWithSources(@Param("id") String id);
}
