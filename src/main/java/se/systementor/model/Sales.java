package se.systementor.model;

import java.time.LocalDateTime;

public class Sales {
    private int id;
    private LocalDateTime dateTime;
    private float totalTax;
    private float totalSale;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public float getTotalTaxTax() {
        return totalTax;
    }

    public void setTotalTax(float totalTax) {
        this.totalTax= totalTax;
    }

    public float getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(float totalSale) {
        this.totalSale = totalSale;
    }
}
