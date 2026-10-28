package com.example.bt1;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ContactDatabaseTest {
    private static final String TEST_DB = "contact_instrumentation_test.db";
    private Context context;
    private DatabaseHandler database;

    @Before public void open() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(TEST_DB);
        database = new DatabaseHandler(context, TEST_DB);
    }

    @After public void clean() {
        database.close();
        context.deleteDatabase(TEST_DB);
    }

    @Test public void crudPreservesUnicodeQuotesAndLeadingZeroAfterReopening() {
        int id = (int) database.addContact(new Contact("Quân O'Connor", "0900123456"));
        assertTrue(id > 0);
        assertEquals("Quân O'Connor", database.getContact(id).getName());
        assertEquals("0900123456", database.getContact(id).getPhoneNumber());
        assertNull(database.getContact(-1));
        assertEquals(1, database.updateContact(new Contact(id, "Võ Minh Quân", "+84900123456")));
        database.close();
        database = new DatabaseHandler(context, TEST_DB);
        assertEquals("Võ Minh Quân", database.getContact(id).getName());
        assertEquals("+84900123456", database.getContact(id).getPhoneNumber());
        assertEquals(1, database.deleteContact(database.getContact(id)));
        assertNull(database.getContact(id));
        assertEquals(0, database.updateContact(new Contact(id, "Đã xóa", "0900000000")));
    }

    @Test public void deletingEverythingDoesNotRestoreSamplesOnNextLaunch() {
        assertEquals(8, database.getAllContacts().size());
        database.addContact(new Contact("Liên hệ thử 1", "0900000001"));
        database.addContact(new Contact("Liên hệ thử 2", "0900000002"));
        for (Contact contact : database.getAllContacts()) database.deleteContact(contact);
        database.close();
        database = new DatabaseHandler(context, TEST_DB);
        assertTrue(database.getAllContacts().isEmpty());
    }
}
