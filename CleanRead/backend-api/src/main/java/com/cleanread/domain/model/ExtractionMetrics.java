package com.cleanread.domain.model;

public record ExtractionMetrics(
        int estimatedReadingTimeMinutes,
        int removedElementsCount,
        double pollutionScore
) {
    public ExtractionMetrics {
        if (estimatedReadingTimeMinutes < 0 || removedElementsCount < 0 || pollutionScore < 0) {
            throw new IllegalArgumentException("Métricas não podem ter valores negativos");
        }
    }
}
