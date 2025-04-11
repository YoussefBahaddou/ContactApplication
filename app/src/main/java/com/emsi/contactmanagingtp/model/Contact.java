package com.emsi.contactmanagingtp.model;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("number")
    private String number;
    
    // Constructor
    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNumber() {
        return number;
    }
    
    public void setNumber(String number) {
        this.number = number;
    }
}
