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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AgrofyScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(AgrofyScraperService.class);
    private static final String WEBSITE_NAME = "Agrofy";
    private static final String BASE_URL = "https://www.agrofy.com.br";

    
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
            
            boolean isExpired = !doc.select(".expired-notice, .sold-notice, .unavailable-notice").isEmpty();
            
            Elements finalizedMessages = doc.getElementsContainingText("A publicação está finalizada");
            if (!finalizedMessages.isEmpty() || isExpired) {
                logger.info("Ad is finalized or expired: {}", url);
                item.setStatus("Finalized");
                extractDataFromUrl(url, item);
                items.add(item);
                return items;
            } else {
                item.setStatus("Active");
            }
            
            Element titleElement = doc.selectFirst("h1.title, h1.product-title, .product-name");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                item.setModel(fullTitle);
                
                String[] titleParts = fullTitle.split(" ");
                if (titleParts.length > 0) {
                    item.setMake(titleParts[0]);
                }
            } else {
                logger.info("Could not find title element for URL: {}", url);
            }
            
            item.setContractType("Sale");
            
            Element yearElement = doc.selectFirst(".specs-item:contains(Año), .specs-item:contains(Ano), .product-year, .product-detail:contains(Ano)");
            if (yearElement != null) {
                String yearText = yearElement.text().replaceAll("[^0-9]", "");
                if (!yearText.isEmpty()) {
                    item.setYear(yearText);
                }
            } else {
                logger.info("Could not find year element for URL: {}", url);
            }
            
            Element hoursElement = doc.selectFirst(".specs-item:contains(Horas), .product-hours, .product-detail:contains(Horas)");
            if (hoursElement != null) {
                String hoursText = hoursElement.text().replaceAll("[^0-9]", "");
                if (!hoursText.isEmpty()) {
                    item.setWorkedHours(hoursText);
                }
            } else {
                logger.info("Could not find worked hours element for URL: {}", url);
            }
            
            Element locationElement = doc.selectFirst(".location, .product-location, .seller-location");
            if (locationElement != null) {
                item.setCity(locationElement.text().trim());
            } else {
                logger.info("Could not find location element for URL: {}", url);
            }
            
            Element priceElement = doc.selectFirst(".price-value, .product-price, .price");
            if (priceElement != null) {
                item.setPrice(priceElement.text().trim());
            } else {
                logger.info("Could not find price element for URL: {}", url);
            }
            
            extractPhotoUrl(doc, url, item);
            if (item.getPhotoUrl() == null) {
                logger.info("Could not find photo URL for URL: {}", url);
            }
            
            items.add(item);
            logger.info("Scraped item from Agrofy: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping Agrofy URL: {}", url, e);
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            item.setStatus("Error");
            extractDataFromUrl(url, item);
            items.add(item);
        }
        
        return items;
    }

    private void extractPhotoUrl(Document doc, String url, MachineryItem item) {
        Element photoElement = doc.selectFirst(".ad-image img, .main-image img, .carousel-item img, .gallery-image img");
        if (photoElement != null) {
            String photoUrl = photoElement.attr("src");
            if (photoUrl.isEmpty()) {
                photoUrl = photoElement.attr("data-src");
            }
            if (!photoUrl.isEmpty()) {
                if (!photoUrl.startsWith("http")) {
                    photoUrl = BASE_URL + photoUrl;
                }
                item.setPhotoUrl(photoUrl);
                return;
            }
        }
        
        Element metaImage = doc.selectFirst("meta[property=og:image]");
        if (metaImage != null) {
            String content = metaImage.attr("content");
            if (!content.isEmpty()) {
                item.setPhotoUrl(content);
                return;
            }
        }
        
        Elements contentImages = doc.select(".product-content img, .product-gallery img");
        if (!contentImages.isEmpty()) {
            Element firstImage = contentImages.first();
            String src = firstImage.attr("src");
            if (!src.isEmpty()) {
                item.setPhotoUrl(src);
                return;
            }
        }
        
        Elements elementsWithBgImage = doc.select("[style*=background-image]");
        if (!elementsWithBgImage.isEmpty()) {
            String style = elementsWithBgImage.first().attr("style");
            Pattern pattern = Pattern.compile("background-image:\\s*url\\(['\"]?(.*?)['\"]?\\)");
            Matcher matcher = pattern.matcher(style);
            if (matcher.find()) {
                item.setPhotoUrl(matcher.group(1));
            }
        }
    }
    
    private void extractDataFromUrl(String url, MachineryItem item) {
        Pattern modelPattern = Pattern.compile("/trator-([a-zA-Z-]+)-([a-zA-Z0-9-]+)");
        Matcher modelMatcher = modelPattern.matcher(url);
        
        if (modelMatcher.find()) {
            String make = modelMatcher.group(1).replace("-", " ");
            String model = modelMatcher.group(2).replace("-", " ");
            
            item.setModel("Tractor " + make + " " + model);
            item.setMake(make.toUpperCase());
            item.setContractType("Sale");
        } else {
            logger.info("Could not extract model/make from URL: {}", url);
        }
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}