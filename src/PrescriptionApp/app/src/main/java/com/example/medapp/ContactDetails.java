package com.example.medapp;

/**
 * Class that represents a contact in the user's Google Contacts
 */
public class ContactDetails {

    private String id;
    private String name;
    private String email;
    private boolean selected;

    public ContactDetails(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.selected = false;
    }

    public ContactDetails(String id, String name, String email, boolean selected) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.selected = selected;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
