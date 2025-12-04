package com.bloodbowlclub.authoring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "authoring_comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {

    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_photo_url", length = 500)
    private String authorPhotoUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;
}