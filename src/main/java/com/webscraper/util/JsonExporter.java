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
import java.util.List;

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
            
            logger.info("Successfully exported {} items to JSON: {}", items.size(), filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting data to JSON: {}", filePath, e);
            return false;
        }
    }
}