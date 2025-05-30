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
@Component
public class DataExporter {
    
    private static final Logger logger = LoggerFactory.getLogger(DataExporter.class);
    
    public boolean exportToCsv(List<MachineryItem> items, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Model,Contract Type,Make,Year,Worked Hours,City,Price,Photo URL,Source Website\n");
                
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
    
    private String escapeField(String field) {
        if (field == null) {
            return "";
        }
        
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        
        return field;
    }
}