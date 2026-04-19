package com.cleanread.infrastructure.adapter.in.web;

import com.cleanread.domain.port.in.ExtractContentUseCase;
import com.cleanread.infrastructure.adapter.in.web.dto.CleanReadResponse;
import com.cleanread.infrastructure.adapter.in.web.dto.ExtractionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleExtractionController {

    private final ExtractContentUseCase extractUseCase;

    public ArticleExtractionController(ExtractContentUseCase extractUseCase) {
        this.extractUseCase = extractUseCase;
    }

    @PostMapping("/extract")
    public Mono<ResponseEntity<CleanReadResponse>> extractArticle(@RequestBody ExtractionRequest request) {

        return extractUseCase.extract(request.url())
                .map(CleanReadResponse::fromDomain)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
