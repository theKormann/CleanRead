package com.cleanread.infrastructure.adapter.out.cache;

import com.cleanread.domain.model.Article;
import com.cleanread.domain.port.out.ArticleCachePort;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
public class CaffeineArticleCacheAdapter implements ArticleCachePort {

    private final Cache<String, Article> cache = Caffeine.newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .maximumSize(5000)
            .build();

    @Override
    public Mono<Article> findByUrl(String url) {
        return Mono.justOrEmpty(cache.getIfPresent(url));
    }

    @Override
    public Mono<Article> save(Article article) {
        return Mono.fromRunnable(() -> cache.put(article.getSourceUrl(), article))
                .thenReturn(article);
    }
}
