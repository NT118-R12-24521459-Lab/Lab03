package com.example.bt1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "contactsManager";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE = "contacts";

    public DatabaseHandler(Context context) {
        this(context, DATABASE_NAME);
    }

    DatabaseHandler(Context context, String databaseName) {
        super(context.getApplicationContext(), databaseName, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, phone_number TEXT NOT NULL)");
        insertInitialContacts(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.delete(TABLE, "name = ? AND phone_number = ?",
                    new String[]{"Liên hệ mẫu 01", "0900000001"});
            db.delete(TABLE, "name = ? AND phone_number = ?",
                    new String[]{"Liên hệ mẫu 02", "0900000002"});
        }
        if (oldVersion < 3) insertInitialContacts(db);
    }

    private void insertInitialContacts(SQLiteDatabase db) {
        insert(db, new Contact("Ravi", "9100000000"));
        insert(db, new Contact("Srinivas", "9199999999"));
        insert(db, new Contact("Tommy", "9522222222"));
        insert(db, new Contact("Karthik", "9533333333"));
        insert(db, new Contact("Ravi", "9100000000"));
        insert(db, new Contact("Srinivas", "9199999999"));
        insert(db, new Contact("Tommy", "9522222222"));
        insert(db, new Contact("Karthik", "9533333333"));
    }

    private ContentValues values(Contact contact) {
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phone_number", contact.getPhoneNumber());
        return values;
    }

    private long insert(SQLiteDatabase db, Contact contact) {
        return db.insertOrThrow(TABLE, null, values(contact));
    }

    public long addContact(Contact contact) { return insert(getWritableDatabase(), contact); }

    public Contact getContact(int id) {
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readContact(cursor) : null;
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, null, null,
                null, null, "id ASC")) {
            while (cursor.moveToNext()) contacts.add(readContact(cursor));
        }
        return contacts;
    }

    public int updateContact(Contact contact) {
        return getWritableDatabase().update(TABLE, values(contact), "id = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public int deleteContact(Contact contact) {
        return getWritableDatabase().delete(TABLE, "id = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    private Contact readContact(Cursor cursor) {
        return new Contact(cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
    }
}
