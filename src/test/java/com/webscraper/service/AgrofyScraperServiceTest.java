package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.impl.AgrofyScraperService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AgrofyScraperService class.
 */
@ExtendWith(MockitoExtension.class)
public class AgrofyScraperServiceTest {

    @InjectMocks
    private AgrofyScraperService scraperService;

    private Document mockDocument;
    private Connection mockConnection;
    private Element mockTitleElement;
    private Element mockYearElement;
    private Element mockHoursElement;
    private Element mockLocationElement;
    private Element mockPriceElement;
    private Element mockPhotoElement;

    @BeforeEach
    public void setUp() {
        mockDocument = Mockito.mock(Document.class);
        mockConnection = Mockito.mock(Connection.class);
        mockTitleElement = Mockito.mock(Element.class);
        mockYearElement = Mockito.mock(Element.class);
        mockHoursElement = Mockito.mock(Element.class);
        mockLocationElement = Mockito.mock(Element.class);
        mockPriceElement = Mockito.mock(Element.class);
        mockPhotoElement = Mockito.mock(Element.class);
    }

    @Test
    public void testGetWebsiteName() {
        // Act
        String websiteName = scraperService.getWebsiteName();
        
        // Assert
        assertEquals("Agrofy", websiteName);
    }
}