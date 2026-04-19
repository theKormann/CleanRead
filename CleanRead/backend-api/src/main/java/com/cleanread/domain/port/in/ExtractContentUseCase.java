package com.cleanread.domain.port.in;

import com.cleanread.domain.model.Article;
import reactor.core.publisher.Mono;

public interface ExtractContentUseCase {
    Mono<Article> extract(String targetUrl);
}
