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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of WebScraperService for the MercadoMaquinas website.
 */
@Service
public class MercadoMaquinasScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(MercadoMaquinasScraperService.class);
    private static final String WEBSITE_NAME = "MercadoMaquinas";
    
    @Override
    public List<MachineryItem> scrapePage(String url) {
        List<MachineryItem> items = new ArrayList<>();
        
        try {
            logger.info("Scraping MercadoMaquinas URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            
            // Extract model and make from the title
            Element titleElement = doc.selectFirst("h1.ad-title");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                
                // Parse the title to extract make and model
                // Example: "RETRO ESCAVADEIRA CATERPILLAR 416E 2015"
                Pattern pattern = Pattern.compile("([A-Za-z\\s]+)\\s+([A-Za-z]+)\\s+([A-Za-z0-9-]+)\\s+(\\d{4})");
                Matcher matcher = pattern.matcher(fullTitle);
                
                if (matcher.find()) {
                    item.setModel(matcher.group(3)); // Model number (e.g., 416E)
                    item.setMake(matcher.group(2));  // Make (e.g., CATERPILLAR)
                    item.setYear(matcher.group(4));  // Year (e.g., 2015)
                } else {
                    // Fallback if regex doesn't match
                    item.setModel(fullTitle);
                    
                    // Try to extract make from the URL
                    String[] urlParts = url.split("-");
                    if (urlParts.length > 2) {
                        item.setMake(urlParts[2].toUpperCase());
                    }
                }
            }
            
            // Contract type - usually indicated somewhere on the page
            // Default to sale for MercadoMaquinas as it's primarily a sales platform
            item.setContractType("Sale");
            
            // Year - try to extract from title or URL if not already set
            if (item.getYear() == null || item.getYear().isEmpty()) {
                Pattern yearPattern = Pattern.compile("(\\d{4})");
                Matcher yearMatcher = yearPattern.matcher(url);
                if (yearMatcher.find()) {
                    item.setYear(yearMatcher.group(1));
                }
            }
            
            // Worked hours - often in specifications
            Element hoursElement = doc.selectFirst(".ad-info-item:contains(Horas) .ad-info-value");
            if (hoursElement != null) {
                item.setWorkedHours(hoursElement.text().trim());
            }
            
            // City/Location - extract from URL or page content
            Element locationElement = doc.selectFirst(".ad-location");
            if (locationElement != null) {
                item.setCity(locationElement.text().trim());
            } else {
                // Try to extract from URL
                Pattern locationPattern = Pattern.compile("-(\\w+)-(\\w{2})$");
                Matcher locationMatcher = locationPattern.matcher(url);
                if (locationMatcher.find()) {
                    item.setCity(locationMatcher.group(1) + ", " + locationMatcher.group(2).toUpperCase());
                }
            }
            
            // Price
            Element priceElement = doc.selectFirst(".ad-price");
            if (priceElement != null) {
                item.setPrice(priceElement.text().trim());
            }
            
            // Photo URL
            Element photoElement = doc.selectFirst(".ad-image img");
            if (photoElement != null) {
                String photoUrl = photoElement.attr("src");
                if (photoUrl.isEmpty()) {
                    photoUrl = photoElement.attr("data-src");
                }
                item.setPhotoUrl(photoUrl);
            }
            
            items.add(item);
            logger.info("Scraped item from MercadoMaquinas: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping MercadoMaquinas URL: {}", url, e);
        }
        
        return items;
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}