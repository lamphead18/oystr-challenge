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
public class MercadoMaquinasScraperService implements WebScraperService {
    
    private static final Logger logger = LoggerFactory.getLogger(MercadoMaquinasScraperService.class);
    private static final String WEBSITE_NAME = "MercadoMaquinas";
    private static final String BASE_URL = "https://www.mercadomaquinas.com.br";
    
    @Override
    public List<MachineryItem> scrapePage(String url) {
        List<MachineryItem> items = new ArrayList<>();
        
        try {
            logger.info("Scraping MercadoMaquinas URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .followRedirects(true)
                    .get();
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            
            boolean isExpired = doc.select(".expired-notice, .sold-notice, .unavailable-notice").size() > 0;
            
            Elements deactivatedMessages = doc.getElementsContainingText("Anúncio desativado");
            Elements soldMessages = doc.getElementsContainingText("já foi vendido");
            
            if (!deactivatedMessages.isEmpty()) {
                logger.info("Ad is deactivated: {}", url);
                item.setStatus("Inactive");
                extractDataFromUrl(url, item);
                items.add(item);
                return items;
            } else if (!soldMessages.isEmpty()) {
                logger.info("Item has been sold: {}", url);
                item.setStatus("Sold");
                extractDataFromUrl(url, item);
                items.add(item);
                return items;
            } else if (isExpired) {
                logger.info("Ad is expired: {}", url);
                item.setStatus("Expired");
                extractDataFromUrl(url, item);
                items.add(item);
                return items;
            } else {
                item.setStatus("Active");
            }
            
            Element titleElement = doc.selectFirst("h1.ad-title, .product-title");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                item.setModel(fullTitle);
                
                Pattern pattern = Pattern.compile("([A-Za-z\\s]+)\\s+([A-Za-z]+)\\s+([A-Za-z0-9-]+)\\s+(\\d{4})");
                Matcher matcher = pattern.matcher(fullTitle);
                
                if (matcher.find()) {
                    item.setModel(matcher.group(3));
                    item.setMake(matcher.group(2));
                    item.setYear(matcher.group(4));
                }
            } else {
                logger.info("Could not find title element for URL: {}", url);
                extractDataFromUrl(url, item);
            }
            
            item.setContractType("Sale");

            Element hoursElement = doc.selectFirst(".ad-info-item:contains(Horas), .product-hours");
            if (hoursElement != null) {
                String hoursText = hoursElement.text().replaceAll("[^0-9]", "");
                if (!hoursText.isEmpty()) {
                    item.setWorkedHours(hoursText);
                }
            } else {
                logger.info("Could not find worked hours element for URL: {}", url);
            }
            
            Element locationElement = doc.selectFirst(".ad-location, .product-location");
            if (locationElement != null) {
                item.setCity(locationElement.text().trim());
            } else if (item.getCity() == null) {
                logger.info("Could not find location element for URL: {}", url);
                Pattern locationPattern = Pattern.compile("-(\\w+)-(\\w{2})$");
                Matcher locationMatcher = locationPattern.matcher(url);
                if (locationMatcher.find()) {
                    item.setCity(locationMatcher.group(1) + ", " + locationMatcher.group(2).toUpperCase());
                }
            }
            
            Element priceElement = doc.selectFirst(".ad-price, .product-price");
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
            logger.info("Scraped item from MercadoMaquinas: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping MercadoMaquinas URL: {}", url, e);
            
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
        
        Elements contentImages = doc.select(".ad-gallery img, .product-gallery img");
        if (!contentImages.isEmpty()) {
            Element firstImage = contentImages.first();
            String src = firstImage.attr("src");
            if (!src.isEmpty()) {
                if (!src.startsWith("http")) {
                    src = BASE_URL + src;
                }
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
                String bgImage = matcher.group(1);
                if (!bgImage.startsWith("http")) {
                    bgImage = BASE_URL + bgImage;
                }
                item.setPhotoUrl(bgImage);
            }
        }
    }
    
    private void extractDataFromUrl(String url, MachineryItem item) {
        Pattern modelPattern = Pattern.compile("/(\\d+)-([a-zA-Z-]+)-([a-zA-Z-]+)-([a-zA-Z0-9-]+)-(\\d{4})-([a-zA-Z-]+)-([a-zA-Z]{2})$");
        Matcher modelMatcher = modelPattern.matcher(url);
        
        if (modelMatcher.find()) {
            String type = modelMatcher.group(2).replace("-", " ");
            String make = modelMatcher.group(3).replace("-", " ");
            String model = modelMatcher.group(4).replace("-", " ");
            String year = modelMatcher.group(5);
            String city = modelMatcher.group(6).replace("-", " ");
            String state = modelMatcher.group(7).toUpperCase();
            
            item.setModel(type + " " + make + " " + model);
            item.setMake(make.toUpperCase());
            item.setYear(year);
            item.setCity(city + ", " + state);
            item.setContractType("Sale");
        } else {
            logger.info("Could not extract data from URL: {}", url);
        }
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}