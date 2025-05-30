package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import java.util.List;

/**
 * Interface for web scraper services.
 */
public interface WebScraperService {
    
    /**
     * Scrapes a specific URL for machinery items.
     * 
     * @param url The URL to scrape
     * @return List of machinery items found on the page
     */
    List<MachineryItem> scrapePage(String url);
    
    /**
     * Gets the name of the website this scraper handles.
     * 
     * @return The website name
     */
    String getWebsiteName();
}