package com.bloodbowlclub.authoring.web.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponse {
    private List<ArticleSummaryDto> articles;
    private long totalArticles;
    private int currentPage;
    private int totalPages;
}