package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.impl.TratoresEColheitadeirasScraperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class TratoresEColheitadeirasScraperServiceTest {

    @InjectMocks
    private TratoresEColheitadeirasScraperService scraperService;

    @Test
    public void testGetWebsiteName() {
        String websiteName = scraperService.getWebsiteName();
        
        assertEquals("TratoresEColheitadeiras", websiteName);
    }
}