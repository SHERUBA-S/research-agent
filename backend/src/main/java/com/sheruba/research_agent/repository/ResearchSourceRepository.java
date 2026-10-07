package com.sheruba.research_agent.repository;

import com.sheruba.research_agent.entity.ResearchSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchSourceRepository extends JpaRepository<ResearchSource, String> {

    List<ResearchSource> findByResearchIdOrderByCitationNumberAsc(String researchId);

    void deleteByResearchId(String researchId);
}
