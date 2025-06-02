package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScraperManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ScraperManager.class);
    
    private final List<WebScraperService> scraperServices;
    
    public ScraperManager(List<WebScraperService> scraperServices) {
        this.scraperServices = scraperServices;
    }
    
    public List<MachineryItem> scrapeAllWebsites(Map<String, List<String>> urlsMap) {
        return scraperServices.stream()
                .flatMap(scraper -> scrapeForScraper(scraper, urlsMap))
                .collect(Collectors.toList());
    }
    
    private Stream<MachineryItem> scrapeForScraper(WebScraperService scraper, Map<String, List<String>> urlsMap) {
        String websiteName = scraper.getWebsiteName();
        logger.info("Starting scraping for website: {}", websiteName);
        
        List<String> urls = getUrlsForWebsite(websiteName, urlsMap);
        if (urls.isEmpty()) {
            logger.warn("No URLs provided for website: {}", websiteName);
            return Stream.empty();
        }
        
        return urls.stream().flatMap(url -> scrapeUrl(scraper, url));
    }
    
    private List<String> getUrlsForWebsite(String websiteName, Map<String, List<String>> urlsMap) {
        return urlsMap.getOrDefault(websiteName, Collections.emptyList());
    }
    
    private Stream<MachineryItem> scrapeUrl(WebScraperService scraper, String url) {
        List<MachineryItem> items = scraper.scrapePage(url);
        logger.info("Scraped {} items from {}", items.size(), url);
        return items.stream();
    }
}