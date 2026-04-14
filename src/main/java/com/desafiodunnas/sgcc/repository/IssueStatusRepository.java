package com.desafiodunnas.sgcc.repository;

import com.desafiodunnas.sgcc.domain.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IssueStatusRepository extends JpaRepository<IssueStatus, Long> {
    Optional<IssueStatus> findByIsDefaultTrue();
    boolean existsByNameIgnoreCase(String name);
}