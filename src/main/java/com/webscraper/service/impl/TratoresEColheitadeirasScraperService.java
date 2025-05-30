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
 * Implementation of WebScraperService for the TratoresEColheitadeiras website.
 */
@Service
public class TratoresEColheitadeirasScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(TratoresEColheitadeirasScraperService.class);
    private static final String WEBSITE_NAME = "TratoresEColheitadeiras";
    
    @Override
    public List<MachineryItem> scrapePage(String url) {
        List<MachineryItem> items = new ArrayList<>();
        
        try {
            logger.info("Scraping TratoresEColheitadeiras URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            
            // Extract model and make from the title
            Element titleElement = doc.selectFirst("h1.title-vehicle");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                String[] titleParts = fullTitle.split(" ");
                
                if (titleParts.length > 0) {
                    item.setMake(titleParts[0]); // First word is typically the brand/make
                    
                    // Model is usually the second part or the rest of the title
                    if (titleParts.length > 1) {
                        item.setModel(fullTitle);
                    }
                }
            }
            
            // Contract type - usually indicated somewhere on the page
            Element contractElement = doc.selectFirst(".vehicle-info-item:contains(Tipo de anúncio)");
            if (contractElement != null) {
                String contractText = contractElement.text().toLowerCase();
                if (contractText.contains("venda")) {
                    item.setContractType("Sale");
                } else if (contractText.contains("aluguel")) {
                    item.setContractType("Rent");
                } else {
                    item.setContractType("Sale"); // Default
                }
            } else {
                item.setContractType("Sale"); // Default
            }
            
            // Year - often found in specifications
            Element yearElement = doc.selectFirst(".vehicle-info-item:contains(Ano) .vehicle-info-value");
            if (yearElement != null) {
                item.setYear(yearElement.text().trim());
            } else {
                // Try to extract from URL
                if (url.contains("/")) {
                    String[] urlParts = url.split("/");
                    for (String part : urlParts) {
                        if (part.matches("\\d{4}")) { // 4-digit year
                            item.setYear(part);
                            break;
                        }
                    }
                }
            }
            
            // Worked hours - often in specifications
            Element hoursElement = doc.selectFirst(".vehicle-info-item:contains(Horas) .vehicle-info-value");
            if (hoursElement != null) {
                item.setWorkedHours(hoursElement.text().trim());
            }
            
            // City/Location - extract from URL or page content
            if (url.contains("/")) {
                String[] urlParts = url.split("/");
                for (int i = 0; i < urlParts.length - 1; i++) {
                    if (urlParts[i].equalsIgnoreCase("veiculo") && i + 2 < urlParts.length) {
                        item.setCity(urlParts[i + 1] + ", " + urlParts[i + 2].toUpperCase());
                        break;
                    }
                }
            }
            
            // Price
            Element priceElement = doc.selectFirst(".vehicle-price");
            if (priceElement != null) {
                item.setPrice(priceElement.text().trim());
            }
            
            // Photo URL
            Element photoElement = doc.selectFirst(".vehicle-image img");
            if (photoElement != null) {
                String photoUrl = photoElement.attr("src");
                if (photoUrl.isEmpty()) {
                    photoUrl = photoElement.attr("data-src");
                }
                item.setPhotoUrl(photoUrl);
            }
            
            items.add(item);
            logger.info("Scraped item from TratoresEColheitadeiras: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping TratoresEColheitadeiras URL: {}", url, e);
        }
        
        return items;
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}