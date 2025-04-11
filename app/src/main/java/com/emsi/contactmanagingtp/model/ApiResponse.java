package com.emsi.contactmanagingtp.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("number")
    private String number;
    
    // Getters
    public String getMessage() {
        return message;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getNumber() {
        return number;
    }
}
