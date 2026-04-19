package com.cleanread.domain.model;

import java.time.LocalDateTime;

public class Article {
    private final String sourceUrl;
    private final String title;
    private final String author;
    private final String cleanContent;
    private final ExtractionMetrics metrics;
    private final LocalDateTime extractedAt;

    public Article(String sourceUrl, String title, String author, String cleanContent, ExtractionMetrics metrics) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            throw new IllegalArgumentException("A URL de origem é obrigatória");
        }
        if (cleanContent == null || cleanContent.isBlank()) {
            throw new IllegalArgumentException("O conteúdo extraído não pode ser vazio");
        }

        this.sourceUrl = sourceUrl;
        this.title = title != null ? title : "Título Desconhecido";
        this.author = author != null ? author : "Autor Desconhecido";
        this.cleanContent = cleanContent;
        this.metrics = metrics;
        this.extractedAt = LocalDateTime.now();
    }

    public String getSourceUrl() { return sourceUrl; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCleanContent() { return cleanContent; }
    public ExtractionMetrics getMetrics() { return metrics; }
    public LocalDateTime getExtractedAt() { return extractedAt; }


    public boolean isHighlyPolluted() {
        return this.metrics.pollutionScore() > 7.0;
    }
}