package com.servicesphere.controller;

import com.servicesphere.entity.KnowledgeArticle;
import com.servicesphere.repository.KnowledgeArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/kb")
public class KnowledgeArticleController {

    @Autowired
    private KnowledgeArticleRepository kbRepository;

    private static final Set<String> ALLOWED_STATUSES = Set.of("DRAFT", "PUBLISHED", "UNPUBLISHED", "ARCHIVED");

    // Search and List Articles (Role-Enforced Visibility)
    @GetMapping
    public ResponseEntity<List<KnowledgeArticle>> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Long roleId) {

        // SECURITY RULE: Ordinary users (roleId = 1) must NEVER see drafts or un-published articles
        if (roleId != null && roleId == 1L) {
            return ResponseEntity.ok(kbRepository.searchPublishedArticles(keyword, category));
        }

        // Authorized staff can view all statuses and search freely
        return ResponseEntity.ok(kbRepository.searchAllArticlesForStaff(status, category, keyword));
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody KnowledgeArticle article, @RequestParam Long roleId) {
        // Only authorized staff (Agents, Managers, Admins -> roleIds 2, 3, 5) can create articles
        if (roleId == null || roleId == 1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Ordinary employees cannot create Knowledge Base articles."));
        }

        if (article.getStatus() == null || !ALLOWED_STATUSES.contains(article.getStatus().toUpperCase())) {
            article.setStatus("DRAFT");
        } else {
            article.setStatus(article.getStatus().toUpperCase());
        }

        KnowledgeArticle saved = kbRepository.save(article);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody KnowledgeArticle updatedArticle, @RequestParam Long roleId) {
        if (roleId == null || roleId == 1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Ordinary employees cannot modify KB articles."));
        }

        KnowledgeArticle article = kbRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));

        article.setTitle(updatedArticle.getTitle());
        article.setSummary(updatedArticle.getSummary());
        article.setBody(updatedArticle.getBody());
        article.setCategory(updatedArticle.getCategory());
        article.setTags(updatedArticle.getTags());

        if (updatedArticle.getStatus() != null && ALLOWED_STATUSES.contains(updatedArticle.getStatus().toUpperCase())) {
            article.setStatus(updatedArticle.getStatus().toUpperCase());
        }

        return ResponseEntity.ok(kbRepository.save(article));
    }

    // Lifecycle Status Transition (Publish, Unpublish, Archive, Draft)
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateArticleStatus(@PathVariable Long id, @RequestParam String status, @RequestParam Long roleId) {
        if (roleId == null || roleId == 1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Ordinary employees cannot change article status."));
        }

        String targetStatus = status.toUpperCase();
        if (!ALLOWED_STATUSES.contains(targetStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid status value. Allowed: DRAFT, PUBLISHED, UNPUBLISHED, ARCHIVED"));
        }

        KnowledgeArticle article = kbRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        article.setStatus(targetStatus);
        return ResponseEntity.ok(kbRepository.save(article));
    }

    // Optional Helpfulness Vote Counter
    @PostMapping("/{id}/helpful")
    public ResponseEntity<?> markHelpful(@PathVariable Long id) {
        KnowledgeArticle article = kbRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
        article.setHelpfulnessCount(article.getHelpfulnessCount() + 1);
        return ResponseEntity.ok(kbRepository.save(article));
    }
}