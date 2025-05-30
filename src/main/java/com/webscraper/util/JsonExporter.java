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

@Component
public class JsonExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonExporter.class);
    
    public boolean exportToJson(List<MachineryItem> items, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            objectMapper.writeValue(new File(filePath), items);
            
            exportGroupedJson(items, filePath);
            
            logger.info("Successfully exported {} items to JSON: {}", items.size(), filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting data to JSON: {}", filePath, e);
            return false;
        }
    }
    
    private boolean exportGroupedJson(List<MachineryItem> items, String filePath) {
        try {
            Map<String, List<MachineryItem>> groupedItems = items.stream()
                    .collect(Collectors.groupingBy(MachineryItem::getSourceWebsite));
            
            Map<String, Object> result = new HashMap<>();
            result.put("websites", groupedItems);
            result.put("totalItems", items.size());
            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String groupedFilePath = filePath.replace(".json", "_grouped.json");
            
            objectMapper.writeValue(new File(groupedFilePath), result);
            
            logger.info("Successfully exported grouped items to JSON: {}", groupedFilePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting grouped data to JSON", e);
            return false;
        }
    }
}