package com.bloodbowlclub.authoring.services;

import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.io.services.UserContext;
import com.bloodbowlclub.authoring.domain.ArticleEntity;
import com.bloodbowlclub.authoring.domain.CommentEntity;
import com.bloodbowlclub.authoring.domain.ParagraphEntity;
import com.bloodbowlclub.authoring.repositories.ArticleRepository;
import com.bloodbowlclub.authoring.repositories.CommentRepository;
import com.bloodbowlclub.authoring.web.dto.requests.CreateArticleRequest;
import com.bloodbowlclub.authoring.web.dto.requests.CreateCommentRequest;
import com.bloodbowlclub.authoring.web.dto.requests.ParagraphRequest;
import com.bloodbowlclub.authoring.web.dto.requests.UpdateArticleRequest;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleDetailDto;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleListResponse;
import com.bloodbowlclub.authoring.web.dto.responses.ArticleSummaryDto;
import com.bloodbowlclub.authoring.web.dto.responses.CommentDto;
import com.bloodbowlclub.authoring.web.mappers.ArticleMapper;
import com.bloodbowlclub.authoring.web.mappers.CommentMapper;
import com.bloodbowlclub.lib.services.IdService;
import com.bloodbowlclub.lib.services.result.exceptions.Forbidden;
import com.bloodbowlclub.lib.services.result.exceptions.NotFound;
import com.bloodbowlclub.lib.services.result.exceptions.Unauthorized;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthoringService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final IdService idService;
    private final UserContext userContext;
    private final MessageSource messageSource;

    private static final int PAGE_SIZE = 20;

    public ArticleListResponse listArticles(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<ArticleEntity> articlePage = articleRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<ArticleSummaryDto> summaries = articleMapper.toSummaryDtoList(articlePage.getContent());

        return new ArticleListResponse(
            summaries,
            articlePage.getTotalElements(),
            page,
            articlePage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ArticleDetailDto getArticleDetail(String articleId) {
        ArticleEntity article = articleRepository.findByIdWithDetails(articleId)
            .orElseThrow(() -> new NotFound(Map.of("article", getMessage("authoring.article.not_found", articleId))));

        return articleMapper.toDetailDto(article);
    }

    public ArticleDetailDto createArticle(CreateArticleRequest request) {
        ActiveUserAccount currentUser = getCurrentUser();

        ArticleEntity article = new ArticleEntity();
        article.setId(idService.getStringId());
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setImageUrl(request.getImageUrl());
        article.setLeadParagraph(request.getLeadParagraph());
        article.setAuthorName(currentUser.getUsername().toString());
        article.setAuthorPhotoUrl(null); // TODO: récupérer depuis le profil utilisateur
        article.setCreatedAt(Instant.now());

        // Ajouter les paragraphes
        if (request.getParagraphs() != null) {
            int index = 0;
            for (ParagraphRequest paragraphRequest : request.getParagraphs()) {
                ParagraphEntity paragraph = new ParagraphEntity();
                paragraph.setId(idService.getStringId());
                paragraph.setTitle(paragraphRequest.getTitle());
                paragraph.setContent(paragraphRequest.getContent());
                paragraph.setOrderIndex(index++);
                article.addParagraph(paragraph);
            }
        }

        ArticleEntity savedArticle = articleRepository.save(article);
        return articleMapper.toDetailDto(savedArticle);
    }

    public ArticleDetailDto updateArticle(String articleId, UpdateArticleRequest request) {
        ActiveUserAccount currentUser = getCurrentUser();

        ArticleEntity article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFound(Map.of("article", getMessage("authoring.article.not_found", articleId))));

        // Vérifier que l'utilisateur est l'auteur
        if (!article.getAuthorName().equals(currentUser.getUsername().toString())) {
            throw new Forbidden(Map.of("authorization", getMessage("authoring.article.forbidden_update")));
        }

        // Mettre à jour les champs
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setImageUrl(request.getImageUrl());
        article.setLeadParagraph(request.getLeadParagraph());

        // Supprimer les anciens paragraphes et ajouter les nouveaux
        article.getParagraphs().clear();
        if (request.getParagraphs() != null) {
            int index = 0;
            for (ParagraphRequest paragraphRequest : request.getParagraphs()) {
                ParagraphEntity paragraph = new ParagraphEntity();
                paragraph.setId(idService.getStringId());
                paragraph.setTitle(paragraphRequest.getTitle());
                paragraph.setContent(paragraphRequest.getContent());
                paragraph.setOrderIndex(index++);
                article.addParagraph(paragraph);
            }
        }

        ArticleEntity savedArticle = articleRepository.save(article);
        return articleMapper.toDetailDto(savedArticle);
    }

    public void deleteArticle(String articleId) {
        ActiveUserAccount currentUser = getCurrentUser();

        ArticleEntity article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFound(Map.of("article", getMessage("authoring.article.not_found", articleId))));

        // Vérifier que l'utilisateur est l'auteur
        if (!article.getAuthorName().equals(currentUser.getUsername().toString())) {
            throw new Forbidden(Map.of("authorization", getMessage("authoring.article.forbidden_delete")));
        }

        articleRepository.delete(article);
    }

    public CommentDto addComment(String articleId, CreateCommentRequest request) {
        ActiveUserAccount currentUser = getCurrentUser();

        ArticleEntity article = articleRepository.findById(articleId)
            .orElseThrow(() -> new NotFound(Map.of("article", getMessage("authoring.article.not_found", articleId))));

        CommentEntity comment = new CommentEntity();
        comment.setId(idService.getStringId());
        comment.setContent(request.getContent());
        comment.setAuthorName(currentUser.getUsername().toString());
        comment.setAuthorPhotoUrl(null); // TODO: récupérer depuis le profil utilisateur
        comment.setCreatedAt(Instant.now());
        article.addComment(comment);

        articleRepository.save(article);

        return commentMapper.toDto(comment);
    }

    public void deleteComment(String commentId) {
        ActiveUserAccount currentUser = getCurrentUser();

        CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new NotFound(Map.of("comment", getMessage("authoring.comment.not_found", commentId))));

        // Vérifier que l'utilisateur est l'auteur
        if (!comment.getAuthorName().equals(currentUser.getUsername().toString())) {
            throw new Forbidden(Map.of("authorization", getMessage("authoring.comment.forbidden_delete")));
        }

        commentRepository.delete(comment);
    }

    private ActiveUserAccount getCurrentUser() {
        ActiveUserAccount user = userContext.getCurrentUser();
        if (user == null) {
            throw new Unauthorized(Map.of("authentication", getMessage("authoring.authentication.required")));
        }
        return user;
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}