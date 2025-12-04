package com.bloodbowlclub.authoring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authoring_article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntity {

    @Id
    @Column(name = "id", length = 26, nullable = false)
    private String id;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_photo_url", length = 500)
    private String authorPhotoUrl;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "description", length = 200, nullable = false)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "lead_paragraph", columnDefinition = "TEXT", nullable = false)
    private String leadParagraph;

    @OneToMany(
        mappedBy = "article",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("orderIndex ASC")
    private List<ParagraphEntity> paragraphs = new ArrayList<>();

    @OneToMany(
        mappedBy = "article",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("createdAt ASC")
    private List<CommentEntity> comments = new ArrayList<>();

    public void addParagraph(ParagraphEntity paragraph) {
        paragraphs.add(paragraph);
        paragraph.setArticle(this);
    }

    public void removeParagraph(ParagraphEntity paragraph) {
        paragraphs.remove(paragraph);
        paragraph.setArticle(null);
    }

    public void addComment(CommentEntity comment) {
        comments.add(comment);
        comment.setArticle(this);
    }

    public void removeComment(CommentEntity comment) {
        comments.remove(comment);
        comment.setArticle(null);
    }
}