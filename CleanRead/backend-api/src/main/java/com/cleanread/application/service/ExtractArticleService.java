package com.cleanread.application.service;

import com.cleanread.domain.model.Article;
import com.cleanread.domain.port.in.ExtractContentUseCase;
import com.cleanread.domain.port.out.ArticleCachePort;
import com.cleanread.domain.port.out.WebScraperPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExtractArticleService implements ExtractContentUseCase {

    private final WebScraperPort scraperPort;
    private final ArticleCachePort cachePort;

    public ExtractArticleService(WebScraperPort scraperPort, ArticleCachePort cachePort) {
        this.scraperPort = scraperPort;
        this.cachePort = cachePort;
    }

    @Override
    public Mono<Article> extract(String targetUrl) {
        return cachePort.findByUrl(targetUrl)
                .switchIfEmpty(
                        scraperPort.fetchAndClean(targetUrl)
                                .flatMap(cachePort::save)
                );
    }
}
