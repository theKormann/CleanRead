package com.cleanread.domain.port.out;

import com.cleanread.domain.model.Article;
import reactor.core.publisher.Mono;

public interface WebScraperPort {
    Mono<Article> fetchAndClean(String targetUrl);
}