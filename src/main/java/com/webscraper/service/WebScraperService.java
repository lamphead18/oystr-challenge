package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import java.util.List;

public interface WebScraperService {
    
    List<MachineryItem> scrapePage(String url);
    
    String getWebsiteName();
}