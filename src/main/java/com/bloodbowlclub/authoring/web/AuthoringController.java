package com.bloodbowlclub.authoring.web;

import com.bloodbowlclub.authoring.services.AuthoringService;
import com.bloodbowlclub.authoring.web.dto.requests.CreateArticleRequest;
import com.bloodbowlclub.authoring.web.dto.requests.CreateCommentRequest;
import com.bloodbowlclub.authoring.web.dto.requests.UpdateArticleRequest;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleDetailDto;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleListResponse;
import com.bloodbowlclub.authoring.web.dto.responses.CommentDto;
import com.bloodbowlclub.lib.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authoring")
@RequiredArgsConstructor
public class AuthoringController {

    private final AuthoringService authoringService;

    @GetMapping("/article")
    public ResponseEntity<ApiResponse<ArticleListResponse>> listArticles(
        @RequestParam(defaultValue = "0") int page
    ) {
        ArticleListResponse response = authoringService.listArticles(page);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> getArticle(
        @PathVariable String id
    ) {
        ArticleDetailDto article = authoringService.getArticleDetail(id);
        return ResponseEntity.ok(ApiResponse.success(article));
    }

    @PostMapping("/article")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> createArticle(
        @Valid @RequestBody CreateArticleRequest request
    ) {
        ArticleDetailDto article = authoringService.createArticle(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(article));
    }

    @PutMapping("/article/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> updateArticle(
        @PathVariable String id,
        @Valid @RequestBody UpdateArticleRequest request
    ) {
        ArticleDetailDto article = authoringService.updateArticle(id, request);
        return ResponseEntity.ok(ApiResponse.success(article));
    }

    @DeleteMapping("/article/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable String id) {
        authoringService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/article/{id}/comment")
    public ResponseEntity<ApiResponse<CommentDto>> addComment(
        @PathVariable String id,
        @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentDto comment = authoringService.addComment(id, request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(comment));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        authoringService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}