package com.example.szallashelyfoglalas.model;

import java.util.Map;

public class Property {
    private String propertyId;
    private String ownerId;
    private Map<String, String> address;
    private double pricePerNight;
    private int maxGuests;
    private String type; // e.g., Apartment, House, Cabin
    private String imageId; // ID of the image associated with the property

    public Property() {
    }

    public Property(String propertyId, String ownerId, Map<String, String> address, double pricePerNight, int maxGuests, String type, String imageId) {
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.type = type;
        this.imageId = imageId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public void setAddress(Map<String, String> address) {
        this.address = address;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return type + " in " + address.get("city") + ", " + address.get("street") +
                " for " + maxGuests + " guests at $" + pricePerNight + " per night";
    }
}
