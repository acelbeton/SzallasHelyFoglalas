package com.example.szallashelyfoglalas.model;

import org.threeten.bp.LocalDate;

public class Booking {
    private String bookingId;
    private String userId;
    private String propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

    private int guestCount;

    public Booking() {
    }

    public Booking(String bookingId, String userId, String propertyId, LocalDate startDate, LocalDate endDate, double totalPrice, int guestCount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.propertyId = propertyId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.guestCount = guestCount;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
