package com.webscraper.service.impl;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.WebScraperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of WebScraperService for the Agrofy website.
 */
@Service
public class AgrofyScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgrofyScraperService.class);
    private static final String WEBSITE_NAME = "Agrofy";
    
    @Override
    public List<MachineryItem> scrapePage(String url) {
        List<MachineryItem> items = new ArrayList<>();
        
        try {
            logger.info("Scraping Agrofy URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            
            // Extract model - typically in the title or product name section
            Element titleElement = doc.selectFirst("h1.title");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                item.setModel(fullTitle);
                
                // Try to extract make from the title
                String[] titleParts = fullTitle.split(" ");
                if (titleParts.length > 0) {
                    item.setMake(titleParts[0]); // Usually the first word is the brand/make
                }
            }
            
            // Contract type - usually indicated somewhere on the page
            // This is a simplification - in a real implementation you'd need to look for specific indicators
            if (url.contains("oferta")) {
                item.setContractType("Sale");
            } else {
                item.setContractType("Sale"); // Default to sale if not specified
            }
            
            // Year - often found in specifications or details section
            Element yearElement = doc.selectFirst(".specs-item:contains(Año) .specs-item-value");
            if (yearElement != null) {
                item.setYear(yearElement.text().trim());
            }
            
            // Worked hours - often in specifications
            Element hoursElement = doc.selectFirst(".specs-item:contains(Horas) .specs-item-value");
            if (hoursElement != null) {
                item.setWorkedHours(hoursElement.text().trim());
            }
            
            // City/Location
            Element locationElement = doc.selectFirst(".location");
            if (locationElement != null) {
                item.setCity(locationElement.text().trim());
            }
            
            // Price
            Element priceElement = doc.selectFirst(".price-value");
            if (priceElement != null) {
                item.setPrice(priceElement.text().trim());
            }
            
            // Photo URL
            Element photoElement = doc.selectFirst(".product-image img");
            if (photoElement != null) {
                item.setPhotoUrl(photoElement.attr("src"));
            }
            
            items.add(item);
            logger.info("Scraped item from Agrofy: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping Agrofy URL: {}", url, e);
        }
        
        return items;
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}