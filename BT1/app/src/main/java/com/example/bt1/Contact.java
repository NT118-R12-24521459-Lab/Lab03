package com.example.bt1;

/** One row in the contacts table. New contacts receive their ID from SQLite. */
public final class Contact {
    private final int id;
    private final String name;
    private final String phoneNumber;

    public Contact(String name, String phoneNumber) { this(0, name, phoneNumber); }
    public Contact(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
}
