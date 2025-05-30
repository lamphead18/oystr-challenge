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
            
            boolean isExpired = doc.select(".expired-notice, .sold-notice, .unavailable-notice").size() > 0;
            
            Elements soldMessages = doc.getElementsContainingText("Esse veículo já foi vendido");
            if (!soldMessages.isEmpty()) {
                logger.info("Vehicle has been sold: {}", url);
                item.setStatus("Sold");
                extractDataFromUrl(url, item);
                
                Elements similarVehicles = doc.select(".similar-vehicles img, .related-vehicles img");
                if (!similarVehicles.isEmpty()) {
                    String imgSrc = similarVehicles.first().attr("src");
                    if (!imgSrc.isEmpty()) {
                        item.setPhotoUrl(imgSrc);
                    }
                }
                
                items.add(item);
                return items;
            } else if (isExpired) {
                logger.info("Ad is expired: {}", url);
                item.setStatus("Inactive");
                extractDataFromUrl(url, item);
                items.add(item);
                return items;
            } else {
                item.setStatus("Active");
            }
            
            Element titleElement = doc.selectFirst("h1.title-vehicle, .vehicle-title");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                item.setModel(fullTitle);
                
                String[] titleParts = fullTitle.split(" ");
                if (titleParts.length > 0) {
                    item.setMake(titleParts[0]);
                } else {
                    Pattern makePattern = Pattern.compile("/([a-zA-Z]+)/[0-9]+$");
                    Matcher makeMatcher = makePattern.matcher(url);
                    if (makeMatcher.find()) {
                        item.setMake(makeMatcher.group(1).toUpperCase());
                    }
                }
            } else {
                logger.info("Could not find title element for URL: {}", url);
                extractDataFromUrl(url, item);
            }
            
            Element contractElement = doc.selectFirst(".vehicle-info-item:contains(Tipo de anúncio)");
            if (contractElement != null) {
                String contractText = contractElement.text().toLowerCase();
                if (contractText.contains("venda")) {
                    item.setContractType("Sale");
                } else if (contractText.contains("aluguel")) {
                    item.setContractType("Rent");
                } else {
                    item.setContractType("Sale");
                }
            } else {
                item.setContractType("Sale");
                logger.info("Could not find contract type element for URL: {}", url);
            }
            
            Element yearElement = doc.selectFirst(".vehicle-info-item:contains(Ano), .vehicle-year");
            if (yearElement != null) {
                String yearText = yearElement.text().replaceAll("[^0-9]", "");
                if (!yearText.isEmpty()) {
                    item.setYear(yearText);
                }
            } else {
                logger.info("Could not find year element for URL: {}", url);
                Pattern yearPattern = Pattern.compile("/(20\\d{2})/");
                Matcher yearMatcher = yearPattern.matcher(url);
                if (yearMatcher.find()) {
                    item.setYear(yearMatcher.group(1));
                }
            }
            
            Element hoursElement = doc.selectFirst(".vehicle-info-item:contains(Horas), .vehicle-hours");
            if (hoursElement != null) {
                String hoursText = hoursElement.text().replaceAll("[^0-9]", "");
                if (!hoursText.isEmpty()) {
                    item.setWorkedHours(hoursText);
                }
            } else {
                logger.info("Could not find worked hours element for URL: {}", url);
            }
            
            Pattern locationPattern = Pattern.compile("/([a-zA-Z]+)/([a-zA-Z]{2})/");
            Matcher locationMatcher = locationPattern.matcher(url);
            if (locationMatcher.find()) {
                item.setCity(locationMatcher.group(1) + ", " + locationMatcher.group(2).toUpperCase());
            } else {
                logger.info("Could not extract city/location from URL: {}", url);
            }
            
            Element priceElement = doc.selectFirst(".vehicle-price, .price");
            if (priceElement != null) {
                item.setPrice(priceElement.text().trim());
            } else {
                logger.info("Could not find price element for URL: {}", url);
            }
            
            extractPhotoUrl(doc, item);
            if (item.getPhotoUrl() == null) {
                logger.info("Could not find photo URL for URL: {}", url);
            }
            
            if (item.getPhotoUrl() != null && item.getPhotoUrl().equals("{6}")) {
                logger.info("Found invalid photo URL '{6}' for URL: {}", url);
                item.setPhotoUrl(null);
                
                Elements allImages = doc.select("img[src*=veiculos]");
                if (!allImages.isEmpty()) {
                    item.setPhotoUrl(allImages.first().attr("src"));
                }
            }
            
            items.add(item);
            logger.info("Scraped item from TratoresEColheitadeiras: {}", item);
        } catch (IOException e) {
            logger.error("Error scraping TratoresEColheitadeiras URL: {}", url, e);
            
            MachineryItem item = new MachineryItem();
            item.setSourceWebsite(WEBSITE_NAME);
            item.setStatus("Error");
            extractDataFromUrl(url, item);
            items.add(item);
        }
        
        return items;
    }
    
    private void extractPhotoUrl(Document doc, MachineryItem item) {
        Element photoElement = doc.selectFirst(".vehicle-image img, .main-image img, .carousel-item img, .gallery-image img");
        if (photoElement != null) {
            String photoUrl = photoElement.attr("src");
            if (photoUrl.isEmpty()) {
                photoUrl = photoElement.attr("data-src");
            }
            if (!photoUrl.isEmpty()) {
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
        
        Elements contentImages = doc.select(".vehicle-gallery img, .vehicle-photos img");
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
        String[] urlParts = url.split("/");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].equalsIgnoreCase("plataforma-colheitadeira") && i+2 < urlParts.length) {
                item.setMake(urlParts[i+1].toUpperCase());
                item.setModel(urlParts[i+2]);
                break;
            }
        }
        
        Pattern yearPattern = Pattern.compile("/(20\\d{2})/");
        Matcher yearMatcher = yearPattern.matcher(url);
        if (yearMatcher.find()) {
            item.setYear(yearMatcher.group(1));
        }
        
        Pattern locationPattern = Pattern.compile("/([a-zA-Z]+)/([a-zA-Z]{2})/");
        Matcher locationMatcher = locationPattern.matcher(url);
        if (locationMatcher.find()) {
            item.setCity(locationMatcher.group(1) + ", " + locationMatcher.group(2).toUpperCase());
        }
        
        item.setContractType("Sale");
    }
    
    @Override
    public String getWebsiteName() {
        return WEBSITE_NAME;
    }
}