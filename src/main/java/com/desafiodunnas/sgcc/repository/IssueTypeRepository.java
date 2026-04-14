package com.desafiodunnas.sgcc.repository;

import com.desafiodunnas.sgcc.domain.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {
    boolean existsByTitleIgnoreCase(String title);
}