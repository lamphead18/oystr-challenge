package com.webscraper.service.impl;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.WebScraperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of WebScraperService for the second website.
 */
@Service
public class Website2ScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(Website2ScraperService.class);
    private static final String WEBSITE_NAME = "Website2";
    
    @Override
    public List<MachineryItem> scrapePage(String url) {
        List<MachineryItem> items = new ArrayList<>();
        
        try {
            logger.info("Scraping URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            // This is a placeholder implementation. In a real application, you would need to
            // inspect the actual website's HTML structure and adjust the selectors accordingly.
            Elements itemElements = doc.select(".product-item");
            
            for (Element itemElement : itemElements) {
                MachineryItem item = new MachineryItem();
                item.setSourceWebsite(WEBSITE_NAME);
                
                // Extract data based on the website's HTML structure
                // These selectors are examples and should be adjusted for the actual website
                item.setModel(extractText(itemElement, ".product-model"));
                item.setContractType(extractText(itemElement, ".product-contract"));
                item.setMake(extractText(itemElement, ".product-make"));
                item.setYear(extractText(itemElement, ".product-year"));
                item.setWorkedHours(extractText(itemElement, ".product-hours"));
                item.setCity(extractText(itemElement, ".product-location"));
                item.setPrice(extractText(itemElement, ".product-price"));
                item.setPhotoUrl(extractAttribute(itemElement, ".product-image img", "src"));
                
                items.add(item);
            }
            
            logger.info("Found {} items on {}", items.size(), url);
        } catch (IOException e) {
            logger.error("Error scraping URL: {}", url, e);
        }
        
        return items;
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
    
    private String extractText(Element element, String selector) {
        Element selectedElement = element.select(selector).first();
        return selectedElement != null ? selectedElement.text().trim() : "";
    }
    
    private String extractAttribute(Element element, String selector, String attribute) {
        Element selectedElement = element.select(selector).first();
        return selectedElement != null ? selectedElement.attr(attribute).trim() : "";
    }
}