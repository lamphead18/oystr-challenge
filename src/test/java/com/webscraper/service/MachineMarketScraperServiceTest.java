package com.webscraper.service;

import com.webscraper.service.impl.MachineMarketScraperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MachineMarketScraperServiceTest {

    @InjectMocks
    private MachineMarketScraperService scraperService;

    @Test
    public void testGetWebsiteName() {
        String websiteName = scraperService.getWebsiteName();
        
        assertEquals("MachineMarket", websiteName);
    }
}