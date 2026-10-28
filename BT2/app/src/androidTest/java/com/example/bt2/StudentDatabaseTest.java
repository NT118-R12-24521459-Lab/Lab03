package com.example.bt2;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StudentDatabaseTest {
    private static final String TEST_DB = "student_instrumentation_test.db";
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

    @Test public void ownerIsSeeded() {
        Student owner = database.getStudent("24521459");
        assertNotNull(owner);
        assertEquals("Võ Minh Quân", owner.getName());
        assertEquals(1, database.getAllStudents().size());
    }

    @Test public void crudAndDuplicateKeyAreDurable() {
        Student initial = new Student("TEST01", "Quân O'Connor");
        assertTrue(database.addStudent(initial));
        assertFalse(database.addStudent(new Student("TEST01", "Không ghi đè")));
        assertEquals("Quân O'Connor", database.getStudent("TEST01").getName());
        assertEquals(1, database.updateStudent(new Student("TEST01", "Võ Minh Quân")));
        database.close();
        database = new DatabaseHandler(context, TEST_DB);
        Student updated = database.getStudent("TEST01");
        assertEquals("Võ Minh Quân", updated.getName());
        assertEquals(1, database.deleteStudent("TEST01"));
        assertNull(database.getStudent("TEST01"));
        assertEquals(0, database.updateStudent(initial));
    }

    @Test public void parameterizedIdsDoNotMatchOtherRows() {
        assertNull(database.getStudent("' OR 1=1 --"));
        assertEquals(0, database.deleteStudent("' OR 1=1 --"));
        assertEquals(1, database.getAllStudents().size());
    }

    @Test public void deletingEverythingDoesNotRestoreSamplesOnNextLaunch() {
        for (Student student : database.getAllStudents()) database.deleteStudent(student.getId());
        database.close();
        database = new DatabaseHandler(context, TEST_DB);
        assertTrue(database.getAllStudents().isEmpty());
    }
}
