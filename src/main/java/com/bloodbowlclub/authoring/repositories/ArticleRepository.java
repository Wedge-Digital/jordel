package com.bloodbowlclub.authoring.repositories;

import com.bloodbowlclub.authoring.domain.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {

    Page<ArticleEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a " +
           "LEFT JOIN FETCH a.paragraphs " +
           "LEFT JOIN FETCH a.comments " +
           "WHERE a.id = :id")
    Optional<ArticleEntity> findByIdWithDetails(@Param("id") String id);
}