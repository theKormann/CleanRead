package com.cleanread.domain.port.out;

import com.cleanread.domain.model.Article;
import reactor.core.publisher.Mono;

public interface ArticleCachePort {
    Mono<Article> findByUrl(String url);
    Mono<Article> save(Article article);
}