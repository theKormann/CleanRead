package com.cleanread.infrastructure.adapter.out.jsoup;

import com.cleanread.domain.model.Article;
import com.cleanread.domain.model.ExtractionMetrics;
import com.cleanread.domain.port.out.WebScraperPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JSoupWebScraperAdapter implements WebScraperPort {

    private final WebClient webClient;

    public JSoupWebScraperAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Article> fetchAndClean(String targetUrl) {
        return webClient.get()
                .uri(targetUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(html -> processHtml(targetUrl, html));
    }

    private Article processHtml(String url, String rawHtml) {
        Document doc = Jsoup.parse(rawHtml, url);
        int rawSize = rawHtml.length();

        String title = doc.title();
        String author = extractAuthor(doc);

        int elementsRemoved = cleanGarbage(doc);

        Element mainContent = findMainContent(doc);
        String cleanText = mainContent.text();
        int cleanSize = cleanText.length();

        int readingTime = calculateReadingTime(cleanText);
        double pollutionScore = calculatePollutionScore(rawSize, cleanSize, elementsRemoved);

        ExtractionMetrics metrics = new ExtractionMetrics(readingTime, elementsRemoved, pollutionScore);

        return new Article(url, title, author, cleanText, metrics);
    }

    private int cleanGarbage(Document doc) {
        String selectorsToRemove = "script, style, nav, footer, header, aside, " +
                ".ad, .advertisement, .promo, .social-share, #comments";

        Elements garbage = doc.select(selectorsToRemove);
        int count = garbage.size();
        garbage.remove();

        return count;
    }

    private Element findMainContent(Document doc) {
        Element articleTag = doc.selectFirst("article");
        if (articleTag != null) return articleTag;

        Element mainTag = doc.selectFirst("main");
        if (mainTag != null) return mainTag;

         return doc.body();
    }

    private String extractAuthor(Document doc) {
        Element metaAuthor = doc.selectFirst("meta[name=author]");
        if (metaAuthor != null) return metaAuthor.attr("content");
        return "Autor Desconhecido";
    }

    private int calculateReadingTime(String text) {
        if (text.isEmpty()) return 0;
        int wordCount = text.split("\\s+").length;
        return (int) Math.ceil((double) wordCount / 200.0);
    }

    private double calculatePollutionScore(int rawSize, int cleanSize, int elementsRemoved) {
        if (rawSize == 0) return 0.0;
        double textDensity = (double) cleanSize / rawSize;
        double score = (1.0 - textDensity) * 10.0;
        return Math.min(Math.max(score, 0.0), 10.0);
    }
}