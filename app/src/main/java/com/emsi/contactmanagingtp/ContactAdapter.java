package com.emsi.contactmanagingtp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());

        // Set contact photo if available
        if (contact.getPhotoUri() != null && !contact.getPhotoUri().isEmpty()) {
            holder.photoImageView.setImageURI(Uri.parse(contact.getPhotoUri()));
        } else {
            holder.photoImageView.setImageResource(R.drawable.ic_person);
        }
        
        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            showContactActionDialog(contact);
        });
    }

    private void showContactActionDialog(Contact contact) {
        ContactActionDialog dialog = new ContactActionDialog(context, contact);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateContacts(List<Contact> contacts) {
        this.contactList = contacts;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;
        ImageView photoImageView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneTextView = itemView.findViewById(R.id.contact_phone);
            photoImageView = itemView.findViewById(R.id.contact_photo);
        }
    }
}
