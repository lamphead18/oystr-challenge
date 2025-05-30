package com.webscraper.util;

import com.webscraper.model.MachineryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for exporting scraped data to various formats.
 */
@Component
public class DataExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(DataExporter.class);
    
    /**
     * Exports machinery items to a CSV file.
     * 
     * @param items List of machinery items to export
     * @param filePath Path to the output file
     * @return true if export was successful, false otherwise
     */
    public boolean exportToCsv(List<MachineryItem> items, String filePath) {
        try {
            // Create directories if they don't exist
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // Write header
                writer.write("Model,Contract Type,Make,Year,Worked Hours,City,Price,Photo URL,Source Website\n");
                
                // Write data
                for (MachineryItem item : items) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            escapeField(item.getModel()),
                            escapeField(item.getContractType()),
                            escapeField(item.getMake()),
                            escapeField(item.getYear()),
                            escapeField(item.getWorkedHours()),
                            escapeField(item.getCity()),
                            escapeField(item.getPrice()),
                            escapeField(item.getPhotoUrl()),
                            escapeField(item.getSourceWebsite())
                    ));
                }
            }
            
            logger.info("Successfully exported {} items to CSV: {}", items.size(), filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error exporting data to CSV: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Escapes a field for CSV output.
     * 
     * @param field The field to escape
     * @return The escaped field
     */
    private String escapeField(String field) {
        if (field == null) {
            return "";
        }
        
        // If the field contains commas, quotes, or newlines, wrap it in quotes and escape any quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        
        return field;
    }
}