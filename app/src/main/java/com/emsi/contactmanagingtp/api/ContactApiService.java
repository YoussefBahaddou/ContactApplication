package com.emsi.contactmanagingtp.api;

import com.emsi.contactmanagingtp.model.ApiResponse;
import com.emsi.contactmanagingtp.model.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ContactApiService {
    // Make sure this matches your PHP endpoint exactly
    @POST("contacts/create.php")
    Call<ApiResponse> createContact(@Body Contact contact);
    
    @GET("contacts/read.php")
    Call<List<Contact>> getAllContacts();
}
