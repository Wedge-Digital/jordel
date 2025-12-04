package com.bloodbowlclub.authoring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authoring_paragraph")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphEntity {

    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}