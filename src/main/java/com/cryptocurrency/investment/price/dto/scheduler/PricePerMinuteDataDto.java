package com.cryptocurrency.investment.price.dto.scheduler;

public class PricePerMinuteDataDto{
    private String curPrice;
    private String maxPrice;
    private String minPrice;

    public void resetValue(){
        this.curPrice = "0";
        this.maxPrice = "0";
        this.minPrice = "0";
    }
    public PricePerMinuteDataDto(String price) {
        this.curPrice = price;
        this.maxPrice = price;
        this.minPrice = price;
    }

    public String getMinPrice() {
        return minPrice;
    }
    public String getMaxPrice() {
        return maxPrice;
    }

    public String getCurPrice() { return curPrice; }

    public void setPrice(String value) {
        this.curPrice = value;
        double price = Double.parseDouble(value);
        double max = Double.parseDouble(this.maxPrice);
        double min = Double.parseDouble(this.minPrice);

        if (max < price) {
            this.maxPrice = value;
        }

        if (min > price || min == 0) {
            this.minPrice = value;
        }
    }
}