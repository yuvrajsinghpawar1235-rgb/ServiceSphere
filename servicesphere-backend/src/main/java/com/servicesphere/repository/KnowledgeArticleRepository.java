package com.servicesphere.repository;

import com.servicesphere.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    // For ordinary users: Only search among PUBLISHED articles matching keyword/category
    @Query("SELECT k FROM KnowledgeArticle k WHERE k.status = 'PUBLISHED' AND " +
            "(:keyword IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR k.category = :category)")
    List<KnowledgeArticle> searchPublishedArticles(@Param("keyword") String keyword, @Param("category") String category);

    // For staff: Search across all statuses and categories
    @Query("SELECT k FROM KnowledgeArticle k WHERE " +
            "(:status IS NULL OR k.status = :status) AND " +
            "(:category IS NULL OR k.category = :category) AND " +
            "(:keyword IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KnowledgeArticle> searchAllArticlesForStaff(
            @Param("status") String status,
            @Param("category") String category,
            @Param("keyword") String keyword);
}