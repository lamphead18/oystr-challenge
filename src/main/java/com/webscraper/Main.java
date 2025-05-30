package com.webscraper;

import com.webscraper.config.AppConfig;
import com.webscraper.model.MachineryItem;
import com.webscraper.service.ScraperManager;
import com.webscraper.util.JsonExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("Starting Agricultural Machinery Web Scraper");
        
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ScraperManager scraperManager = context.getBean(ScraperManager.class);
            JsonExporter jsonExporter = context.getBean(JsonExporter.class);
            
            Map<String, List<String>> urlsMap = new HashMap<>();
            
            List<String> agrofyUrls = new ArrayList<>();
            agrofyUrls.add("https://www.agrofy.com.br/trator-john-deere-7230j-oferta.html");
            agrofyUrls.add("https://www.agrofy.com.br/trator-case-puma-215-193793.html");
            urlsMap.put("Agrofy", agrofyUrls);
            
            List<String> tratoresUrls = new ArrayList<>();
            tratoresUrls.add("https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/flexer-xs-45/2023/45-pes/draper/triamaq-tratores/1028839");
            tratoresUrls.add("https://www.tratoresecolheitadeiras.com.br/veiculo/uberlandia/mg/plataforma-colheitadeira/gts/produttiva-1250/2022/caracol/12-linhas/triamaq-tratores/994257");
            urlsMap.put("TratoresEColheitadeiras", tratoresUrls);
            
            List<String> mercadoUrls = new ArrayList<>();
            mercadoUrls.add("https://www.mercadomaquinas.com.br/anuncio/236624-retro-escavadeira-caterpillar-416e-2015-carlopolis-pr");
            mercadoUrls.add("https://www.mercadomaquinas.com.br/anuncio/236623-mini-escavadeira-bobcat-e27z-2019-sete-lagoas-mg");
            urlsMap.put("MercadoMaquinas", mercadoUrls);
            
            logger.info("Starting to scrape all websites");
            List<MachineryItem> allItems = scraperManager.scrapeAllWebsites(urlsMap);
            logger.info("Finished scraping. Total items found: {}", allItems.size());
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputFile = "output/machinery_data_" + timestamp + ".json";
            boolean exportSuccess = jsonExporter.exportToJson(allItems, outputFile);
            
            if (exportSuccess) {
                logger.info("Data successfully exported to JSON: {}", outputFile);
            } else {
                logger.error("Failed to export data to JSON");
            }
            
            System.out.println("\n===== SCRAPING SUMMARY =====");
            System.out.println("Total items scraped: " + allItems.size());
            System.out.println("Output file: " + outputFile);
            System.out.println("===========================\n");
            
            if (!allItems.isEmpty()) {
                System.out.println("Sample data (first item):");
                System.out.println(allItems.get(0));
            }
        }
        
        logger.info("Agricultural Machinery Web Scraper completed");
    }
}