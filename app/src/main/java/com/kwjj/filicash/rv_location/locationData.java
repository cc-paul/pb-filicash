package com.kwjj.filicash.rv_location;

public class locationData {
    int id;
    String location;
    Double price;

    public locationData(int id, String location, Double price) {
        this.id = id;
        this.location = location;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
