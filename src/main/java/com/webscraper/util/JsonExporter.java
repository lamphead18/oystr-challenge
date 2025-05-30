package com.webscraper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.webscraper.model.MachineryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for exporting scraped data to JSON format.
 */
@Component
public class JsonExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonExporter.class);
    
    /**
     * Exports machinery items to a JSON file.
     * 
     * @param items List of machinery items to export
     * @param filePath Path to the output file
     * @return true if export was successful, false otherwise
     */
    public boolean exportToJson(List<MachineryItem> items, String filePath) {
        try {
            // Create directories if they don't exist
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            // Configure object mapper for pretty printing
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            // Write data to JSON file
            objectMapper.writeValue(new File(filePath), items);
            
            // Also create a grouped JSON file by website
            exportGroupedJson(items, filePath);
            
            logger.info("Successfully exported {} items to JSON: {}", items.size(), filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting data to JSON: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Exports machinery items grouped by website to a JSON file.
     * 
     * @param items List of machinery items to export
     * @param filePath Path to the output file
     * @return true if export was successful, false otherwise
     */
    private boolean exportGroupedJson(List<MachineryItem> items, String filePath) {
        try {
            // Group items by website
            Map<String, List<MachineryItem>> groupedItems = items.stream()
                    .collect(Collectors.groupingBy(MachineryItem::getSourceWebsite));
            
            // Create a map with website names as keys and items as values
            Map<String, Object> result = new HashMap<>();
            result.put("websites", groupedItems);
            result.put("totalItems", items.size());
            
            // Configure object mapper for pretty printing
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            // Create a new file path for the grouped JSON
            String groupedFilePath = filePath.replace(".json", "_grouped.json");
            
            // Write data to JSON file
            objectMapper.writeValue(new File(groupedFilePath), result);
            
            logger.info("Successfully exported grouped items to JSON: {}", groupedFilePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting grouped data to JSON", e);
            return false;
        }
    }
}