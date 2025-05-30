# Agricultural Machinery Web Scraper

This application fetches data from three agricultural machinery websites and collects information about machinery items.

## Features

- Scrapes data from three agricultural machinery websites:
  - Agrofy
  - TratoresEColheitadeiras
  - MercadoMaquinas
- Collects the following information for each machinery item:
  - Model
  - Contract type (rent or sale)
  - Make
  - Year
  - Worked hours
  - City
  - Price
  - Photo/Picture URL
- Exports data to CSV format
- Uses Spring for Dependency Injection
- Includes unit tests with JUnit and Mockito

## Requirements

- Java 11 or higher
- Maven

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── webscraper/
│               ├── config/
│               │   └── AppConfig.java
│               ├── model/
│               │   └── MachineryItem.java
│               ├── service/
│               │   ├── impl/
│               │   │   ├── AgrofyScraperService.java
│               │   │   ├── TratoresEColheitadeirasScraperService.java
│               │   │   └── MercadoMaquinasScraperService.java
│               │   ├── ScraperManager.java
│               │   └── WebScraperService.java
│               ├── util/
│               │   └── DataExporter.java
│               └── Main.java
└── test/
    └── java/
        └── com/
            └── webscraper/
                └── service/
                    └── AgrofyScraperServiceTest.java
```

## How to Build

```bash
mvn clean package
```

## How to Run

```bash
java -jar target/agricultural-machinery-scraper-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Output

The application will create a CSV file in the `output` directory with the scraped data. The filename includes a timestamp to avoid overwriting previous results.