package se.systementor.model;

import java.time.LocalDateTime;

public class Statistics {
    private LocalDateTime firstOrderDateTime;
    private LocalDateTime lastOrderDateTime;
    private float totalSaleInclVat;
    private float totalVat;
    private int totalNoOfReceipt;

    public LocalDateTime getFirstOrderDateTime() {
        return firstOrderDateTime;
    }

    public void setFirstOrderDateTime(LocalDateTime firstOrderDateTime) {
        this.firstOrderDateTime = firstOrderDateTime;
    }

    public LocalDateTime getLastOrderDateTime() {
        return lastOrderDateTime;
    }

    public void setLastOrderDateTime(LocalDateTime lastOrderDateTime) {
        this.lastOrderDateTime = lastOrderDateTime;
    }

    public float getTotalSaleInclVat() {
        return totalSaleInclVat;
    }

    public void setTotalSaleInclVat(float totalSaleInclVat) {
        this.totalSaleInclVat = totalSaleInclVat;
    }

    public float getTotalVat() {
        return totalVat;
    }

    public void setTotalVat(float totalVat) {
        this.totalVat = totalVat;
    }

    public int getTotalNoOfReceipt() {
        return totalNoOfReceipt;
    }

    public void setTotalNoOfReceipt(int totalNoOfReceipt) {
        this.totalNoOfReceipt = totalNoOfReceipt;
    }
}
