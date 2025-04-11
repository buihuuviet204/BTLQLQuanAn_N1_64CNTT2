package com.example.qunnbnhyn;

public class Customer {
    private String id;
    private String name;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String idCard;
    private int visitCount;
    private int points;

    public Customer() {
    }

    public Customer(String name, String dateOfBirth, String phoneNumber, String address, String idCard) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.idCard = idCard;
        this.visitCount = 0;
        this.points = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}