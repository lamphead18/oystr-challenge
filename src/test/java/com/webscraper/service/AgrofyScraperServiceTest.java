package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.impl.AgrofyScraperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class AgrofyScraperServiceTest {

    @InjectMocks
    private AgrofyScraperService scraperService;

    @Test
    public void testGetWebsiteName() {
        String websiteName = scraperService.getWebsiteName();
        
        assertEquals("Agrofy", websiteName);
    }
}