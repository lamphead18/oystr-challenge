package com.webscraper.service;

import com.webscraper.model.MachineryItem;
import com.webscraper.service.impl.AgrofyScraperService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    public void testScrapePage() throws IOException {
        // Arrange
        String testUrl = "https://www.agrofy.com.br/trator-john-deere-7230j-oferta.html";
        
        try (MockedStatic<Jsoup> jsoupMockedStatic = Mockito.mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
            when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            
            // Mock the elements
            when(mockDocument.selectFirst("h1.title")).thenReturn(mockTitleElement);
            when(mockTitleElement.text()).thenReturn("JOHN DEERE 7230J");
            
            when(mockDocument.selectFirst(".specs-item:contains(Año) .specs-item-value")).thenReturn(mockYearElement);
            when(mockYearElement.text()).thenReturn("2020");
            
            when(mockDocument.selectFirst(".specs-item:contains(Horas) .specs-item-value")).thenReturn(mockHoursElement);
            when(mockHoursElement.text()).thenReturn("1500");
            
            when(mockDocument.selectFirst(".location")).thenReturn(mockLocationElement);
            when(mockLocationElement.text()).thenReturn("São Paulo, SP");
            
            when(mockDocument.selectFirst(".price-value")).thenReturn(mockPriceElement);
            when(mockPriceElement.text()).thenReturn("R$ 450.000,00");
            
            when(mockDocument.selectFirst(".product-image img")).thenReturn(mockPhotoElement);
            when(mockPhotoElement.attr("src")).thenReturn("https://example.com/tractor.jpg");
            
            // Act
            List<MachineryItem> result = scraperService.scrapePage(testUrl);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            
            MachineryItem item = result.get(0);
            assertEquals("JOHN DEERE 7230J", item.getModel());
            assertEquals("JOHN", item.getMake());
            assertEquals("Sale", item.getContractType());
            assertEquals("2020", item.getYear());
            assertEquals("1500", item.getWorkedHours());
            assertEquals("São Paulo, SP", item.getCity());
            assertEquals("R$ 450.000,00", item.getPrice());
            assertEquals("https://example.com/tractor.jpg", item.getPhotoUrl());
            assertEquals("Agrofy", item.getSourceWebsite());
        }
    }
    
    @Test
    public void testGetWebsiteName() {
        // Act
        String websiteName = scraperService.getWebsiteName();
        
        // Assert
        assertEquals("Agrofy", websiteName);
    }
}