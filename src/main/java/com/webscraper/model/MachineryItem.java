package com.webscraper.model;

public class MachineryItem {
    private String model;
    private String contractType;
    private String make;
    private String year;
    private String workedHours;
    private String city;
    private String price;
    private String photoUrl;
    private String sourceWebsite;
    private String status;

    public MachineryItem() {
    }

    public MachineryItem(String model, String contractType, String make, String year, 
                         String workedHours, String city, String price, String photoUrl, 
                         String sourceWebsite, String status) {
        this.model = model;
        this.contractType = contractType;
        this.make = make;
        this.year = year;
        this.workedHours = workedHours;
        this.city = city;
        this.price = price;
        this.photoUrl = photoUrl;
        this.sourceWebsite = sourceWebsite;
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(String workedHours) {
        this.workedHours = workedHours;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSourceWebsite() {
        return sourceWebsite;
    }

    public void setSourceWebsite(String sourceWebsite) {
        this.sourceWebsite = sourceWebsite;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MachineryItem{" +
                "model='" + model + '\'' +
                ", contractType='" + contractType + '\'' +
                ", make='" + make + '\'' +
                ", year='" + year + '\'' +
                ", workedHours='" + workedHours + '\'' +
                ", city='" + city + '\'' +
                ", price='" + price + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", sourceWebsite='" + sourceWebsite + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}