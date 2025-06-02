package com.webscraper.service;

import com.webscraper.service.impl.TractorsAndHarvestersScraperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TractorsAndHarvestersScraperServiceTest {

    @InjectMocks
    private TractorsAndHarvestersScraperService scraperService;

    @Test
    public void testGetWebsiteName() {
        String websiteName = scraperService.getWebsiteName();
        
        assertEquals("TractorsAndHarvesters", websiteName);
    }
}