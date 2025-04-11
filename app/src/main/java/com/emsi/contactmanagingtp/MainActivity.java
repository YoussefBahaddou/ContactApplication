package com.emsi.contactmanagingtp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 102;
    
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private ImageButton searchButton;
    private ImageButton clearButton;
    private TextView noResultsTextView;
    private List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.contacts_recycler_view);
        progressBar = findViewById(R.id.loading_progress);
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        clearButton = findViewById(R.id.clear_button);
        noResultsTextView = findViewById(R.id.no_results_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, contactList, noResultsTextView);
        recyclerView.setAdapter(adapter);

        // Set up search functionality
        setupSearchFunctionality();

        // Request all necessary permissions
        requestRequiredPermissions();
    }

    private void setupSearchFunctionality() {
        // Search button click listener
        searchButton.setOnClickListener(v -> {
            performSearch();
        });

        // Clear button click listener
        clearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            clearButton.setVisibility(View.GONE);
            adapter.getFilter().filter("");
        });

        // Search on enter key
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // Real-time search as user types and show/hide clear button
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter contacts as user types
                adapter.getFilter().filter(s);
                
                // Show/hide clear button
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        adapter.getFilter().filter(query);
    }

    private void requestRequiredPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        // Check for READ_CONTACTS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        
        // Check for CALL_PHONE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        
        // Check for SEND_SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // All permissions are already granted
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            // Check if READ_CONTACTS permission was granted
            boolean contactsPermissionGranted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_CONTACTS) && 
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    contactsPermissionGranted = true;
                    break;
                }
            }
            
            if (contactsPermissionGranted) {
                loadContacts();
            } else {
                Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        progressBar.setVisibility(View.VISIBLE);
        
        new Thread(() -> {
            List<Contact> contacts = getContacts();
            
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.updateContacts(contacts);
            });
        }).start();
    }

    private List<Contact> getContacts() {
        List<Contact> contactsList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        
        Cursor cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    );

    if (cursor != null && cursor.getCount() > 0) {
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            
            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        
                        contactsList.add(new Contact(name, phoneNumber, photoUri));
                        break; // Just get the first phone number
                    }
                    phoneCursor.close();
                }
            }
        }
        cursor.close();
    }
    
    return contactsList;
}
}
