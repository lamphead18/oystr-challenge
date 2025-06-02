package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScraperManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ScraperManager.class);
    
    private final List<WebScraperService> scraperServices;
    
    public ScraperManager(List<WebScraperService> scraperServices) {
        this.scraperServices = scraperServices;
    }
    
    public List<MachineryItem> scrapeAllWebsites(Map<String, List<String>> urlsMap) {
        List<MachineryItem> allItems = new ArrayList<>();
        
        for (WebScraperService scraper : scraperServices) {
            String websiteName = scraper.getWebsiteName();
            logger.info("Starting scraping for website: {}", websiteName);
            
            List<String> urls = urlsMap.getOrDefault(websiteName, new ArrayList<>());
            
            if (urls.isEmpty()) {
                logger.warn("No URLs provided for website: {}", websiteName);
                continue;
            }
            
            for (String url : urls) {
                List<MachineryItem> items = scraper.scrapePage(url);
                allItems.addAll(items);
                logger.info("Scraped {} items from {}", items.size(), url);
            }
        }
        
        return allItems;
    }
    
    public WebScraperService getScraperByWebsite(String websiteName) {
        for (WebScraperService scraper : scraperServices) {
            if (scraper.getWebsiteName().equals(websiteName)) {
                return scraper;
            }
        }
        return null;
    }
}