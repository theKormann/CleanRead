package com.cleanread.infrastructure.adapter.in.web.dto;

import com.cleanread.domain.model.Article;

public record CleanReadResponse(
        String url,
        String title,
        String author,
        String content,
        MetricsDto metrics
) {

    public record MetricsDto(
            int estimatedReadingTimeMinutes,
            int removedElementsCount,
            double pollutionScore
    ) {}

    public static CleanReadResponse fromDomain(Article article) {
        return new CleanReadResponse(
                article.getSourceUrl(),
                article.getTitle(),
                article.getAuthor(),
                article.getCleanContent(),
                new MetricsDto(
                        article.getMetrics().estimatedReadingTimeMinutes(),
                        article.getMetrics().removedElementsCount(),
                        article.getMetrics().pollutionScore()
                )
        );
    }
}
