package com.desafiodunnas.sgcc.repository;

import com.desafiodunnas.sgcc.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}