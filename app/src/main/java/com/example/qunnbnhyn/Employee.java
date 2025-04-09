package com.example.qunnbnhyn;
public class Employee {
    private String id;
    private String name;
    private String shift;

    public Employee() {
        // Constructor mặc định cho Firebase
    }

    public Employee(String id, String name, String shift) {
        this.id = id;
        this.name = name;
        this.shift = shift;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
